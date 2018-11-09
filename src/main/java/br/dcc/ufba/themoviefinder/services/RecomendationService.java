package br.dcc.ufba.themoviefinder.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dcc.ufba.themoviefinder.entities.models.Movie;
import br.dcc.ufba.themoviefinder.entities.models.User;
import br.dcc.ufba.themoviefinder.entities.services.MovieService;
import br.dcc.ufba.themoviefinder.services.similarity.MovieSimilarity;
import br.dcc.ufba.themoviefinder.services.similarity.UserMovieSimilarity;
import br.dcc.ufba.themoviefinder.utils.TFIDFCalculator;
import br.dcc.ufba.themoviefinder.utils.TermValue;

@Service
public class RecomendationService 
{
	private UserMovieSimilarity simService;
	
	@Autowired
	private MovieService movieService;
	
	private static final Logger LOGGER = LogManager.getLogger(RecomendationService.class);
	
	public void setUserMovieSimilarity(UserMovieSimilarity userMovieSimilarity)
	{
		simService = userMovieSimilarity;
	}
	
	public void updateRecomendations(User user, int qtdMovies)
	{
		
	}
	
	public List<MovieSimilarity> getRecomendationsByMovie(Movie movie, int qtMovies)
	{
		return getRecomendations(movie.getTokensList(), movieService.getAllMovies(), qtMovies);	
	}
	
	public List<MovieSimilarity> getRecomendationsByUser(User user, int qtMovies)
	{
		return getRecomendations(getUserMovieTokens(user), movieService.getAllMovies(), qtMovies);
	}
	
	public List<MovieSimilarity> getRecomendationsByUserBestTerms(User user, int qtMovies, int qtTerms)
	{
		return getRecomendations(getUserBestTerms(user, qtTerms), movieService.getAllMovies(), qtMovies);
	}
	
	public List<String> getUserBestTerms(User user, int qtTerms)
	{
		List<List<String>> listOfDocs = new ArrayList<List<String>>();
		List<String> uniqueValues = new ArrayList<String>();
	
		for(Movie movie : user.getMovies()) {
			listOfDocs.add(movie.getTokensList());
			uniqueValues.addAll(movie.getTokensList());
		}
		uniqueValues = TFIDFCalculator.uniqueValues(uniqueValues);
		
		List<TermValue> termValueList = new ArrayList<TermValue>();
		for(String term : uniqueValues) {
			for (Movie movie : user.getMovies()) {
				termValueList.add(new TermValue(term, TFIDFCalculator.tfIdf(movie.getTokensList(), listOfDocs, term)));
			}
		}
		
		termValueList.sort((TermValue a, TermValue b) -> a.compareTo(b));
		int max = 0;
		if(qtTerms >= 0) {
			max = Math.min(qtTerms, termValueList.size());
		}
		termValueList = termValueList.subList(0, max);
		uniqueValues = new ArrayList<String>();
		for (TermValue termValue : termValueList) {
			if(! uniqueValues.contains(termValue.term)) {
				uniqueValues.add(termValue.term);	
			}
		}
		return uniqueValues;
	}
	
	private List<MovieSimilarity> getRecomendations(List<String> tokens, List<Movie> movies, int qtMovies)
	{
		if(simService != null) {
			List<MovieSimilarity> simList = new ArrayList<MovieSimilarity>();
			if(! (tokens.isEmpty() || movies.isEmpty())) {
				for (Movie movie : movies) {
					try {
						MovieSimilarity mv = new MovieSimilarity(movie, simService.getSimilarity(tokens, movie.getTokensList()));
						simList.add(mv);
						LOGGER.info(mv);
					} catch(Exception e) {
						LOGGER.error(e.getMessage(), e);
					}
					System.out.println(simList.size() + " - " + simList.size() / ((double) movies.size()) * 100 + "%");
				}
				simList.sort((MovieSimilarity a, MovieSimilarity b) -> a.compareTo(b));
			}
			int max = 0;
			if(qtMovies >= 0) {
				max = Math.min(qtMovies, simList.size());
			}
			return simList.subList(0, max);	
		} else {
			throw new IllegalStateException("a userMovieSimilarity service must be setted");
		}
	}
	
	private List<String> getUserMovieTokens(User user)
	{
		List<String> userTokens = new ArrayList<String>();
		for(Movie userMovie : user.getMovies()) {
			userTokens.addAll(userMovie.getTokensList());
		}
		return userTokens;
	}
}
