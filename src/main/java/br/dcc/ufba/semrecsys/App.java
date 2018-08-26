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
import br.dcc.ufba.semrecsys.repositories.UserRepository;
import br.dcc.ufba.semrecsys.services.MovieSimilarityService;
import br.dcc.ufba.semrecsys.services.MoviesService;

@SpringBootApplication
public class App implements CommandLineRunner
{
	@Autowired
	private MoviesService movieService;
	
	@Autowired
	private MovieSimilarityService simService;
	
	@Autowired
	private UserRepository userRepo;
	
	private static final Logger LOGGER = LogManager.getLogger(App.class);
	
	public static void main(String args[])
	{
		SpringApplication.run(App.class, args);
	}

	@Override
	public void run(String... args) throws Exception 
	{
		User user = userRepo.findByName("Lucas");
		System.out.println(simService.getSimilarityFromUser(user, movieService.findFirstByTitle("GoldenEye")));
		System.out.println();
		simService.setExtendedMode(false);
		System.out.println(simService.getSimilarityFromUser(user, movieService.findFirstByTitle("GoldenEye")));
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
