package br.dcc.ufba.themoviefinder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.dcc.ufba.themoviefinder.entities.models.User;
import br.dcc.ufba.themoviefinder.entities.services.MovieService;
import br.dcc.ufba.themoviefinder.entities.services.UserService;
import br.dcc.ufba.themoviefinder.launcher.controllers.LauncherContext;
import br.dcc.ufba.themoviefinder.services.UserMovieRLWSimilarityService;
import net.codecrafting.springfx.context.ViewStage;
import net.codecrafting.springfx.core.SpringFXApplication;
import net.codecrafting.springfx.core.SpringFXLauncher;

@SpringBootApplication
public class App extends SpringFXApplication
{
	@Autowired
	private MovieService movieService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserMovieRLWSimilarityService rlwSimilarityService; 
	
	private static final Logger LOGGER = LogManager.getLogger(App.class);
	
	public static void main(String args[])
	{
        try {
			SpringFXLauncher.launch(new LauncherContext(App.class), args);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@Override
	public void start(ViewStage viewStage) throws Exception 
	{
		//Movie toyStory = movieService.findFirstByLikeTitle("Toy Story");
		//Movie matrix = movieService.findFirstByLikeTitle("Matrix");
		
		User user = userService.findByName("Lucas");
		userService.setUserMovieSimilarity(rlwSimilarityService);
		//System.out.println(rlwSimilarityService.getSimilarityFromMovie(movieService.findFirstByTitle("Toy Story 2"), movieService.findFirstByTitle("Toy Story")));
		//System.out.println(rlwSimilarityService.getSimilarityFromMovie(movieService.findFirstByTitle("Toy Story 2"), movieService.findFirstByTitle("Halloween II")));
		
		/*
		System.out.println("\nRecomendations with RLW Similarity");
		List<MovieSimilarity> recomendations = userService.getRecomendationsWithBestTerms(user, 20, 15);
		for (MovieSimilarity movieSimilarity : recomendations) {
			System.out.println(movieSimilarity.movie.getTitle() + " " + movieSimilarity.similarity);
		}
		*/
		
		SpringFXLauncher.exit();
	}
}