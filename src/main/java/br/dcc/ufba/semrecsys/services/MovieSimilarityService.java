package br.dcc.ufba.semrecsys.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dcc.ufba.semrecsys.models.Movie;
import br.dcc.ufba.semrecsys.models.User;
import br.dcc.ufba.semrecsys.utils.CosineSimilarity;
import br.dcc.ufba.semrecsys.utils.DocVector;

@Service
public class MovieSimilarityService 
{
	
	@Autowired
	private TfIdfService tfIdfService;
	private boolean extendedMode;
	
	public MovieSimilarityService()
	{
		extendedMode = true;
	}
	
	public boolean isExtendedMode() 
	{
		return extendedMode;
	}

	public void setExtendedMode(boolean extendedMode) 
	{
		this.extendedMode = extendedMode;
	}

	public Double getSimilarityFromMovie(Movie movie1, Movie movie2)
	{
		return CosineSimilarity.getSimilarity(getMovieDocVector(movie1), getMovieDocVector(movie2));
	}
	
	public Double getSimilarityFromUser(User user, Movie movie)
	{
		return CosineSimilarity.getSimilarity(getUserDocVetor(user), getMovieDocVector(movie));
	}
	
	private DocVector getUserDocVetor(User user)
	{
		List<String> userTokens = new ArrayList<String>();
		for(Movie userMovie : user.getMovies()) {
			if(extendedMode) {
				userTokens.addAll(userMovie.getExtendedTokensList());
			} else {
				userTokens.addAll(userMovie.getTokensList());
			}
		}
		DocVector userVector = new DocVector(userTokens);
		for (Map.Entry<String, Integer> entry : userVector.vectorTerms.entrySet()) {
			Float tfIdf = null;
			if(extendedMode) {
				tfIdf = tfIdfService.getTfIdfExtended(userTokens, entry.getKey());
			} else {
				tfIdf = tfIdfService.getTfIdf(userTokens, entry.getKey());
			}
			userVector.setVectorValue(entry.getKey(), tfIdf);
		}
		userVector.normalize();
		
		for (Movie movie : user.getMovies()) {
			System.out.println(movie.getTitle());
		}
		System.out.println(userVector);
		return userVector;		
	}
	
	private DocVector getMovieDocVector(Movie movie)
	{
		DocVector movieVector = null;
		if(extendedMode) {
			movieVector = new DocVector(movie.getExtendedTokensList());
		} else {
			movieVector = new DocVector(movie.getTokensList());
		}
		for (Map.Entry<String, Integer> entry : movieVector.vectorTerms.entrySet()) {
			Float tfIdf = null;
			if(extendedMode) {
				tfIdf = tfIdfService.getTfIdfExtended(movie.getExtendedTokensList(), entry.getKey());
			} else {
				tfIdf = tfIdfService.getTfIdf(movie.getTokensList(), entry.getKey());
			}
			movieVector.setVectorValue(entry.getKey(), tfIdf);
		}
		movieVector.normalize();
		System.out.println(movie.getTitle());
		System.out.println(movieVector);
		return movieVector;
	}
}
