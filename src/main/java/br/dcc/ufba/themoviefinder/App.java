package br.dcc.ufba.themoviefinder;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import br.dcc.ufba.themoviefinder.controllers.launcher.LauncherContext;
import br.dcc.ufba.themoviefinder.entities.models.LodRelationId;
import br.dcc.ufba.themoviefinder.entities.models.Movie;
import br.dcc.ufba.themoviefinder.entities.models.User;
import br.dcc.ufba.themoviefinder.entities.services.UserService;
import br.dcc.ufba.themoviefinder.lodweb.Sparql;
import br.dcc.ufba.themoviefinder.lodweb.SparqlWalk;
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
	private UserMovieRLWSimilarityService similarityService;
	
	@Autowired
	private ConfigurableApplicationContext springContext;
	
	private static final Logger LOGGER = LogManager.getLogger(App.class);
	
	public static void main(String args[]) throws Exception
	{
    	SpringFXLauncher.setRelaunchable(true);
		SpringFXLauncher.launch(new LauncherContext(App.class), args);
	}

	@Override
	public void start(ViewStage viewStage) throws Exception 
	{	
		StopWatch watch = new StopWatch();
		
		/*
		SparqlWalk sparqlWalk = springContext.getBean(SparqlWalk.class);
		List<LodRelationId> lodIds = Arrays.asList(new LodRelationId("France", "Paris"),
				new LodRelationId("France", "Juice"),
				new LodRelationId("France", "Art"),
				new LodRelationId("Brazil", "Brasilia"),
				new LodRelationId("Brazil", "Box"),
				new LodRelationId("Brazil", "Paper"),
				new LodRelationId("Brazil", "Beach"),
				new LodRelationId("Car", "Automobile"),
				new LodRelationId("United_States", "Washington,_D.C."),
				new LodRelationId("China", "Hong_Kong"),
				new LodRelationId("Ariana_Grande", "Selena_Gomez"),
				new LodRelationId("Selena_Gomez", "Elon_Musk"),
				new LodRelationId("Coconut", "Plant"),
				new LodRelationId("Tom_Cruise", "Lady_Gaga"),
				new LodRelationId("Star", "Galaxy"),
				new LodRelationId("Earth", "Moon"),
				new LodRelationId("Earth", "Table"),
				new LodRelationId("Book", "Movie"),
				new LodRelationId("Book", "Metal"),
				new LodRelationId("Johnny_Cash", "June_Carter_Cash"),
				new LodRelationId("Johnny_Cash", "Al_Green"),
				new LodRelationId("Johnny_Cash", "Elvis_Presley"),
				new LodRelationId("Johnny_Cash", "Kris_Kristofferson"),
				new LodRelationId("Johnny_Cash", "Carlene_Carter"));

		similarityService.init();
		lodIds.forEach(lodId -> {
			System.out.println(lodId.getResource1() + "\t" + lodId.getResource2() + "\t" + similarityService.getSimilarityBetween2Terms(lodId.getResource1(), lodId.getResource2()));
		});
		similarityService.reset();
		*/
		
		/*
		DBPediaService s = springContext.getBean(DBPediaService.class);
		lodIds.forEach((lodId) -> {
			String term1 = Sparql.wrapStringAsResource(lodId.getResource1());
			String term2 = Sparql.wrapStringAsResource(lodId.getResource2());
			double direct1 = sparqlWalk.countDirectLinksFromResource(term1);
			double direct2 = sparqlWalk.countDirectLinksFromResource(term2);
			double directRelation = sparqlWalk.countDirectLinksBetween2Resources(term1, term2);
			System.out.println(String.format("%s/%s\t%f\t%f", lodId.getResource1(), lodId.getResource2(), direct1 + direct2, directRelation));
		});
		System.out.println();
		lodIds.forEach((lodId) -> {
			String term1 = Sparql.wrapStringAsResource(lodId.getResource1());
			String term2 = Sparql.wrapStringAsResource(lodId.getResource2());
			double indirect1 = sparqlWalk.countIndirectLinksFromResource(term1);
			double indirect2 = sparqlWalk.countIndirectLinksFromResource(term2);
			double indirectRelation = sparqlWalk.countIndirectLinksBetween2Resources(term1, term2);
			System.out.println(String.format("%s/%s\t%f\t%f", lodId.getResource1(), lodId.getResource2(), indirect1 + indirect2, indirectRelation));
		});
		*/
		
		User user = userService.findByName("Lucas");

		for (Movie movie : user.getMovies()) {
			System.out.println();
			System.out.println(movie.getTitle());
			System.out.println(movie.getTokensList());
		}
		
		int qtRecomendations = 20;
		int qtTerms = 15;
		
		List<String> userTokens = user.getUserBestTerms(qtTerms);
		recomendationService.setUserMovieSimilarity(similarityService);
		System.out.println("Recomendations with RLW Similarity\n");
		watch.start();
		List<ItemValue<Movie>> recomendations = recomendationService.getRecomendationsByUserBestTerms(user, qtRecomendations, qtTerms);
		watch.stop();
		System.out.println("Time Elapsed: " + (watch.getTime() / 1000) + "s");
		for (ItemValue<Movie> movieSimilarity : recomendations) {
			System.out.println(movieSimilarity.item + " - " + movieSimilarity.value);
			System.out.println(TFIDFCalculator.uniqueValues(movieSimilarity.item.getTokensList()));
			System.out.println(userTokens);
			System.out.println();
		}
		
		SpringFXLauncher.exit();
	}
}
