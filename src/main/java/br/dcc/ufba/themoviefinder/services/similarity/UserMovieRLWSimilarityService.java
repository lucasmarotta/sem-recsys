package br.dcc.ufba.themoviefinder.services.similarity;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import br.dcc.ufba.themoviefinder.entities.models.Movie;
import br.dcc.ufba.themoviefinder.entities.models.User;
import br.dcc.ufba.themoviefinder.exception.ResourceNotFoundException;
import br.dcc.ufba.themoviefinder.services.LocalLodCacheService;

@Service
public class UserMovieRLWSimilarityService implements UserMovieSimilarityService
{
	@Value("${app.rlw-use-cache: true}")
	public boolean useCache;
	
	@Autowired
	private LocalLodCacheService localCache;
	
	@Autowired
	private ConfigurableApplicationContext springContext;
	
	@Value("${app.rlw-direct-weight: 0.8}")
	private double directWeight;
	
	@Value("${app.rlw-indirect-weight: 0.2}")
	private double indirectWeight;
	
	private static final Logger LOGGER = LogManager.getLogger(UserMovieRLWSimilarityService.class);

	@Override
	public void init() 
	{
		localCache.updateNotResourceCache();
	}
	
	@Override
	public void reset() 
	{
		localCache.clear();
	}
	
	public void setDirectWeight(double directWeight) 
	{
		this.directWeight = directWeight;
	}

	public void setIndirectWeight(double indirectWeight) 
	{
		this.indirectWeight = indirectWeight;
	}

	@Override
	public double getSimilarityFromMovie(Movie movie1, Movie movie2) 
	{
		return getSimilarity(movie1.getTokensList(), movie2.getTokensList());
	}

	@Override
	public double getSimilarityFromUser(User user, Movie movie) 
	{
		List<String> userTokens = new ArrayList<String>();
		for(Movie userMovie : user.getMovies()) {
			userTokens.addAll(userMovie.getTokensList());
		}
		return getSimilarity(userTokens, movie.getTokensList());
	}
	
	public double getSimilarityBetween2Terms(String term1, String term2)
	{
		try {
			return getRLWSimilarity().getSimilarityBetween2Terms(term1, term2);
		} catch (ResourceNotFoundException e) {
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug(e.getMessage(), e);
			}
		}
		return 0;
	}

	@Override
	public double getSimilarity(List<String> queryTokens, List<String> docTokens) 
	{
		return getRLWSimilarity().getSimilarity(queryTokens, docTokens);
	}
	
	private RLWSimilarity getRLWSimilarity()
	{
		RLWSimilarity rlwSimilarity = springContext.getBean(RLWSimilarity.class);
		rlwSimilarity.setDirectWeight(directWeight);
		rlwSimilarity.setIndirectWeight(indirectWeight);
		if(useCache) {
			rlwSimilarity.setLocalCache(springContext.getBean(LocalLodCacheService.class));	
		}
		return rlwSimilarity;
	}
}
