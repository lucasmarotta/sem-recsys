package br.dcc.ufba.themoviefinder.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.dcc.ufba.themoviefinder.entities.models.Movie;
import br.dcc.ufba.themoviefinder.entities.models.User;
import br.dcc.ufba.themoviefinder.entities.services.MovieService;
import br.dcc.ufba.themoviefinder.services.similarity.UserMovieSimilarityService;
import br.dcc.ufba.themoviefinder.utils.ItemValue;
import net.codecrafting.springfx.util.AsyncUtils;

@Service
public class RecomendationService 
{
	private UserMovieSimilarityService similarityService;
	
	@Value("${app.recomendation-batch-size: 5}")
	public int batchSize;
	
	@Autowired
	private MovieService movieService;
	
	private static final Logger LOGGER = LogManager.getLogger(RecomendationService.class);
	
	public void setUserMovieSimilarity(UserMovieSimilarityService similarityService)
	{
		this.similarityService = similarityService;
	}
	
	public int getBatchSize() 
	{
		return batchSize;
	}

	public void setBatchSize(int batchSize) 
	{
		this.batchSize = batchSize;
	}

	public List<ItemValue<Movie>> getRecomendationsByMovie(Movie movie, int qtMovies)
	{
		return getRecomendations(movie.getTokensList(), movieService.getAllMoviesExcept(Arrays.asList(movie)), qtMovies);	
	}
	
	public List<ItemValue<Movie>> getRecomendationsByUser(User user, int qtMovies)
	{
		return getRecomendations(user.getUserMovieTokens(), movieService.getAllMoviesExcept(user.getMovies()), qtMovies);
	}
	
	public List<ItemValue<Movie>> getRecomendationsByUserBestTerms(User user, int qtMovies, int qtTerms)
	{
		return getRecomendations(user.getUserBestTerms(qtTerms), movieService.getAllMoviesExcept(user.getMovies()), qtMovies);
	}
	
	private List<ItemValue<Movie>> getRecomendations(List<String> tokens, List<Movie> movies, int qtMovies)
	{
		if(similarityService != null) {
			List<ItemValue<Movie>> simList = new ArrayList<ItemValue<Movie>>();
			if(! (tokens.isEmpty() || movies.isEmpty())) {
				similarityService.init();
				List<List<Movie>> batchList = createBatchs(movies, batchSize);
				for (List<Movie> batchMovieList : batchList) {
					CompletableFuture<Boolean> completable = new CompletableFuture<Boolean>();
					int batchMovieSize = batchMovieList.size();
					AtomicInteger batchCounter = new AtomicInteger(1);
					for (int i = 0; i < batchMovieSize; i++) {
						final Movie movie = batchMovieList.get(i);
						AsyncUtils.async(() -> {
							ItemValue<Movie> mv = null;
							try {
								mv = new ItemValue<Movie>(movie, similarityService.getSimilarity(tokens, movie.getTokensList()), true);
								simList.add(mv);
							} catch(Exception e) {
								LOGGER.error(e.getMessage(), e);
							}
							
							int v = batchCounter.getAndIncrement();	
							if(LOGGER.isDebugEnabled()) {
								LOGGER.debug(v + " " + mv);
							}
							if(v == batchMovieSize) {
								completable.complete(true);
							}
						});
					}
					
					try {
						completable.get();
					} catch (Exception e) {
						if(LOGGER.isErrorEnabled()) {
							LOGGER.error(e.getCause().getMessage(), e);
						}
					}
					
					if(LOGGER.isDebugEnabled()) {
						LOGGER.debug(simList.size() + " - " + simList.size() / ((double) movies.size()) * 100 + "%");
					}
				}
				similarityService.reset();
				simList.sort((ItemValue<Movie> a, ItemValue<Movie> b) -> a.compareTo(b));
			}
			int max = simList.size();
			if(qtMovies > 0) {
				max = Math.min(qtMovies, max);
			}
			return simList.subList(0, max);	
		} else {
			throw new IllegalStateException("a userMovieSimilarity service must be setted");
		}
	}	
	
	public void updateRecomendations(User user, int qtdMovies)
	{
		
	}
	
	private List<List<Movie>> createBatchs(List<Movie> siouvList, int pageSize) 
	{
	    if (siouvList == null)
	        return  new ArrayList<List<Movie>>();
	    List<Movie> list = new ArrayList<Movie>(siouvList);
	    if (pageSize <= 0 || pageSize > list.size())
	        pageSize = list.size();
	    int numPages = (int) Math.ceil((double)list.size() / (double)pageSize);
	    List<List<Movie>> pages = new ArrayList<List<Movie>>(numPages);
	    for (int pageNum = 0; pageNum < numPages;)
	        pages.add(list.subList(pageNum * pageSize, Math.min(++pageNum * pageSize, list.size())));
	    return pages;
	}
}
