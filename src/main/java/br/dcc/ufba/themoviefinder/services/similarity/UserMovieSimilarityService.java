package br.dcc.ufba.themoviefinder.services.similarity;

import java.util.List;

import br.dcc.ufba.themoviefinder.entities.models.Movie;
import br.dcc.ufba.themoviefinder.entities.models.User;

public interface UserMovieSimilarityService
{
	public void init();
	public double getSimilarityFromMovie(Movie movie1, Movie movie2);
	public double getSimilarityFromUser(User user, Movie movie);
	public double getSimilarity(List<String> queryTokens, List<String> docTokens);
	public void reset();
}
