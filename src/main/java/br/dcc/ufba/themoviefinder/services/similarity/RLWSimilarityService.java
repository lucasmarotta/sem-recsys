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
import br.dcc.ufba.themoviefinder.entities.models.RecommendationType;
import br.dcc.ufba.themoviefinder.entities.models.User;
import br.dcc.ufba.themoviefinder.exception.ResourceNotFoundException;
import br.dcc.ufba.themoviefinder.services.RecommendationModel;
import br.dcc.ufba.themoviefinder.services.cache.LocalCacheService;
import br.dcc.ufba.themoviefinder.utils.TFIDFCalculator;

@Service
public class RLWSimilarityService implements SimilarityService
{
	@Value("${app.rlws-use-cache: true}")
	private boolean useCache;
	
	@Autowired
	private LocalCacheService localCache;
	
	@Autowired
	private RLWSimilarity rlwSimilarity;
	
	private RecommendationType type = RecommendationType.RLWS_DIRECT;
	
	private static final Logger LOGGER = LogManager.getLogger(RLWSimilarityService.class);
	
	@Override
	public RecommendationType getType() 
	{
		return type;
	}
	
	public void setType(RecommendationType type)
	{
		this.type = type;
	}
	
	public void setWeights(double directWeight, double indirectWeight) 
	{
		if(rlwSimilarity != null) {
			rlwSimilarity.setDirectWeight(directWeight);
			rlwSimilarity.setIndirectWeight(indirectWeight);
		}
	}
	
	@Override
	public void init()
	{
		if(type.equals(RecommendationType.RLWS_DIRECT)) {
			setWeights(.8, .2);
		} else {
			setWeights(.2, .8);
		}
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("direct weight: " + rlwSimilarity.getDirectWeight() + " indirect weight: " + rlwSimilarity.getIndirectWeight());
		}
		if(! useCache) {
			rlwSimilarity.setLocalCache(null);
		}
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
	public double getSimilarityFromUser(User user, Movie movie, RecommendationModel recModel) throws Exception 
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
	
	@Override
	public void close() 
	{
		
	}
}
