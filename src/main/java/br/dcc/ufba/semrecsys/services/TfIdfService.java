package br.dcc.ufba.semrecsys.services;

import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dcc.ufba.semrecsys.models.Idf;
import br.dcc.ufba.semrecsys.models.Movie;
import br.dcc.ufba.semrecsys.repositories.IdfRepository;

@Service
public class TfIdfService 
{
	@Autowired
	private IdfRepository idfRepository;
	
	@Autowired
	private MoviesService movieService;
	
	private long docCounter;
	
	private TFIDFSimilarity tfIdfSimilarity;
	
	public TfIdfService()
	{
		tfIdfSimilarity = new ClassicSimilarity();
	}
	
	public void updateIdf()
	{
		this.docCounter = movieService.countMovies();
		List<Movie> movies = movieService.getAllMovies();
		SortedSet<String> uniqueTokens = new TreeSet<String>();
		for (Movie movie : movies) {
			List<String> tokens = movie.getExtendedTokensList();
			for (String token : tokens) {
				uniqueTokens.add(token);
			}
		}
		for (String token : uniqueTokens) {
			if(token.length() > 0 && token != null) {
				Idf idf = new Idf();
				idf.setTerm(token);
				int counter = 0;
				int extendedCounter = 0;
				
				for (Movie movie : movies) {
					if(movie.getExtendedTokensList().contains(token)) {
						extendedCounter++;
					}
					if(movie.getTokensList().contains(token)) {
						counter++;
					}
				}
				
				idf.setValue(tfIdfSimilarity.idf(counter, docCounter));
				idf.setExtendedValue(tfIdfSimilarity.idf(extendedCounter, docCounter));
				idfRepository.save(idf);
			}
		}
	}
	
	public Float getTfIdfExtended(Collection<String> terms, String term)
	{
		Idf idf = idfRepository.findByTerm(term);
		if(idf != null) {
			float freq = 0f;
			for (String token : terms) {
				if(token.equalsIgnoreCase(term)) {
					freq++;
				}
			}
			return tfIdfSimilarity.tf(freq/terms.size()) * idf.getExtendedValue();
		}
		return null;
	}
	
	public Float getTfIdf(Collection<String> terms, String term)
	{
		Idf idf = idfRepository.findByTerm(term);
		if(idf != null) {
			float freq = 0f;
			for (String token : terms) {
				if(token.equalsIgnoreCase(term)) {
					freq++;
				}
			}
			return tfIdfSimilarity.tf(freq/terms.size()) * idf.getValue();
		}
		return null;
	}
}
