package br.dcc.ufba.semrecsys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.dcc.ufba.semrecsys.models.Idf;
import br.dcc.ufba.semrecsys.models.Movie;
import br.dcc.ufba.semrecsys.repositories.IDFRepository;
import br.dcc.ufba.semrecsys.services.MoviesService;

@SpringBootApplication
public class App implements CommandLineRunner
{
	@Autowired
	private MoviesService movieService;
	
	@Autowired
	private IDFRepository idfRespository;
	
	private static final Logger LOGGER = LogManager.getLogger(App.class);
	
	public static void main(String args[])
	{
		SpringApplication.run(App.class, args);
	}

	@Override
	public void run(String... args) throws Exception 
	{
		List<Movie> movies = movieService.getAllMovies();
		Set<String> uniqueTokens = new HashSet<String>();
		for (Movie movie : movies) {
			List<String> tokens = movie.getExtendedTokensList();
			for (String token : tokens) {
				uniqueTokens.add(token);
			}
		}
		
		List<Idf> idfList = new ArrayList<Idf>();
		for (String token : uniqueTokens) {
			idfList.add(new Idf(token, 0f, 0f));
		}
		idfRespository.saveAll(idfList);
		System.out.println(idfList.size());
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
