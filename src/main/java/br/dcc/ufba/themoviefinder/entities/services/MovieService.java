package br.dcc.ufba.themoviefinder.entities.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.dcc.ufba.themoviefinder.entities.models.Movie;
import br.dcc.ufba.themoviefinder.entities.models.User;
import br.dcc.ufba.themoviefinder.entities.repositories.MovieRepository;
import br.dcc.ufba.themoviefinder.services.NLPTokenizer;

@Service
public class MovieService 
{
	@Autowired
	private MovieRepository movieRepo;
	
	@Autowired
	private NLPTokenizer nlpTokenizer;
	
	@Value("${app.recommendation-batch-movie-size: 500}")
	private int batchMovieSize;
	
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
	
	public List<Movie> getAllMoviesExcept(List<Integer> movieIds)
	{
		return movieRepo.findByIdNotIn(movieIds);
	}
	
	public Page<Movie> pageMoviesExcept(List<Integer> movieIds, Pageable pageble)
	{
		return movieRepo.findByIdNotIn(movieIds, pageble);
	}
	
	public Page<Movie> pageMoviesNotRatedByUser(User user, Pageable pageble)
	{
		return movieRepo.findByNotRatedByUser(user.getId(), pageble);
	}
	
	public Page<Movie> pageMovies(Pageable pageble)
	{
		return movieRepo.findAll(pageble);
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
		Pageable pageRequest = PageRequest.of(0, batchMovieSize);
		Page<Movie> page = movieRepo.findAll(pageRequest);
		int qtPages = page.getTotalPages();
		updateMovieTokens(page.getContent());
		for (int i = 1; i < qtPages; i++) {
			updateMovieTokens(movieRepo.findAll(page.nextPageable()).getContent());
		}
		nlpTokenizer.unloadModels();
	}
	
	public List<String> generateMovieTokens(Movie movie)
	{
		List<String> tokens = new ArrayList<String>();
		if(movie != null && movie.getDescription() != null) {
			if(! nlpTokenizer.isModelsLoaded()) {
				try {
					nlpTokenizer.loadModels();
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			if(nlpTokenizer.isModelsLoaded()) {
				return nlpTokenizer.tokenize(movie.getDescription());
			}
		}
		return tokens;	
	}
	
	private void updateMovieTokens(List<Movie> movies)
	{
		if(movies != null) {
			movies.forEach(movie -> {
				List<String> tokens = generateMovieTokens(movie);
				if(! tokens.isEmpty()) {
					movie.setTokens(tokens);
					movieRepo.save(movie);
				}
			});
		}		
	}
}
