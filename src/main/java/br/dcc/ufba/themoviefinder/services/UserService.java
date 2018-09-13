package br.dcc.ufba.themoviefinder.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dcc.ufba.themoviefinder.lodweb.TFIDFCalculator;
import br.dcc.ufba.themoviefinder.models.Movie;
import br.dcc.ufba.themoviefinder.models.User;
import br.dcc.ufba.themoviefinder.repositories.UserRepository;
import br.dcc.ufba.themoviefinder.utils.MovieSimilarity;
import br.dcc.ufba.themoviefinder.utils.TermValue;

@Service
public class UserService 
{
	@Autowired
	private UserRepository userRepo;
	
	private UserMovieSimilarity simService;
	
	@Autowired
	private MovieService movieService;
	
	private static final Logger LOGGER = LogManager.getLogger(UserService.class);
	
	public void save(User user)
	{
		if(user != null) {
			userRepo.save(user);
		}
	}
	
	public void removeById(int id)
	{
		userRepo.deleteById((long) id);
	}
	
	public User findById(int id)
	{
		return userRepo.findById(id);
	}
	
	public User findByName(String name)
	{
		if(name != null) {
			return userRepo.findByName(name);
		}
		return null;
	}
	
	public User findByEmail(String email)
	{
		if(email != null) {
			return userRepo.findByEmail(email);
		}
		return null;
	}
	
	public List<User> findAll()
	{
		return userRepo.findAll();
	}
	
	public void setUserMovieSimilarity(UserMovieSimilarity userMovieSimilarity)
	{
		simService = userMovieSimilarity;
	}
	
	public void updateRecomendations(User user, int qtdMovies)
	{
		
	}
	
	public void updateExtendedRecomendations(User user, int qtdMovies)
	{
		
	}
	
	public List<MovieSimilarity> getRecomendationsWithAllTerms(User user, int qtMovies, boolean extendedMode)
	{
		if(simService != null) {
			List<MovieSimilarity> simList = new ArrayList<MovieSimilarity>();
			if(user.getMovies() != null) {
				List<Movie> movies = movieService.getAllMoviesExcept(user.getMovies());
				List<String> userTokens = getUserMovieTokens(user, false);
				for (Movie movie : movies) {
					List<String> movieTokens = null;
					if(extendedMode) {
						movieTokens = movie.getExtendedTokensList();	
					} else {
						movieTokens = movie.getTokensList();	
					}
					MovieSimilarity mv = new MovieSimilarity(movie, simService.getSimilarity(userTokens, movieTokens));
					LOGGER.info(mv);
					simList.add(mv);
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
	
	public List<MovieSimilarity> getRecomendationsWithBestTerms(User user, int qtMovies, int qtTerms)
	{
		if(simService != null) {
			List<MovieSimilarity> simList = new ArrayList<MovieSimilarity>();
			if(user.getMovies() != null) {
				List<Movie> movies = movieService.getAllMoviesExcept(user.getMovies());
				List<String> userTokens = getUserBestNTerms(user, qtTerms);
				for (Movie movie : movies) {
					MovieSimilarity mv = new MovieSimilarity(movie, simService.getSimilarity(userTokens, movie.getTokensList()));
					LOGGER.info(mv);
					simList.add(mv);
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
	
	private List<String> getUserMovieTokens(User user, boolean extendedMode)
	{
		List<String> userTokens = new ArrayList<String>();
		for(Movie userMovie : user.getMovies()) {
			if(extendedMode) {
				userTokens.addAll(userMovie.getExtendedTokensList());
			} else {
				userTokens.addAll(userMovie.getTokensList());
			}
		}
		return userTokens;
	}
	
	public List<String> getUserBestNTerms(User user, int qtTerms)
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
	
	/*
	private void saveUserMovies(User user)
	{
		if(user != null && user.getMovies() != null) {
			for(Movie movie : user.getMovies()) {
				if(movie != null) {
					if(userMovieRepo.findByUserAndMovie(user, movie) == null) {
						UserMovie userMovie = new UserMovie(user, movie);
						userMovieRepo.save(userMovie);	
					}
				}
			}
		}
	}
	
	public void removeUserMovie(User user, Movie movie)
	{
		if(user != null && movie != null) {
			removeUserMovie(user.getId(), movie.getId());
		}
	}
	
	public void removeUserMovie(int userId, int movieId)
	{
		userMovieRepo.delete(new UserMovie(new User(userId), new Movie(movieId)));
	}
	
	public void removeUserMovieById(int id)
	{
		userMovieRepo.deleteById((long) id);
	}
	
	public void removeAllUserMovieByUser(User user)
	{
		if(user != null) {
			removeAllUserMovieByUser(user.getId());	
		}
	}
	
	public void removeAllUserMovieByUser(int userId)
	{
		userMovieRepo.deleteByUser(new User(userId));
	}
	*/
}
