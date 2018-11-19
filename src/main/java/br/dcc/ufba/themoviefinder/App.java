package br.dcc.ufba.themoviefinder;

import java.util.List;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import br.dcc.ufba.themoviefinder.controllers.launcher.LauncherContext;
import br.dcc.ufba.themoviefinder.entities.models.Movie;
import br.dcc.ufba.themoviefinder.entities.models.User;
import br.dcc.ufba.themoviefinder.entities.services.UserService;
import br.dcc.ufba.themoviefinder.services.RecomendationService;
import br.dcc.ufba.themoviefinder.services.similarity.UserMovieRLWSimilarityService;
import br.dcc.ufba.themoviefinder.utils.ItemValue;
import br.dcc.ufba.themoviefinder.utils.TFIDFCalculator;
import net.codecrafting.springfx.context.ViewStage;
import net.codecrafting.springfx.core.SpringFXApplication;
import net.codecrafting.springfx.core.SpringFXLauncher;

@SpringBootApplication
public class App extends SpringFXApplication
{
	@Autowired
	private UserService userService;
	
	@Autowired
	private RecomendationService recomendationService;
	
	@Autowired
	private ConfigurableApplicationContext springContext;
	
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
		List<String> userTokens = user.getUserBestTerms(15);
		recomendationService.setUserMovieSimilarity(springContext.getBean(UserMovieRLWSimilarityService.class));
		
		System.out.println("Recomendations with RLW Similarity\n");
		StopWatch watch = new StopWatch();
		watch.start();
		List<ItemValue<Movie>> recomendations = recomendationService.getRecomendationsByUserBestTerms(user, 20, 15);
		watch.stop();
		System.out.println("Time Elapsed: " + (watch.getTime() / 1000) + "s");
		
		for (ItemValue<Movie> movieSimilarity : recomendations) {
			System.out.println(movieSimilarity.item.getTitle() + " - " + movieSimilarity.value);
			System.out.println(TFIDFCalculator.uniqueValues(movieSimilarity.item.getTokensList()));
			System.out.println(userTokens);
			System.out.println();
		}
		SpringFXLauncher.exit();
	}
}
