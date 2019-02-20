package br.dcc.ufba.themoviefinder.services.similarity;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dcc.ufba.themoviefinder.entities.models.LodCache;
import br.dcc.ufba.themoviefinder.entities.models.LodCacheRelation;
import br.dcc.ufba.themoviefinder.exception.ResourceNotFoundException;
import br.dcc.ufba.themoviefinder.lodweb.Sparql;
import br.dcc.ufba.themoviefinder.lodweb.SparqlWalk;
import br.dcc.ufba.themoviefinder.services.cache.LocalCacheService;
import br.dcc.ufba.themoviefinder.utils.TFIDFCalculator;

@Service
public class RLWSimilarity
{
	@Autowired
	private LocalCacheService localCache;
	
	@Autowired
	private SparqlWalk sparqlWalk;
	
	private double directWeight = 0.1;
	private double indirectWeight = 0.9;
	private static final Logger LOGGER = LogManager.getLogger(RLWSimilarity.class);

	public LocalCacheService getLocalCache()
	{
		return localCache;
	}

	public void setLocalCache(LocalCacheService localCache)
	{
		this.localCache = localCache;
	}

	public double getDirectWeight()
	{
		return directWeight;
	}

	public void setDirectWeight(double directWeight)
	{
		this.directWeight = directWeight;
	}

	public double getIndirectWeight()
	{
		return indirectWeight;
	}

	public void setIndirectWeight(double indirectWeight)
	{
		this.indirectWeight = indirectWeight;
	}

	public double getSimilarity(List<String> terms1, List<String> terms2) throws Exception
	{
		if(ObjectUtils.allNotNull(terms1, terms2)) {
			if(terms1.equals(terms2)) {
				return 1;
			}
			terms1 = TFIDFCalculator.uniqueValues(terms1);
			terms2 = TFIDFCalculator.uniqueValues(terms2);
			if(LOGGER.isTraceEnabled()) {
				LOGGER.trace(terms1);
				LOGGER.trace(terms2);
			}
			if(localCache != null) {
				localCache.updateLocalCache(terms1, terms2);
			}
			if(terms2.size() > terms1.size()) {
				return calculateSimilarity(terms2, terms1);
			} else {
				return calculateSimilarity(terms1, terms2);
			}
		} else {
			throw new NullPointerException("terms1 and terms2 must not be null");
		}
	}

	public double getSimilarityBetween2Terms(String term1, String term2) throws ResourceNotFoundException
	{
		if(ObjectUtils.allNotNull(term1, term2)) {
			if(term1.equalsIgnoreCase(term2)) {
				return 1;
			}
			double totalDirect = 0, totalIndirect = 0, totalBetweenDirect = 0, totalBetweenIndirect = 0;
			String term1Resource = Sparql.wrapStringAsResource(term1);
			String term2Resource = Sparql.wrapStringAsResource(term2);
			if(localCache != null) {
				LodCache lodCache1 = localCache.findLodCache(term1);
				LodCache lodCache2 = localCache.findLodCache(term2);
				if(isLodCacheResource(lodCache1) && isLodCacheResource(lodCache2)) {
					totalDirect = lodCache1.getDirectLinks() + lodCache2.getDirectLinks();
					totalIndirect = lodCache1.getIndirectLinks() + lodCache2.getIndirectLinks();
					LodCacheRelation lodCacheRelation = localCache.findLodCacheRelation(term1, term2);
					if(LOGGER.isTraceEnabled()) {
						LOGGER.trace(lodCacheRelation);
					}
					totalBetweenDirect = lodCacheRelation.getDirectLinks();
					totalBetweenIndirect = lodCacheRelation.getIndirectLinks();
				} else {
					throw new ResourceNotFoundException(String.format("%s and/or %s not found on dbpedia", term1, term2));
				}
			} else {
				totalDirect = sparqlWalk.countDirectLinksFromResource(term1Resource) + sparqlWalk.countDirectLinksFromResource(term2Resource);
				totalIndirect = sparqlWalk.countIndirectLinksFromResource(term1Resource) + sparqlWalk.countIndirectLinksFromResource(term2Resource);
				totalBetweenDirect = sparqlWalk.countDirectLinksBetween2Resources(term1Resource, term2Resource);
				totalBetweenIndirect = sparqlWalk.countIndirectLinksBetween2Resources(term1Resource, term2Resource);				
			}
			
			if(totalBetweenDirect < totalDirect) {
				totalBetweenDirect++;
			} else {
				totalBetweenDirect = totalDirect;
			}
			if(totalBetweenIndirect < totalIndirect) {
				totalBetweenIndirect++;
			} else {
				totalBetweenIndirect = totalIndirect;
			}

			double pDirect = Math.log(totalBetweenDirect) / Math.log(totalDirect) * directWeight;
			double pIndirect = Math.log(totalBetweenIndirect) / Math.log(totalIndirect) * indirectWeight;
			if(directWeight + indirectWeight > 0) {
				return (pDirect + pIndirect) / (directWeight + indirectWeight);
			}
			return 0;
		} else {
			throw new NullPointerException("terms1 and terms2 must not be null");
		}
	}

	private boolean isLodCacheResource(LodCache lodCache)
	{
		if(lodCache.getDirectLinks() == 0) {
			return false;
		}
		return true;
	}
	
	private double calculateSimilarity(List<String> terms1, List<String> terms2) throws Exception
	{
		int combinations = 0;
		int similarity = 0;
		
		for (String term1 : terms1) {
			double bestSimilarity = -1;
			for (String term2 : terms2) {
				try {
					double s = getSimilarityBetween2Terms(term1, term2);
					if(s > bestSimilarity) {
						bestSimilarity = s;
					}
				} catch (ResourceNotFoundException e) {
					if(LOGGER.isTraceEnabled()) {
						LOGGER.trace(e.getMessage(), e);
					}
				}
			}
			if(bestSimilarity > -1) {
				similarity += bestSimilarity;
				combinations++;
			}
		}
		if(combinations > 0) {
			return similarity / combinations;
		}
		return 0;
	}
}
