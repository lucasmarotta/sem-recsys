package br.dcc.ufba.themoviefinder.services.similarity;

import java.util.List;

import br.dcc.ufba.themoviefinder.entities.models.Movie;
import br.dcc.ufba.themoviefinder.entities.models.RecommendationType;
import br.dcc.ufba.themoviefinder.entities.models.User;
import br.dcc.ufba.themoviefinder.services.RecommendationModel;

public interface SimilarityService
{
	public RecommendationType getType();
	public void init();
	public double getSimilarityFromMovie(Movie movie1, Movie movie2) throws Exception;
	public double getSimilarityFromUser(User user, Movie movie, RecommendationModel recModel) throws Exception;
	public double getSimilarity(List<String> queryTokens, List<String> docTokens) throws Exception;
	public void updateCache(List<String> queryTokens, List<Movie> movies);
	public void resetCache();
	public void close();
}
