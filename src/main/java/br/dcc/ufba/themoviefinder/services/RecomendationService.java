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
import br.dcc.ufba.themoviefinder.entities.models.Recomendation;
import br.dcc.ufba.themoviefinder.entities.models.User;
import br.dcc.ufba.themoviefinder.entities.services.MovieService;
import br.dcc.ufba.themoviefinder.services.similarity.UserMovieSimilarityService;
import br.dcc.ufba.themoviefinder.utils.BatchWorkLoad;
import br.dcc.ufba.themoviefinder.utils.ItemValue;

@Service
public class RecomendationService 
{	
	private UserMovieSimilarityService similarityService;
	private static final StopWatch WATCH = new StopWatch();
	
	@Value("${app.recomendation-batch-size: 5}")
	public int batchSize;
	
	@Value("${app.recomendation-batch-movie-size: 250}")
	public int batchMovieSize;
	
	@Autowired
	private MovieService movieService;
	
	private RecomendationModel recModel;

	private static final Logger LOGGER = LogManager.getLogger(RecomendationService.class);
	
	public RecomendationService()
	{
		recModel = new RecomendationModel();
	}
	
	public void setUserMovieSimilarity(UserMovieSimilarityService similarityService)
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
	
	public RecomendationModel getRecModel() 
	{
		return recModel;
	}

	public void setRecModel(RecomendationModel recModel) 
	{
		this.recModel = recModel;
	}

	public List<ItemValue<Movie>> getRecomendationsByMovie(Movie movie)
	{
		return getRecomendations(movie.getTokensList(), Arrays.asList(movie));
	}
	
	public List<Recomendation> getRecomendationsByUser(User user)
	{
		return getRecomendations(user.getMovieTokens(), user.getMovies()).stream().map(recItem -> {
			return new Recomendation(user, recItem.item, similarityService.getType(), recItem.value);
		}).collect(Collectors.toList());
	}
	
	public List<Recomendation> getRecomendationsByUserBestTerms(User user)
	{
		List<String> bestTerms = user.getUserBestTerms(recModel);
		List<Movie> moviesRecModel =  user.getMovies();
		return getRecomendations(bestTerms, moviesRecModel).stream().map(recItem -> {
			return new Recomendation(user, recItem.item, similarityService.getType(), recItem.value);
		}).collect(Collectors.toList());
	}
	
	private List<ItemValue<Movie>> getRecomendations(List<String> userTokens, List<Movie> movies)
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
				addRecomendations(simList, userTokens, moviesPage.getContent(), totalMovies);
				for (int i = 1; i < qtPages; i++) {
					moviesPage = movieService.pageMoviesExcept(movieIds, moviesPage.nextPageable());
					addRecomendations(simList, userTokens, moviesPage.getContent(), totalMovies);
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
			int max = simList.size();
			if(recModel.recomendationSize > 0) {
				max = Math.min(recModel.recomendationSize, max);
			}
			return simList.subList(0, max);
		} else {
			throw new IllegalStateException("a userMovieSimilarity service must be setted");
		}
	}
	
	private void addRecomendations(List<ItemValue<Movie>> simList, List<String> userTokens, List<Movie> movies, long totalMovies)
	{
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
				}
				if(LOGGER.isDebugEnabled()) {
					LOGGER.debug(String.format("\n%s\n%s\n%s\n%d - %f%%, %fs\n", 
							mv, userTokens, movie.getTokensList(), simList.size(), simList.size() / ((double) totalMovies) * 100, ((double) WATCH.getTime() / 1000)));
				}
				return null;
			});
			similarityService.resetCache();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
}
