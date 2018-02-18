package br.dcc.ufba.semrecsys;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.dcc.ufba.semrecsys.models.Movie;
import br.dcc.ufba.semrecsys.services.MovieService;
import br.dcc.ufba.semrecsys.services.MovieTokenizer;

public class App 
{
	private static final Logger LOGGER = LogManager.getLogger(App.class);
	
	public static void main(String args[])
	{
		MovieService movieService = new MovieService();
		List<Movie> movies = setMovieTokens(movieService.getMovies());
		//movieService.getResourceProperties(movies.get(0));
	}
	
	public static List<Movie> setMovieTokens(List<Movie> movies)
	{
		MovieTokenizer tokenizer = new MovieTokenizer();
		for (Movie movie : movies) {
			movie.setTokens(tokenizer.generateTokens(movie.getDescription()));
			System.out.println(movie.getTitle());
			System.out.println(movie.getTokens()+"\n");
		}
		return movies;
	}
}
