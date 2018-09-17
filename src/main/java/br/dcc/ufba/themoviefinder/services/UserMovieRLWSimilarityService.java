package br.dcc.ufba.themoviefinder.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.dcc.ufba.themoviefinder.entities.models.Movie;
import br.dcc.ufba.themoviefinder.entities.models.User;

@Service
public class UserMovieRLWSimilarityService implements UserMovieSimilarity
{
	@Autowired
	private RLWSimilarity rlwSimilarity;
	
	@Value("${app.rlw-use-cache: true}")
	public boolean useCache;

	public void setDirectWeight(double directWeight) 
	{
		rlwSimilarity.setDirectWeight(directWeight);
	}

	public void setIndirectWeight(double indirectWeight) 
	{
		rlwSimilarity.setIndirectWeight(indirectWeight);
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
		return rlwSimilarity.getSimilarityBetween2Terms(term1, term2, useCache);
	}

	@Override
	public double getSimilarity(List<String> queryTokens, List<String> docTokens) 
	{
		return rlwSimilarity.getSimilarity(queryTokens, docTokens, useCache);
	}
}
