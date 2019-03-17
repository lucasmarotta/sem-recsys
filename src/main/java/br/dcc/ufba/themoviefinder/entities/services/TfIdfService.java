package br.dcc.ufba.themoviefinder.entities.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.dcc.ufba.themoviefinder.entities.models.Idf;
import br.dcc.ufba.themoviefinder.entities.models.Movie;
import br.dcc.ufba.themoviefinder.entities.repositories.IdfRepository;
import br.dcc.ufba.themoviefinder.utils.BatchWorkLoad;
import br.dcc.ufba.themoviefinder.utils.TFIDFCalculator;

@Service
public class TfIdfService 
{
	@Autowired
	private IdfRepository idfRepo;
	
	@Autowired
	private MovieService movieService;
	
	public int batchMovieSize;
	
	private long docCounter;
	
	private TFIDFSimilarity tfIdfSimilarity;
	
	private static final Logger LOGGER = LogManager.getLogger(TfIdfService.class);
	
	public TfIdfService()
	{
		tfIdfSimilarity = new ClassicSimilarity();
		batchMovieSize = 1000;
	}
	
	public void updateIdf()
	{
		this.docCounter = movieService.countMovies();
		List<String> uniqueTokens = getUniqueMovieTokens();
		BatchWorkLoad<String> batchWorkLoad = new BatchWorkLoad<String>(5, uniqueTokens, false);
		try {
			batchWorkLoad.run(token -> {
				try {
					if(token != null && token.length() > 0) {
						Idf idf = new Idf();
						idf.setTerm(token);
						int counter = 0;
						
						
						Pageable pageRequest = PageRequest.of(0, batchMovieSize);
						Page<Movie> moviesPage = movieService.pageMovies(pageRequest);
						int qtPages = moviesPage.getTotalPages();
						for(Movie movie : moviesPage.getContent()) {
							if(movie.getTokensList().contains(token)) {
								counter++;
							}
						}
						for (int i = 1; i < qtPages; i++) {
							moviesPage = movieService.pageMovies(moviesPage.nextPageable());
							for(Movie movie : moviesPage.getContent()) {
								if(movie.getTokensList().contains(token)) {
									counter++;
								}
							}
						}
						
						idf.setValue(tfIdfSimilarity.idf(counter, docCounter));
						idfRepo.save(idf);
					}	
				} catch(Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
				return null;
			});
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		System.gc();
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
	
	private List<String> getUniqueMovieTokens()
	{
		List<String> uniqueTokens = new ArrayList<String>();
		Pageable pageRequest = PageRequest.of(0, batchMovieSize);
		Page<Movie> moviesPage = movieService.pageMovies(pageRequest);
		int qtPages = moviesPage.getTotalPages();
		for(Movie movie : moviesPage.getContent()) {
			uniqueTokens.addAll(movie.getTokensList());
		}
		for (int i = 1; i < qtPages; i++) {
			moviesPage = movieService.pageMovies(moviesPage.nextPageable());
			for(Movie movie : moviesPage.getContent()) {
				uniqueTokens.addAll(movie.getTokensList());
			}
		}
		return TFIDFCalculator.uniqueValues(uniqueTokens);
	}
}
