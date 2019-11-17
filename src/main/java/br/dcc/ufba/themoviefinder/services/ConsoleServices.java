package br.dcc.ufba.themoviefinder.services;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dcc.ufba.themoviefinder.entities.models.LodRelationId;
import br.dcc.ufba.themoviefinder.entities.models.Movie;
import br.dcc.ufba.themoviefinder.entities.models.Recomendation;
import br.dcc.ufba.themoviefinder.entities.models.RecommendationType;
import br.dcc.ufba.themoviefinder.entities.models.User;
import br.dcc.ufba.themoviefinder.entities.services.MovieService;
import br.dcc.ufba.themoviefinder.entities.services.TfIdfService;
import br.dcc.ufba.themoviefinder.entities.services.UserService;
import br.dcc.ufba.themoviefinder.lodweb.SparqlWalk;
import br.dcc.ufba.themoviefinder.services.similarity.RLWSimilarityService;

@Service
public class ConsoleServices 
{
	@Autowired
	private MovieService movieService;
	
	@Autowired
	private NLPTokenizer nlpTokenizer;
	
	@Autowired
	private SparqlWalk sparqlWalk;
	
	@Autowired
	private RLWSimilarityService similarityService;
	
	@Autowired
	private RecommendationService recService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private TfIdfService tfIdfService;
	
	private static final Logger LOGGER = LogManager.getLogger(ConsoleServices.class);
	
	@PostConstruct
	public void init()
	{
		similarityService.init();
	}
			
