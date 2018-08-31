package br.dcc.ufba.semrecsys;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.dcc.ufba.semrecsys.models.Movie;
import br.dcc.ufba.semrecsys.models.User;
import br.dcc.ufba.semrecsys.services.MovieService;
import br.dcc.ufba.semrecsys.services.UserMovieSimilarityService;
import br.dcc.ufba.semrecsys.services.UserService;
import br.dcc.ufba.semrecsys.utils.MovieSimilarity;

@SpringBootApplication
public class App implements CommandLineRunner
{
	@Autowired
	private MovieService movieService;
	
	@Autowired
	private UserMovieSimilarityService simService;
	
	@Autowired
	private UserService userService;
	
	private static final Logger LOGGER = LogManager.getLogger(App.class);
	
	public static void main(String args[])
	{
		SpringApplication.run(App.class, args);
	}

	@Override
	public void run(String... args) throws Exception 
	{
		User user = userService.findByName("Lucas");
		//user.getMovies().add(movieService.findFirstByLikeTitle("The Matrix"));
		//userService.save(user);
		
		for (Movie movie : user.getMovies()) {
			System.out.println(movie.getTitle());
		}
		
		System.out.println("\nNormal Recomendations");
		List<MovieSimilarity> recomendations = userService.getRecomendations(user, 20, false);
		for (MovieSimilarity movieSimilarity : recomendations) {
			System.out.println(movieSimilarity.movie.getTitle());
		}
		
		System.out.println("\nRecomendations with DbPedia");
		recomendations = userService.getRecomendations(user, 20, true);
		for (MovieSimilarity movieSimilarity : recomendations) {
			System.out.println(movieSimilarity.movie.getTitle());
		}
		
		/*
		System.out.println(simService.getSimilarityFromUser(user, movieService.findFirstByTitle("GoldenEye")));
		simService.setExtendedMode(false);
		System.out.println(simService.getSimilarityFromUser(user, movieService.findFirstByTitle("GoldenEye")));
		simService.setExtendedMode(true);
		user.getMovies().clear();
		user.getMovies().add(movieService.findFirstByTitle("GoldenEye"));
		System.out.println(simService.getSimilarityFromUser(user, movieService.findFirstByTitle("GoldenEye")));
		*/
		
		
	}
	
	public void listMovies()
	{
		//movieService.updateMovieExtendedTokens();
		List<Movie> movies = Arrays.asList(movieService.getMovieById(4515));
		for (Movie movie : movies) 
		{
			System.out.println("ID: "+movie.getId());
			System.out.println("Title: "+movie.getTitle());
			System.out.println("Description: "+movie.getDescription());
			System.out.println(movie.getTokensList());
			System.out.println(movie.getExtendedTokensList());
			System.out.println(movie.getExtendedTokensList().size());
			System.out.println("");
		}
	}
}
