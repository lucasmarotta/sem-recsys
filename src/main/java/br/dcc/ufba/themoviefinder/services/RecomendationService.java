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
	
	@Autowired
	private MovieService movieService;
	
	private static final Logger LOGGER = LogManager.getLogger(RecomendationService.class);
	
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

	public List<ItemValue<Movie>> getRecomendationsByMovie(Movie movie, int qtMovies)
	{
		return pageAllRecomendations(movie.getTokensList(), Arrays.asList(movie), qtMovies);	
	}
	
	public List<ItemValue<Movie>> getRecomendationsByUser(User user, int qtMovies)
	{
		return pageAllRecomendations(user.getUserMovieTokens(), user.getMovies(), qtMovies);
	}
	
	public List<ItemValue<Movie>> getRecomendationsByUserBestTerms(User user, int qtMovies, int qtTerms)
	{
		return pageAllRecomendations(user.getUserBestTerms(qtTerms), user.getMovies(), qtMovies);
	}
	
	public void updateRecomendations(User user, int qtdMovies)
	{
		
	}
	
	private List<ItemValue<Movie>> pageAllRecomendations(List<String> tokens, List<Movie> movies, int qtMovies)
	{
		if(similarityService != null) {
			similarityService.init();
			List<ItemValue<Movie>> simList = new ArrayList<ItemValue<Movie>>();
			List<Integer> movieIds = movies.stream().map(movie -> {
				return movie.getId();
			}).collect(Collectors.toList());
			Pageable pageRequest = PageRequest.of(0, 500);
			try {
				Page<Movie> moviesPage = movieService.pageMoviesExcept(movieIds, pageRequest);
				int qtPages = moviesPage.getTotalPages();
				if(LOGGER.isDebugEnabled()) {
					WATCH.start();
				}
				simList.addAll(getRecomendations(tokens, moviesPage.getContent(), qtMovies));
				similarityService.reset();
				for (int i = 1; i < qtPages; i++) {
					moviesPage = movieService.pageMoviesExcept(movieIds, moviesPage.nextPageable());
					simList.addAll(getRecomendations(tokens, moviesPage.getContent(), qtMovies));
					similarityService.reset();
				}
			} catch(Exception e) {
				simList.clear();
				LOGGER.error(e.getMessage(), e);
			}
			if(LOGGER.isDebugEnabled()) {
				WATCH.stop();
			}
			simList.sort((ItemValue<Movie> a, ItemValue<Movie> b) -> a.compareTo(b));
			int max = simList.size();
			if(qtMovies > 0) {
				max = Math.min(qtMovies, max);
			}
			return simList.subList(0, max);	
		} else {
			throw new IllegalStateException("a userMovieSimilarity service must be setted");
		}
	}
	
	private List<ItemValue<Movie>> getRecomendations(List<String> tokens, List<Movie> movies, int qtMovies)
	{
		List<ItemValue<Movie>> simList = Collections.synchronizedList(new ArrayList<ItemValue<Movie>>());
		try {
			BatchWorkLoad<Movie> batchWorkLoad = new BatchWorkLoad<Movie>(batchSize, movies);
			batchWorkLoad.run(movie -> {
				String debug = "";
				if(LOGGER.isDebugEnabled()) {
					debug += "\n" + movie.getTitle();
					debug += "\n" + movie.getTokensList();
				}
				ItemValue<Movie> mv = null;
				try {
					mv = new ItemValue<Movie>(movie, similarityService.getSimilarity(tokens, movie.getTokensList()), true);
					simList.add(mv);
				} catch(Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
				if(LOGGER.isDebugEnabled()) {
					debug += "\n" + mv;
					debug += "\n" + String.format("%d - %f%%, %fs", simList.size(), simList.size() / ((double) movies.size()) * 100, ((double) WATCH.getTime() / 1000)) + "\n";
					LOGGER.debug(debug);
				}
				return null;
			});
		} catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
		}
		return simList;
	}
}
