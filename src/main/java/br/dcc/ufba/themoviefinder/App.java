package br.dcc.ufba.themoviefinder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import br.dcc.ufba.themoviefinder.controllers.launcher.LauncherContext;
import br.dcc.ufba.themoviefinder.entities.services.UserService;
import br.dcc.ufba.themoviefinder.services.RecomendationService;
import br.dcc.ufba.themoviefinder.services.similarity.RLWSimilarity;
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
	private RLWSimilarity rlwSimilarity;
	
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
		/*
		DBPediaService s = springContext.getBean(DBPediaService.class);
		SparqlWalk sparqlWalk = springContext.getBean(SparqlWalk.class);
		
		s.parseResultVarName(s.query("SELECT  distinct ?r2\r\n" + 
				"WHERE {\r\n" + 
				"	{values (?r1 ?r3) {(<http://dbpedia.org/resource/Brown> <http://dbpedia.org/resource/Brazil>)} ?r2 ?p1 ?r1 . ?r2 ?p2 ?r3 . FILTER (?r1 != ?r3 && ?r2 != ?r1 && ?r2 != ?r3)} UNION\r\n" + 
				"	{values (?r1 ?r3) {(<http://dbpedia.org/resource/Brown> <http://dbpedia.org/resource/Brazil>)} ?r1 ?p1 ?r2 . ?r3 ?p2 ?r2 . FILTER (?r1 != ?r3 && ?r2 != ?r1 && ?r2 != ?r3)} " + Sparql.addFilter("p1", "r2") + "" + Sparql.addFilter("p2", null) + "\r\n" +
				"}"), "?r2").forEach(r -> {
					System.out.println(r);
				});
		*/
		
		/*
		LodRelationRepository lodRepo = springContext.getBean(LodRelationRepository.class);
		List<LodRelationId> lodIds = Arrays.asList(new LodRelationId("France", "Paris"), 
				new LodRelationId("Brazil", "Brasilia"), 
				//new LodRelationId("United_States", "Washington,_New_York"), 
				new LodRelationId("China", "Hong_Kong"), 
				new LodRelationId("Brazil", "Bee"),
				new LodRelationId("Ariana_Grande", "Selena_Gomez"),
				new LodRelationId("Selena_Gomez", "Elon_Musk"),
				new LodRelationId("United_States", "Africa"),
				new LodRelationId("Car", "Automobile"),
				new LodRelationId("Coconut", "Plant"),
				new LodRelationId("Tom_Cruise", "Lady_Gaga"),
				new LodRelationId("Melon", "Mars"),
				new LodRelationId("Star", "Galaxy"),
				new LodRelationId("Book", "Movie"));
		*/
		
		/*
		lodIds = Stream.concat(lodIds.stream(), new ArrayList<LodCacheRelation>(lodRepo.findRandomTop100()).stream().map(lodRelation -> {
			return lodRelation.getId();
		}).collect(Collectors.toList()).stream()).collect(Collectors.toList());
		*/
		
		/*
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
		
		System.out.println("France/Paris " + rlwSimilarity.getSimilarityBetween2Terms("France", "Paris"));
		System.out.println("Brazil/Brasilia " + rlwSimilarity.getSimilarityBetween2Terms("Brazil", "Brasilia"));
		System.out.println("United_States/Washington,_New_York " + rlwSimilarity.getSimilarityBetween2Terms("United_States", "Washington,_New_York"));
		System.out.println("Italy/Rome " + rlwSimilarity.getSimilarityBetween2Terms("Italy", "Rome"));
		System.out.println("China/Hong_Kong " + rlwSimilarity.getSimilarityBetween2Terms("China", "Hong_Kong"));
		System.out.println("Brazil/Bee " + rlwSimilarity.getSimilarityBetween2Terms("Brazil", "Bee"));
		System.out.println("Ariana_Grande/Selena_Gomez " + rlwSimilarity.getSimilarityBetween2Terms("Ariana_Grande", "Selena_Gomez"));
		System.out.println("Selena_Gomez/Elon_Musk " + rlwSimilarity.getSimilarityBetween2Terms("Selena_Gomez", "Elon_Musk"));
		*/
		
		/*
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
		*/
		SpringFXLauncher.exit();
	}
}
