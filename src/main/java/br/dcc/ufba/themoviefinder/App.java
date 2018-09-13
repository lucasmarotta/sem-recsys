package br.dcc.ufba.themoviefinder;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import br.dcc.ufba.themoviefinder.models.User;
import br.dcc.ufba.themoviefinder.services.MovieService;
import br.dcc.ufba.themoviefinder.services.UserMovieRLWSimilarityService;
import br.dcc.ufba.themoviefinder.services.UserService;
import br.dcc.ufba.themoviefinder.utils.MovieSimilarity;

@SpringBootApplication
public class App implements CommandLineRunner
{
	@Autowired
	private MovieService movieService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserMovieRLWSimilarityService rlwSimilarityService; 
	
	@Autowired
	private ConfigurableApplicationContext context;
	
	private static final Logger LOGGER = LogManager.getLogger(App.class);
	
	public static void main(String args[])
	{
		SpringApplication.run(App.class, args);
	}

	@Override
	public void run(String... args) throws Exception 
	{	
		//Movie toyStory = movieService.findFirstByLikeTitle("Toy Story");
		//Movie matrix = movieService.findFirstByLikeTitle("Matrix");
		
		User user = userService.findByName("Lucas");
		userService.setUserMovieSimilarity(rlwSimilarityService);
		
		System.out.println(rlwSimilarityService.getSimilarityFromMovie(movieService.findFirstByTitle("Toy Story"), movieService.findFirstByTitle("Toy Story 2")));
		System.out.println(rlwSimilarityService.getSimilarityFromMovie(movieService.findFirstByTitle("Toy Story 2"), movieService.findFirstByTitle("Halloween II")));
		
		/*
		System.out.println("\nRecomendations with RLW Similarity");
		List<MovieSimilarity> recomendations = userService.getRecomendationsWithBestTerms(user, 20, 15);
		for (MovieSimilarity movieSimilarity : recomendations) {
			System.out.println(movieSimilarity.movie.getTitle() + " " + movieSimilarity.similarity);
		}
		*/
		
		context.stop();
		context.close();
	}
}
