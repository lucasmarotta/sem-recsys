package br.dcc.ufba.semrecsys.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dcc.ufba.semrecsys.models.Movie;
import br.dcc.ufba.semrecsys.models.User;
import br.dcc.ufba.semrecsys.repositories.UserRepository;
import br.dcc.ufba.semrecsys.utils.MovieSimilarity;

@Service
public class UserService 
{
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private UserMovieSimilarityService simService;
	
	@Autowired
	private MovieService movieService;
	
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
	
	public void updateRecomendations(User user, int qtdMovies)
	{
		
	}
	
	public void updateExtendedRecomendations(User user, int qtdMovies)
	{
		
	}
	
	public List<MovieSimilarity> getRecomendations(User user, int qtdMovies, boolean extendedMode)
	{
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
				simList.add(new MovieSimilarity(movie, simService.getSimilarity(userTokens, movieTokens)));
			}
			simList.sort((MovieSimilarity a, MovieSimilarity b) -> a.compareTo(b));
		}
		int max = 0;
		if(qtdMovies >= 0) {
			max = Math.min(qtdMovies, simList.size());
		}
		return simList.subList(0, max);
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
