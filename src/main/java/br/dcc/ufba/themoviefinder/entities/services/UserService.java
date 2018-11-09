package br.dcc.ufba.themoviefinder.entities.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dcc.ufba.themoviefinder.entities.models.User;
import br.dcc.ufba.themoviefinder.entities.repositories.UserRepository;

@Service
public class UserService 
{
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private MovieService movieService;
	
	//private static final Logger LOGGER = LogManager.getLogger(UserService.class);
	
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
