package br.dcc.ufba.themoviefinder.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dcc.ufba.themoviefinder.entities.models.Movie;
import br.dcc.ufba.themoviefinder.entities.models.User;
import br.dcc.ufba.themoviefinder.entities.services.MovieService;
import br.dcc.ufba.themoviefinder.services.similarity.UserMovieSimilarity;
import br.dcc.ufba.themoviefinder.utils.ItemValue;

@Service
public class RecomendationService 
{
	private UserMovieSimilarity simService;
	
	@Autowired
	private MovieService movieService;
	
	private static final Logger LOGGER = LogManager.getLogger(RecomendationService.class);
	
	public void setUserMovieSimilarity(UserMovieSimilarity userMovieSimilarity)
	{
		simService = userMovieSimilarity;
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
		if(simService != null) {
			List<ItemValue<Movie>> simList = new ArrayList<ItemValue<Movie>>();
			simService.init();
			if(! (tokens.isEmpty() || movies.isEmpty())) {
				for (Movie movie : movies) {
					try {
						ItemValue<Movie> mv = new ItemValue<Movie>(movie, simService.getSimilarity(tokens, movie.getTokensList()), true);
						simList.add(mv);
						if(LOGGER.isDebugEnabled()) {
							LOGGER.debug(mv);
						}
					} catch(Exception e) {
						LOGGER.error(e.getMessage(), e);
					}
					if(LOGGER.isDebugEnabled()) {
						LOGGER.debug(simList.size() + " - " + simList.size() / ((double) movies.size()) * 100 + "%");
					}
				}
				simList.sort((ItemValue<Movie> a, ItemValue<Movie> b) -> a.compareTo(b));
			}
			int max = 0;
			if(qtMovies >= 0) {
				max = Math.min(qtMovies, simList.size());
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
