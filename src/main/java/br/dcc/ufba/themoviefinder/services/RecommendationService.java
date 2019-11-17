package br.dcc.ufba.themoviefinder.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.dcc.ufba.themoviefinder.entities.models.Movie;
import br.dcc.ufba.themoviefinder.entities.models.Recommendation;
import br.dcc.ufba.themoviefinder.entities.models.User;
import br.dcc.ufba.themoviefinder.entities.services.MovieService;
import br.dcc.ufba.themoviefinder.services.similarity.SimilarityService;
import br.dcc.ufba.themoviefinder.utils.ItemValue;
import br.dcc.ufba.themoviefinder.utils.TFIDFCalculator;

@Service
public class RecommendationService 
{
	private SimilarityService similarityService;
	
	@Value("${app.recommendation-batch-size: 5}")
	public int batchSize;
	
	@Value("${app.recommendation-batch-movie-size: 250}")
	public int batchMovieSize;
	
	@Autowired
	private MovieService movieService;
	
	private RecommendationModel recModel;

	private static final StopWatch WATCH = new StopWatch();
	private static final Logger LOGGER = LogManager.getLogger(RecommendationService.class);
	
	public RecommendationService()
	{
		recModel = new RecommendationModel();
	}
	
	public void setUserMovieSimilarity(SimilarityService similarityService)
	{
		this.similarityService = similarityService;
	}
	
	public int getBatchSize() 
	{
		return batchSize;
	}

	public void setBatchSize(int batchSize) 
	{
		this.batchSize = batchSize;
	}
	
	public int getBatchMovieSize() 
	{
		return batchMovieSize;
	}

	public void setBatchMovieSize(int batchMovieSize) 
	{
		this.batchMovieSize = batchMovieSize;
	}
	
	public RecommendationModel getRecModel() 
	{
		return recModel;
	}

	public void setRecModel(RecommendationModel recModel)
	{
		this.recModel = recModel;
		similarityService = recModel.getServiceByType();
	}

	public List<ItemValue<Movie>> getRecommendationsByMovie(Movie movie)
	{
		return getRecommendations(movie.getTokensList(), Arrays.asList(movie));
	}
	
	public List<Recommendation> getRecommendationsByUser(User user)
	{
		return getRecommendations(user.getMovieTokens(), user.getMovies()).stream().map(recItem -> {
			return new Recommendation(user, recItem.item, similarityService.getType(), recItem.value);
		}).collect(Collectors.toList());
	}
	
	public List<Recommendation> getRecommendationsByUserBestTerms(User user)
	{
		List<String> bestTerms = user.getUserBestTerms(recModel);
		List<Movie> moviesRecModel =  user.getMovies();
		return getRecommendations(bestTerms, moviesRecModel).stream().map(recItem -> {
			return new Recommendation(user, recItem.item, similarityService.getType(), recItem.value);
		}).collect(Collectors.toList());
	}
	
	private List<ItemValue<Movie>> getRecommendations(List<String> userTokens, List<Movie> movies)
	{
		if(similarityService != null) {
			similarityService.init();
			List<ItemValue<Movie>> simList = Collections.synchronizedList(new ArrayList<ItemValue<Movie>>());
			List<Integer> movieIds = movies.stream().map(movie -> movie.getId()).collect(Collectors.toList());
			Pageable pageRequest = PageRequest.of(0, batchMovieSize);
			try {
				Page<Movie> moviesPage = movieService.pageMoviesExcept(movieIds, pageRequest);
				int qtPages = moviesPage.getTotalPages();
				long totalMovies = moviesPage.getTotalElements();
				if(LOGGER.isDebugEnabled()) {
					WATCH.start();
				}
				addRecommendations(simList, userTokens, moviesPage.getContent(), totalMovies);
				for (int i = 1; i < qtPages; i++) {
					moviesPage = movieService.pageMoviesExcept(movieIds, moviesPage.nextPageable());
					addRecommendations(simList, userTokens, moviesPage.getContent(), totalMovies);
				}
				if(LOGGER.isDebugEnabled()) {
					WATCH.stop();
					WATCH.reset();
				}
			} catch(Exception e) {
				simList.clear();
				LOGGER.error(e.getMessage(), e);
			}
			similarityService.close();
			Collections.sort(simList, Collections.reverseOrder());
			return simList.stream().limit(recModel.recomendationSize).collect(Collectors.toList());
		} else {
			throw new IllegalStateException("a userMovieSimilarity service must be setted");
		}
	}
	
	private void addRecommendations(List<ItemValue<Movie>> simList, List<String> userTokens, List<Movie> movies, long totalMovies)
	{
		/* 
		try {
			//similarityService.updateCache(tokens, movies);
			BatchWorkLoad<Movie> batchWorkLoad = new BatchWorkLoad<Movie>(batchSize, movies, false);
			batchWorkLoad.run(movie -> {
				ItemValue<Movie> mv = null;
				try {
					mv = new ItemValue<Movie>(movie, similarityService.getSimilarity(userTokens, movie.getTokensList()), recModel.randomEqualOrder);
					simList.add(mv);
				} catch(Exception e) {
					LOGGER.error(e.getMessage(), e);
				} finally {
					int size = simList.size();
					if(LOGGER.isDebugEnabled()) {
						if(mv != null) {
							LOGGER.debug(String.format("\n%s\n%s\n%s\n%d - %f%%, %fs\n", 
									mv, userTokens, TFIDFCalculator.uniqueValues(movie.getTokensList()), size, size / ((double) totalMovies) * 100, ((double) WATCH.getTime() / 1000)));	
						} else {
							LOGGER.debug(String.format("\n%d - %f%%, %fs\n", 
									size, size / ((double) totalMovies) * 100, ((double) WATCH.getTime() / 1000)));
						}
					}	
				}
				return null;
			});
			similarityService.resetCache();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		*/
		
		movies.parallelStream().forEach(movie -> {
			ItemValue<Movie> mv = null;
			try {
				mv = new ItemValue<Movie>(movie, similarityService.getSimilarity(userTokens, movie.getTokensList()), recModel.randomEqualOrder);
				simList.add(mv);
			} catch(Exception e) {
				LOGGER.error(e.getMessage(), e);
			} finally {
				int size = simList.size();
				if(LOGGER.isDebugEnabled()) {
					if(mv != null) {
						LOGGER.debug(String.format("\n%s\n%s\n%s\n%d - %f%%, %fs\n", 
								mv, userTokens, TFIDFCalculator.uniqueValues(movie.getTokensList()), size, size / ((double) totalMovies) * 100, ((double) WATCH.getTime() / 1000)));	
					} else {
						LOGGER.debug(String.format("\n%d - %f%%, %fs\n", 
								size, size / ((double) totalMovies) * 100, ((double) WATCH.getTime() / 1000)));
					}
				}	
			}
		});
		similarityService.resetCache();
	}
}
