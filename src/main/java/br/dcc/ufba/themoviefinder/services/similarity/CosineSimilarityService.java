package br.dcc.ufba.themoviefinder.services.similarity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dcc.ufba.themoviefinder.entities.models.Movie;
import br.dcc.ufba.themoviefinder.entities.models.RecommendationType;
import br.dcc.ufba.themoviefinder.entities.models.User;
import br.dcc.ufba.themoviefinder.entities.services.TfIdfService;
import br.dcc.ufba.themoviefinder.services.RecommendationModel;
import br.dcc.ufba.themoviefinder.utils.CosineSimilarity;
import br.dcc.ufba.themoviefinder.utils.DocVector;

@Service
public class CosineSimilarityService implements SimilarityService
{
	@Autowired
	private TfIdfService tfIdfService;
	
	@Override
	public double getSimilarityFromMovie(Movie movie1, Movie movie2)
	{
		return getSimilarity(movie1.getTokensList(), movie2.getTokensList());
	}
	
	@Override
	public double getSimilarityFromUser(User user, Movie movie, RecommendationModel recModel)
	{
		List<String> userTokens = new ArrayList<String>();
		for(Movie userMovie : user.getMoviesRecModel(recModel)) {
			userTokens.addAll(userMovie.getTokensList());
		}
		return getSimilarity(userTokens, movie.getTokensList());
	}
	
	@Override
	public double getSimilarity(List<String> queryTokens, List<String> docTokens)
	{
		DocVector queryVector = new DocVector(queryTokens);
		DocVector docVector = new DocVector(queryTokens);
		String[] uniqueTerms = queryVector.vectorTerms.keySet().toArray(new String[queryVector.vectorTerms.size()]);
		List<Float> queryTfIdfList = null;
		List<Float> docTfIdfList = null;
		
		queryTfIdfList = tfIdfService.getBulkTfIdf(queryTokens, uniqueTerms);
		docTfIdfList = tfIdfService.getBulkTfIdf(docTokens, uniqueTerms);
		
		int size = queryTfIdfList.size();
		for (int i = 0; i < size; i++) {
			queryVector.setVectorValue(uniqueTerms[i], queryTfIdfList.get(i));
			docVector.setVectorValue(uniqueTerms[i], docTfIdfList.get(i));
		}
		return CosineSimilarity.getSimilarity(queryVector, docVector);
	}
	
	@Override
	public RecommendationType getType() 
	{
		return RecommendationType.COSINE;
	}

	@Override
	public void init() 
	{
		//tfIdfService.updateIdf();
	}
	
	@Override
	public void updateCache(List<String> queryTokens, List<Movie> movies)
	{
		
	}

	@Override
	public void resetCache()
	{
		
	}
	
	@Override
	public void close()
	{
		
	}
}
