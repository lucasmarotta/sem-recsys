package br.dcc.ufba.themoviefinder.services.similarity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.dcc.ufba.themoviefinder.entities.models.Movie;
import br.dcc.ufba.themoviefinder.entities.models.RecomendationType;
import br.dcc.ufba.themoviefinder.entities.models.User;
import br.dcc.ufba.themoviefinder.exception.ResourceNotFoundException;
import br.dcc.ufba.themoviefinder.services.RecomendationModel;
import br.dcc.ufba.themoviefinder.services.cache.LocalCacheService;
import br.dcc.ufba.themoviefinder.utils.TFIDFCalculator;

@Service
public class UserMovieRLWSimilarityService implements UserMovieSimilarityService
{
	@Value("${app.rlw-use-cache: true}")
	private boolean useCache;
	
	@Autowired
	private LocalCacheService localCache;
	
	@Autowired
	private RLWSimilarity rlwSimilarity;
	
	@Value("${app.rlw-direct-weight: 0.8}")
	private double directWeight;
	
	@Value("${app.rlw-indirect-weight: 0.2}")
	private double indirectWeight;
	
	private static final Logger LOGGER = LogManager.getLogger(UserMovieRLWSimilarityService.class);
	
	@Override
	public RecomendationType getType() 
	{
		return RecomendationType.RLWS;
	}
	
	public void init()
	{
		rlwSimilarity.setDirectWeight(directWeight);
		rlwSimilarity.setIndirectWeight(indirectWeight);
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("direct weight: " + directWeight + " indirect weight: " + indirectWeight);
		}
		if(! useCache) {
			rlwSimilarity.setLocalCache(null);
		}		
	}
	
	public void setDirectWeight(double directWeight) 
	{
		this.directWeight = directWeight;
	}

	public void setIndirectWeight(double indirectWeight) 
	{
		this.indirectWeight = indirectWeight;
	}
	
	public boolean isUseCache() 
	{
		return useCache;
	}

	public void setUseCache(boolean useCache) 
	{
		this.useCache = useCache;
	}

	@Override
	public double getSimilarityFromMovie(Movie movie1, Movie movie2) throws Exception 
	{
		return getSimilarity(movie1.getTokensList(), movie2.getTokensList());
	}

	@Override
	public double getSimilarityFromUser(User user, Movie movie, RecomendationModel recModel) throws Exception 
	{
		List<String> userTokens = new ArrayList<String>();
		for(Movie userMovie : user.getMoviesRecModel(recModel)) {
			userTokens.addAll(userMovie.getTokensList());
		}
		return getSimilarity(userTokens, movie.getTokensList());
	}
	
	public double getSimilarityBetween2Terms(String term1, String term2)
	{
		try {
			if(useCache) {
				localCache.updateLocalCache(Arrays.asList(term1), Arrays.asList(term2));
			}
			return rlwSimilarity.getSimilarityBetween2Terms(term1, term2);
		} catch (ResourceNotFoundException e) {
			if(LOGGER.isTraceEnabled()) {
				LOGGER.trace(e.getMessage(), e);
			}
		}
		return 0;
	}

	@Override
	public double getSimilarity(List<String> queryTokens, List<String> docTokens)
	{
		return rlwSimilarity.getSimilarity(queryTokens, docTokens);
	}
	
	@Override
	public void updateCache(List<String> queryTokens, List<Movie> movies)
	{
		queryTokens = TFIDFCalculator.uniqueValues(queryTokens);
		for (Movie movie : movies) {
			localCache.updateLocalCache(queryTokens, TFIDFCalculator.uniqueValues(movie.getTokensList()));
		}
	}
	
	@Override
	public void resetCache()
	{
		if(useCache) {
			localCache.clear();
		}
	}
	
	public void close() 
	{
		
	}
}