	/**
	 * Generate tokens from a text string
	 * @param text
	 */
	public void generateTokens(String text)
	{
		if(text != null) {
			if(! nlpTokenizer.isModelsLoaded()) {
				try {
					nlpTokenizer.loadModels();
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			if(nlpTokenizer.isModelsLoaded()) {
				nlpTokenizer.tokenize(text).forEach(System.out::println);
			}
		}
	}
	
	/**
	 * Generates movie tokens by first title like
	 * @param movieTitle
	 */
	public void generateMovieTokensByTitleLike(String movieTitle)
	{
		Movie movie = movieService.findFirstByLikeTitle(movieTitle);
		System.out.println(movie.getDescription());
		System.out.println(movieService.generateMovieTokens(movie));
	}

	/**
	 * Generates movie tokens by id
	 * @param movieTitle
	 */
	public void generateMovieById(int movieId)
	{
		Movie movie = movieService.getMovieById(movieId);
		System.out.println(movie.getDescription());
		System.out.println(movieService.generateMovieTokens(movie));
	}
	
	/**
	 * Updates the database tokens for all movies
	 */
	public void updateMovieTokens()
	{
		movieService.updateMovieTokens();
		System.out.println("Movies tokens updated");
	}
	
	/**
	 * Updates the idf cache
	 */
	public void updateIdfCache()
	{
		tfIdfService.updateIdf();
		System.out.println("IDF cache updated");
	}
	
	/**
	 * Check whether or not the provided URIs are redirects
	 * @param uri1
	 * @param uri2
	 */
	public void isRedirect(String uri1, String uri2)
	{
		System.out.println(sparqlWalk.isRedirect(uri1, uri2));
	}
	
	/**
	 * Count the number of direct links reached to the given resource
	 * @param uri
	 */
	public void countDirectLinksFromResource(String uri)
	{
		System.out.println(sparqlWalk.countDirectLinksFromResource(uri));
	}
	
	/**
	 * Count the number of indirect links reached to the given resource
	 * @param uri
	 */
	public void countIndirectLinksFromResource(String uri)
	{
		System.out.println(sparqlWalk.countIndirectLinksFromResource(uri));
	}
	
	/**
	 * Count the number of direct links between 2 given resources
	 * @param uri1
	 * @param uri2
	 */
	public void countDirectLinksFrom2Resources(String uri1, String uri2)
	{
		System.out.println(sparqlWalk.countDirectLinksBetween2Resources(uri1, uri2));
	}
	
	/**
	 * Count the number of indirect incoming/outgoing links between two resources
	 * @param uri1
	 * @param uri2
	 */
	public void countIndirectLinksFrom2Resources(String uri1, String uri2)
	{
		System.out.println(sparqlWalk.countIndirectLinksBetween2Resources(uri1, uri2));
	}
	
	/**
	 * Generates some examples of comparisons between resources, using RLWS recommendation type
	 */
	public void generateRLWSExamplesByType(RecommendationType type)
	{
		generateRLWSByLodIds(Arrays.asList(new LodRelationId("France", "Paris"),
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
				new LodRelationId("Johnny_Cash", "Carlene_Carter")), type);
	}
	
	/**
	 * Generates some examples of comparisons between resources, using RLWS custom weights
	 */
	public void generateRLWSExamplesByWeights(Double directWeight, Double indirectWeight)
	{
		generateRLWSByLodIds(Arrays.asList(new LodRelationId("France", "Paris"),
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
				new LodRelationId("Johnny_Cash", "Carlene_Carter")), directWeight, indirectWeight);
	}
	
	/**
	 * Generates comparisons between resources, using RLWS custom weights
	 */
	public void generateRLWSByLodIds(List<LodRelationId> lodIds, Double directWeight, Double indirectWeight)
	{
		similarityService.setWeights(directWeight, indirectWeight);
		lodIds.forEach(lodId -> {
			System.out.println(lodId.getResource1() + "\t" + lodId.getResource2() + "\t" + similarityService.getSimilarityBetween2Terms(lodId.getResource1(), lodId.getResource2()));
		});
		similarityService.resetCache();
	}

	/**
	 * Generates comparisons between resources, using RLWS RLWS recommendation type
	 */
	public void generateRLWSByLodIds(List<LodRelationId> lodIds, RecommendationType type)
	{
		similarityService.setType(type);
		lodIds.forEach(lodId -> {
			System.out.println(lodId.getResource1() + "\t" + lodId.getResource2() + "\t" + similarityService.getSimilarityBetween2Terms(lodId.getResource1(), lodId.getResource2()));
		});
		similarityService.resetCache();
	}
	
	/**
	 * Generates the best terms by a user id
	 * @param userId
	 * @param recModel
	 */
	public void getBestTermsByUserId(int userId, RecommendationModel recModel)
	{
		userService.findById(userId).getUserBestTerms(recModel).forEach(System.out::println);
	}
	
	/**
	 * Generates the best terms by a user id
	 * @param userEmail
	 * @param recModel
	 */
	public void getBestTermsByUserEmail(String userEmail, RecommendationModel recModel)
	{
		userService.findByEmail(userEmail).getUserBestTerms(recModel).forEach(System.out::println);
	}
	
	/**
	 * Generates recomendations by user id
	 * @param users
	 * @param recModel
	 */
	public void generateRecommendationsByUserId(int userId, RecommendationModel recModel)
	{
		generateRecommendationsByUsers(Arrays.asList(userService.findById(userId)), recModel);
	}
	
	/**
	 * Generates recommendations by user email
	 * @param users
	 * @param recModel
	 */
	public void generateRecommendationsByUserEmail(String userEmail, RecommendationModel recModel)
	{
		generateRecommendationsByUsers(Arrays.asList(userService.findByEmail(userEmail)), recModel);
	}
	
	/**
	 * Generate recommendations for the next 30 online users which does have not any recommendation
	 * @param recModel
	 */
	public void generateRecomendationsByOnlineUsers(RecommendationModel recModel)
	{
		generateRecommendationsByUsers(userService.getOnlineUsersToRecomendation(recModel.type), recModel);
	}
	
	/**
	 * Generate recommendations for the next 30 offline users which does have not any recommendation
	 * @param recModel
	 */
	public void generateRecomendationsByOfflineUsers(RecommendationModel recModel)
	{
		generateRecommendationsByUsers(userService.getOfflineUsersToRecomendation(recModel.type), recModel);
	}
	
	/**
	 * Generates recommendations by users
	 * @param users
	 * @param recModel
	 */
	public void generateRecommendationsByUsers(List<User> users, RecommendationModel recModel)
	{
		recService.setRecModel(recModel);
		users.forEach(user -> {
			System.out.println("Movies for " + user.getEmail() + "\n");
			user.getMoviesRecModel(recModel).forEach(movie -> {
				System.out.println(movie.getTitle());
				System.out.println(movie.getTokensList() + "\n");
			});
			List<String> userTokens = user.getUserBestTerms(recModel);
			System.out.println("Best terms: \n" + userTokens);
			
			StopWatch watch = new StopWatch();
			watch.start();
			List<Recomendation> recomendations = recService.getRecommendationsByUserBestTerms(user);
			watch.stop();
			
			System.out.println("\nRecommendations for " + user.getEmail() + "\n");
			recomendations.stream().map(rec -> rec.getMovie().getTitle()).forEach(System.out::println);
			
			System.out.println("\nRecommendations details:\n");
			recomendations.forEach(rec -> {
				System.out.println(rec);
				System.out.println(userTokens + "\n");
				//System.out.println("=HIPERLINK(\"https://www.themoviedb.org/movie/" + rec.getMovie().getTmdbId() + "?language=pt-BR\"; \"" + rec.getMovie().getTitle() + "\")");
			});
			
			System.out.println("Recommendation Time Elapsed: " + (watch.getTime() / 1000) + "s");
		});
	}
	
	/**
	 * Update recommendations by user id
	 * @param users
	 * @param recModel
	 */
	public void updateRecommendationsByUserId(int userId, RecommendationModel recModel)
	{
		updateRecommendationsByUsers(Arrays.asList(userService.findById(userId)), recModel, false);
	}
	
	/**
	 * Update recommendations by user email
	 * @param users
	 * @param recModel
	 */
	public void updateRecommendationsByUserEmail(String userEmail, RecommendationModel recModel)
	{
		updateRecommendationsByUsers(Arrays.asList(userService.findByEmail(userEmail)), recModel, false);
	}
	
	/**
	 * Update users recommendations
	 * @param users
	 * @param recModel
	 * @param runTwice
	 */
	public void updateRecommendationsByUsers(List<User> users, RecommendationModel recModel, boolean runTwice)
	{
		recService.setRecModel(recModel);
		users.forEach(user -> {
			StopWatch watch = new StopWatch();
			
			watch.start();
			if(runTwice) recService.getRecommendationsByUserBestTerms(user);
			List<Recomendation> recomendations = recService.getRecommendationsByUserBestTerms(user);
			watch.stop();
			
			user.setRecomendations(recomendations);
			userService.save(user);
			System.out.println(user.getEmail() + " updated. Time Elapsed: " + (watch.getTime() / 1000) + "s");
		});
	}
}
