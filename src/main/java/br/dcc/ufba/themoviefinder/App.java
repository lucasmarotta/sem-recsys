package br.dcc.ufba.themoviefinder;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.dcc.ufba.themoviefinder.controllers.launcher.LauncherContext;
import br.dcc.ufba.themoviefinder.entities.models.Movie;
import br.dcc.ufba.themoviefinder.entities.models.User;
import br.dcc.ufba.themoviefinder.entities.services.MovieService;
import br.dcc.ufba.themoviefinder.entities.services.UserService;
import br.dcc.ufba.themoviefinder.services.RecomendationService;
import br.dcc.ufba.themoviefinder.services.similarity.UserMovieRLWSimilarityService;
import br.dcc.ufba.themoviefinder.utils.ItemValue;
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
	
	@Autowired
	private RecomendationService recomendationService; 
	
	
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
		User user = userService.findByName("Lucas");
		for (Movie movie : user.getMovies()) {
			System.out.println(movie.getTitle());
			System.out.println(movie.getTokensList());
		}
		System.out.println("User best terms");
		System.out.println(user.getUserBestTerms(15));
		recomendationService.setUserMovieSimilarity(rlwSimilarityService);
		
		LOGGER.info("Recomendations with RLW Similarity");
		List<ItemValue<Movie>> recomendations = recomendationService.getRecomendationsByUserBestTerms(user, 15, 15);
		for (ItemValue<Movie> movieSimilarity : recomendations) {
			System.out.println(movieSimilarity.item.getTitle() + " - " + movieSimilarity.value);
		}
		SpringFXLauncher.exit();
	}
}
