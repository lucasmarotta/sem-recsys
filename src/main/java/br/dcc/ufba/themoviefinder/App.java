package br.dcc.ufba.themoviefinder;

import java.util.List;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import br.dcc.ufba.themoviefinder.controllers.launcher.LauncherContext;
import br.dcc.ufba.themoviefinder.entities.models.Recomendation;
import br.dcc.ufba.themoviefinder.entities.models.RecomendationType;
import br.dcc.ufba.themoviefinder.entities.models.User;
import br.dcc.ufba.themoviefinder.entities.services.MovieService;
import br.dcc.ufba.themoviefinder.entities.services.UserService;
import br.dcc.ufba.themoviefinder.services.RecomendationModel;
import br.dcc.ufba.themoviefinder.services.RecomendationService;
import br.dcc.ufba.themoviefinder.services.similarity.UserMovieRLWSimilarityService;
import net.codecrafting.springfx.context.ViewStage;
import net.codecrafting.springfx.core.SpringFXApplication;
import net.codecrafting.springfx.core.SpringFXLauncher;

@SpringBootApplication
public class App extends SpringFXApplication
{
	@Autowired
	private UserService userService;
	
	@Autowired
	private MovieService movieService;
	
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
		//SparqlWalk sparqlWalk = springContext.getBean(SparqlWalk.class);
		List<LodRelationId> lodIds = Arrays.asList(new LodRelationId("Luiz_Inácio_Lula_da_Silva", "President"),
				new LodRelationId("Luiz_Inácio_Lula_da_Silva", "Greeting"),
				new LodRelationId("Luiz_Inácio_Lula_da_Silva", "News_media"),
				new LodRelationId("Luiz_Inácio_Lula_da_Silva", "Curitiba"),
				new LodRelationId("Speech", "President"),
				new LodRelationId("Speech", "Greeting"),
				new LodRelationId("Speech", "News_media"),
				new LodRelationId("Speech", "Curitiba"),
				new LodRelationId("Media", "President"),
				new LodRelationId("Media", "Greeting"),
				new LodRelationId("Media", "News_media"),
				new LodRelationId("Media", "Curitiba"),
				new LodRelationId("Paraná_(state)", "President"),
				new LodRelationId("Paraná_(state)", "Greeting"),
				new LodRelationId("Paraná_(state)", "News_media"),
				new LodRelationId("Paraná_(state)", "Curitiba"));

		similarityService.init();
		similarityService.setUseCache(false);
		lodIds.forEach(lodId -> {
			System.out.println(lodId.getResource1() + "\t" + lodId.getResource2() + "\t" + similarityService.getSimilarityBetween2Terms(lodId.getResource1(), lodId.getResource2()));
		});
		similarityService.resetCache();
		System.exit(1);
		
		
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
	
		similarityService.setTypeToIndirect();
		RecomendationModel recModel = new RecomendationModel();
		List<User> users = userService.getOnlineUsersToRecomendation(RecomendationType.RLWS_DIRECT);
		
		users.forEach(user -> {
			System.out.println("Recomendations for " + user.getEmail() + "\n");
			user.getMoviesRecModel(recModel).forEach(movie -> {
				System.out.println(movie.getTitle());
				System.out.println(movie.getTokensList() + "\n");
			});
			
			List<String> userTokens = user.getUserBestTerms(recModel);
			recomendationService.setRecModel(recModel);
			recomendationService.setUserMovieSimilarity(similarityService);
			//recomendationService.setUserMovieSimilarity(springContext.getBean(UserMovieCosineSimilarityService.class));
			
			watch.reset();
			watch.start();
			//recomendationService.getRecomendationsByUserBestTerms(user);
			List<Recomendation> recomendations = recomendationService.getRecomendationsByUserBestTerms(user);
			watch.stop();
			user.setRecomendations(recomendations);
			//userService.save(user);
			
			System.out.println("Time Elapsed: " + (watch.getTime() / 1000) + "s");
			recomendations.forEach(rec -> {
				System.out.println(rec);
				System.out.println(userTokens + "\n");
				//System.out.println("=HIPERLINK(\"https://www.themoviedb.org/movie/" + rec.getMovie().getTmdbId() + "?language=pt-BR\"; \"" + rec.getMovie().getTitle() + "\")");
			});
		});
		SpringFXLauncher.exit();
	}
}
