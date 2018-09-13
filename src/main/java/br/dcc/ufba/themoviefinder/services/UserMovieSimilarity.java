package br.dcc.ufba.themoviefinder.services;

import java.util.List;

import br.dcc.ufba.themoviefinder.models.Movie;
import br.dcc.ufba.themoviefinder.models.User;

public interface UserMovieSimilarity
{
	public double getSimilarityFromMovie(Movie movie1, Movie movie2);
	public double getSimilarityFromUser(User user, Movie movie);
	public double getSimilarity(List<String> queryTokens, List<String> docTokens);
}
