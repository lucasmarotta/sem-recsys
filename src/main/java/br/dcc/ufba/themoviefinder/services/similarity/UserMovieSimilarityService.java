package br.dcc.ufba.themoviefinder.services.similarity;

import java.util.List;

import br.dcc.ufba.themoviefinder.entities.models.Movie;
import br.dcc.ufba.themoviefinder.entities.models.User;

public interface UserMovieSimilarityService
{
	public void init();
	public double getSimilarityFromMovie(Movie movie1, Movie movie2) throws Exception;
	public double getSimilarityFromUser(User user, Movie movie) throws Exception;
	public double getSimilarity(List<String> queryTokens, List<String> docTokens) throws Exception;
	public void reset();
}