package br.dcc.ufba.themoviefinder.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dcc.ufba.themoviefinder.models.Idf;
import br.dcc.ufba.themoviefinder.models.Movie;
import br.dcc.ufba.themoviefinder.repositories.IdfRepository;

@Service
public class TfIdfService 
{
	@Autowired
	private IdfRepository idfRepo;
	
	@Autowired
	private MovieService movieService;
	
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
				idfRepo.save(idf);
			}
		}
	}
	
	public float getTfIdfExtended(Collection<String> terms, String term)
	{
		Idf idf = idfRepo.findByTerm(term);
		if(idf != null) {
			float freq = 0f;
			for (String token : terms) {
				if(token.equalsIgnoreCase(term)) {
					freq++;
				}
			}
			return tfIdfSimilarity.tf(freq) * idf.getExtendedValue() * tfIdfSimilarity.lengthNorm(terms.size());
		}
		return 0f;
	}
	
	public float getTfIdf(Collection<String> terms, String term)
	{
		Idf idf = idfRepo.findByTerm(term);
		if(idf != null) {
			float freq = 0f;
			for (String token : terms) {
				if(token.equalsIgnoreCase(term)) {
					freq++;
				}
			}
			return tfIdfSimilarity.tf(freq) * idf.getValue() * tfIdfSimilarity.lengthNorm(terms.size());
		}
		return 0f;
	}
	
	public List<Float> getBulkTfIdf(Collection<String> terms, String[] toCompareTerms)
	{
		List<Float> tfIdfs = new ArrayList<Float>();
		List<Idf> idfList = idfRepo.findByTermIn(toCompareTerms);
		for (Idf idf : idfList) {
			float freq = 0f;
			for (String token : terms) {
				if(token.equalsIgnoreCase(idf.getTerm())) {
					freq++;
				}
			}
			tfIdfs.add(tfIdfSimilarity.tf(freq) * idf.getValue() * tfIdfSimilarity.lengthNorm(terms.size()));
		}
		return tfIdfs;
	}
	
	public List<Float> getBulkTfIdfExtended(Collection<String> terms, String[] toCompareTerms)
	{
		List<Float> tfIdfs = new ArrayList<Float>();
		List<Idf> idfList = idfRepo.findByTermIn(toCompareTerms);
		for (Idf idf : idfList) {
			float freq = 0f;
			for (String token : terms) {
				if(token.equalsIgnoreCase(idf.getTerm())) {
					freq++;
				}
			}
			tfIdfs.add(tfIdfSimilarity.tf(freq) * idf.getExtendedValue() * tfIdfSimilarity.lengthNorm(terms.size()));
		}
		return tfIdfs;
	}
}
