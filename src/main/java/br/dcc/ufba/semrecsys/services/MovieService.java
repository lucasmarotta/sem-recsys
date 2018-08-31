package br.dcc.ufba.semrecsys.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import br.dcc.ufba.semrecsys.models.Movie;
import br.dcc.ufba.semrecsys.repositories.MovieRepository;

@Service
public class MovieService 
{
	@Autowired
	private MovieRepository movieRepo;
	
	@Autowired
	private NLPTokenizer nlpTokenizer;
	
	@Autowired
	private DBPediaService dbPediaService;
	
	private static final Logger LOGGER = LogManager.getLogger(MovieService.class);
	
	public Movie getMovieById(Integer id)
	{
		return movieRepo.findById(id);
	}
	
	public Movie getMovieByTitle(String title)
	{
		return movieRepo.findFirstByTitle(title);
	}
	
	public List<Movie> getMovies()
	{
		List<Movie> movies = movieRepo.findTop30ByOrderByIdAsc();
		return (movies != null) ? movies : new ArrayList<Movie>();
	}
	
	public List<Movie> getAllMovies()
	{
		List<Movie> movies = movieRepo.findAll();
		return (movies != null) ? movies : new ArrayList<Movie>();
	}
	
	public List<Movie> getAllMoviesExcept(Set<Movie> movies)
	{
		List<Integer> movieIds = new ArrayList<Integer>();
		for (Movie movie : movies) {
			movieIds.add(movie.getId());
		}
		return movieRepo.findByIdNotIn(movieIds);
	}
	
	public Movie findFirstByTitle(String title)
	{
		return movieRepo.findFirstByTitle(title);
	}
	
	public Movie findFirstByLikeTitle(String title)
	{
		List<Movie> movies = findByLikeTitle(title, 1);
		if(movies.size() > 0) {
			return movies.get(0);
		}
		return null;
	}
	
	public List<Movie> findByLikeTitle(String title, int qtdMovies)
	{
		return movieRepo.findByLikeTitle(title, PageRequest.of(0, qtdMovies)).getContent();
	}
	
	public List<Movie> findAllByLikeTitle(String title)
	{
		return movieRepo.findAllByLikeTitle(title);
	}
	
	public long countMovies()
	{
		return movieRepo.count();
	}
	
	public void updateMovieTokens()
	{
		updateMovieTokens(getAllMovies());
	}
	
	public void updateMovieTokens(List<Movie> movies)
	{
		if(movies != null) {
			for (Movie movie : movies) 
			{
				List<String> tokens = generateMovieTokens(movie);
				if(!tokens.isEmpty()) {
					movie.setExtendedTokens(tokens);
					movieRepo.save(movie);
				}
			}	
		}		
	}
	
	public void updateMovieExtendedTokens()
	{
		updateMovieExtendedTokens(getAllMovies());
	}
	
	public void updateMovieExtendedTokens(List<Movie> movies)
	{
		if(movies != null) {
			for (Movie movie : movies) 
			{
				List<String> tokens = generateExtendedMovieTokens(movie);
				if(!tokens.isEmpty()) {
					movie.setExtendedTokens(tokens);
					LOGGER.info("Update movie: "+ movie.getId() + " - " + movie.getTitle());
					LOGGER.info(movie.getExtendedTokensList());
					LOGGER.info("");
					movieRepo.save(movie);
				}
			}	
		}		
	}
	
	public List<String> generateMovieTokens(Movie movie)
	{
		if(movie.getTokens() == null) {
			List<String> tokens = new ArrayList<String>();
			if(movie != null && movie.getDescription() != null) {
				return nlpTokenizer.tokenize(movie.getDescription());
			}
			return tokens;	
		}
		return movie.getTokensList();
	}
	
	public List<String> generateExtendedMovieTokens(Movie movie)
	{
		List<String> tokens = generateMovieTokens(movie);
		tokens.addAll(dbPediaService.getResourceTokens(tokens));
		return tokens;
	}
}
