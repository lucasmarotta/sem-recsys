package br.dcc.ufba.themoviefinder.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.dcc.ufba.themoviefinder.entities.models.Movie;
import br.dcc.ufba.themoviefinder.entities.models.User;
import br.dcc.ufba.themoviefinder.entities.services.MovieService;
import br.dcc.ufba.themoviefinder.services.similarity.UserMovieSimilarityService;
import br.dcc.ufba.themoviefinder.utils.ItemValue;

@Service
public class RecomendationService 
{	
	private UserMovieSimilarityService similarityService;
	
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
		return getRecomendations(movie.getTokensList(), movieService.getAllMoviesExcept(Arrays.asList(movie)), qtMovies);	
	}
	
	public List<ItemValue<Movie>> getRecomendationsByUser(User user, int qtMovies)
	{
		return getRecomendations(user.getUserMovieTokens(), movieService.getAllMoviesExcept(user.getMovies()), qtMovies);
	}
	
	public List<ItemValue<Movie>> getRecomendationsByUserBestTerms(User user, int qtMovies, int qtTerms)
	{
		return getRecomendations(user.getUserBestTerms(qtTerms), movieService.getAllMoviesExcept(user.getMovies()), qtMovies);
	}
	
	private List<ItemValue<Movie>> getRecomendations(List<String> tokens, List<Movie> movies, int qtMovies)
	{
		if(similarityService != null) {
			similarityService.init();
			List<ItemValue<Movie>> simList = new ArrayList<ItemValue<Movie>>();
			int mSize = movies.size();
			StopWatch watch = null;
			if(LOGGER.isDebugEnabled()) { 
				watch = new StopWatch();
				watch.start();	
			}
			for (int i = 0; i < mSize; i++) {
				Movie movie = movies.get(i);
				if(LOGGER.isDebugEnabled()) {
					System.out.println("");
					LOGGER.debug(movie.getTitle());
					LOGGER.debug(movie.getTokensList());
				}
				ItemValue<Movie> mv = null;
				try {
					mv = new ItemValue<Movie>(movie, similarityService.getSimilarity(tokens, movie.getTokensList()), true);
					simList.add(mv);
				} catch(Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
				if(LOGGER.isDebugEnabled()) {
					LOGGER.debug(i + " " + mv);
					LOGGER.debug(String.format("%d - %f%%, %ds", simList.size(), simList.size() / ((double) movies.size()) * 100, watch.getTime() / 1000));
				}
				similarityService.reset();
			}
			if(LOGGER.isDebugEnabled()) { 
				watch.stop();
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
	
	public void updateRecomendations(User user, int qtdMovies)
	{
		
	}
}
