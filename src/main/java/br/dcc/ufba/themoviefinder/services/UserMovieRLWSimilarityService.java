package br.dcc.ufba.themoviefinder.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dcc.ufba.themoviefinder.lodweb.RLWSimilarity;
import br.dcc.ufba.themoviefinder.models.Movie;
import br.dcc.ufba.themoviefinder.models.User;

@Service
public class UserMovieRLWSimilarityService implements UserMovieSimilarity
{
	@Autowired
	private RLWSimilarity rlwSimilarity;
	
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

	@Override
	public double getSimilarity(List<String> queryTokens, List<String> docTokens) 
	{
		return Math.min(1.0, rlwSimilarity.getSimilarityWithWeights(queryTokens, docTokens, 0.7, 0.3));
	}
}
