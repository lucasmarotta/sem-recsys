package br.dcc.ufba.themoviefinder.services.similarity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.dcc.ufba.themoviefinder.entities.models.LodCache;
import br.dcc.ufba.themoviefinder.entities.models.LodCacheRelation;
import br.dcc.ufba.themoviefinder.entities.models.LodRelationId;
import br.dcc.ufba.themoviefinder.entities.models.NotResource;
import br.dcc.ufba.themoviefinder.entities.services.LodCacheService;
import br.dcc.ufba.themoviefinder.exception.ResourceNotFoundException;
import br.dcc.ufba.themoviefinder.lodweb.Sparql;
import br.dcc.ufba.themoviefinder.lodweb.SparqlWalk;
import br.dcc.ufba.themoviefinder.utils.TFIDFCalculator;

@Component
public class RLWSimilarity 
{
	@Autowired
	private SparqlWalk sparqlWalk;
	
	@Autowired
	private LodCacheService lodCacheService;
	
	private Set<LodCache> localLodCache;
	private Set<LodCacheRelation> localLodCacheRelation;
	private Set<NotResource> notResource;
	private double directWeight = 0.8;
	private double indirectWeight = 0.2;
	private static final Logger LOGGER = LogManager.getLogger(RLWSimilarity.class);
	
	public RLWSimilarity()
	{
		localLodCache = new HashSet<LodCache>();
		localLodCacheRelation = new HashSet<LodCacheRelation>();
		notResource = new HashSet<NotResource>();
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
	
	public double getSimilarity(List<String> terms1, List<String> terms2, boolean useCache)
	{
		if(ObjectUtils.allNotNull(terms1, terms2)) {
			if(terms1.equals(terms2)) {
				return 1;
			}
			double similarity = 0;
			terms1 = TFIDFCalculator.uniqueValues(terms1);
			terms2 = TFIDFCalculator.uniqueValues(terms2);
			if(LOGGER.isTraceEnabled()) {
				LOGGER.trace(terms1);
				LOGGER.trace(terms2);
			}
			updateLocalCache(terms1, terms2);
			int combinations = 0;
			for (String term1 : terms1) {
				for (String term2 : terms2) {
					try {
						similarity += getSimilarityBetween2Terms(term1, term2, useCache);
						combinations++;
					} catch (ResourceNotFoundException e) {
						if(LOGGER.isTraceEnabled()) {
							LOGGER.trace(e.getMessage(), e);
						}
					}
				}
			}
			
			//Average similarity
			if(combinations > 0) {
				return similarity / combinations;
			}
			return 0;
		} else {
			throw new NullPointerException("terms1 and terms2 must not be null");
		}
	}
	
	public double getSimilarityBetween2Terms(String term1, String term2, boolean useCache) throws ResourceNotFoundException
	{
		if(ObjectUtils.allNotNull(term1, term2)) {
			if(term1.equalsIgnoreCase(term2)) {
				return 1;
			}
			double totalTermDirect = 0, totalTermIndirect = 0, totalDirect = 0, totalIndirect = 0;
			String term1Resource = Sparql.wrapStringAsResource(term1);
			String term2Resource = Sparql.wrapStringAsResource(term2);
			if(useCache) {
				LodCache lodCache1 = findOnLocalLodCache(term1);
				LodCache lodCache2 = findOnLocalLodCache(term2);
				if((lodCache1 != null && lodCache2 != null) || (isResource(term2) && isResource(term1))) {
					if(lodCache1 == null) {
						lodCache1 = new LodCache(term1);
						lodCache1.setDirectLinks(sparqlWalk.countDirectLinksFromResource(term1Resource)); 
						lodCache1.setIndirectLinks(sparqlWalk.countIndirectLinksFromResource(term1Resource));
						lodCacheService.saveResource(lodCache1);
						localLodCache.add(lodCache1);
					}
					if(lodCache2 == null) {
						lodCache2 = new LodCache(term2);
						lodCache2.setDirectLinks(sparqlWalk.countDirectLinksFromResource(term2Resource));
						lodCache2.setIndirectLinks(sparqlWalk.countIndirectLinksFromResource(term2Resource));
						lodCacheService.saveResource(lodCache2);
						localLodCache.add(lodCache2);
					}
					totalTermDirect = lodCache1.getDirectLinks() + lodCache2.getDirectLinks();
					totalTermIndirect = lodCache1.getIndirectLinks() + lodCache2.getIndirectLinks();
					LodCacheRelation lodCacheRelation = findOnLocalLodCacheRelation(term1, term2);
					if(lodCacheRelation == null) {
						lodCacheRelation = new LodCacheRelation(term1, term2);
						if(sparqlWalk.isRedirect(term1Resource, term2Resource)) {
							lodCacheRelation.setDirectLinks((int) totalTermDirect);
							lodCacheRelation.setIndirectLinks((int) totalTermIndirect);
						} else {
							lodCacheRelation.setDirectLinks(sparqlWalk.countDirectLinksBetween2Resources(term1Resource, term2Resource));
							lodCacheRelation.setIndirectLinks(sparqlWalk.countIndirectLinksBetween2Resources(term1Resource, term2Resource));	
						}
						lodCacheService.saveResourceRelation(lodCacheRelation);
						localLodCacheRelation.add(lodCacheRelation);
						if(LOGGER.isTraceEnabled()) {
							LOGGER.trace(lodCacheRelation);
						}
					}
					totalDirect = lodCacheRelation.getDirectLinks();
					totalIndirect = lodCacheRelation.getIndirectLinks();	
				} else {
					throw new ResourceNotFoundException(String.format("%s and/or %s not found on dbpedia", term1, term2));
				}
			} else if(sparqlWalk.isResource(term1Resource) && sparqlWalk.isResource(term2Resource)) {
				totalTermDirect = sparqlWalk.countDirectLinksFromResource(term1Resource) + sparqlWalk.countDirectLinksFromResource(term2Resource);
				totalTermIndirect = sparqlWalk.countIndirectLinksFromResource(term1Resource) + sparqlWalk.countIndirectLinksFromResource(term2Resource);
				if(sparqlWalk.isRedirect(term1Resource, term2Resource)) {
					totalDirect = totalTermDirect;
					totalIndirect = totalTermIndirect;
				} else {
					totalDirect = sparqlWalk.countDirectLinksBetween2Resources(term1Resource, term2Resource);
					totalIndirect = sparqlWalk.countIndirectLinksBetween2Resources(term1Resource, term2Resource);	
				}
			}
			
			if(totalDirect < totalTermDirect) {
				totalTermDirect = Math.log(totalTermDirect);
			} else {
				totalDirect = totalTermDirect;
			}
			
			if(totalIndirect < totalTermIndirect) {
				totalTermIndirect = Math.log(totalTermIndirect);
			} else {
				totalIndirect = totalTermIndirect;
			}
			
			double sDirect = totalDirect / (1 + Math.log(totalTermDirect)) * directWeight;
			double sIndirect = totalIndirect / (1 + Math.log(totalTermIndirect)) * indirectWeight;
			return 1 - 1 / (1 + (sDirect + sIndirect) / (directWeight + indirectWeight));
		} else {
			throw new NullPointerException("terms1 and terms2 must not be null");
		}
	}
	
	public void clearLocalCache()
	{
		localLodCache.clear();
		localLodCacheRelation.clear();
	}
	
	public void updateNotResource()
	{
		notResource.clear();
		notResource.addAll(lodCacheService.getAllNotResource());
	}
	
	public void clearNotResource()
	{
		notResource.clear();
	}
	
	private void updateLocalCache(List<String> terms1, List<String> terms2)
	{
		List<String> allTerms = new ArrayList<String>(terms1);
		allTerms.addAll(terms2);
		localLodCache = new HashSet<LodCache>(lodCacheService.getResourceList(TFIDFCalculator.uniqueValues(allTerms)));
		List<LodRelationId> lodRelationIds = new ArrayList<LodRelationId>();
		for (String term1 : terms1) {
			for (String term2 : terms2) {
				if(! term1.equalsIgnoreCase(term2)) {
					lodRelationIds.add(new LodRelationId(term1, term2));
				}
			}
		}
		localLodCacheRelation = new HashSet<LodCacheRelation>(lodCacheService.getResourceRelationList(lodRelationIds));
		if(LOGGER.isTraceEnabled()) {
			LOGGER.trace("Search size " + lodRelationIds.size() + ", Local Lod Cache Size " + localLodCache.size() + ", Local Lod Cache Relation " + localLodCacheRelation.size());
		}
	}
	
	private LodCache findOnLocalLodCache(String resource)
	{
		LodCache toFind = new LodCache(resource);
		for (LodCache lodCache : localLodCache) {
			if(toFind.equals(lodCache)) {
				return lodCache;
			}
		}
		return null;
	}
	
	private LodCacheRelation findOnLocalLodCacheRelation(String resource1, String resource2)
	{
		LodCacheRelation toFind = new LodCacheRelation(resource1, resource2);
		for (LodCacheRelation lodCacheRelation : localLodCacheRelation) {
			if(toFind.equals(lodCacheRelation)) {
				return lodCacheRelation;
			}
		}
		return null;
	}
	
	private boolean isResource(String term)
	{
		NotResource nr = new NotResource(term);
		boolean isNotResource = notResource.contains(nr);
		if(! localLodCache.contains(new LodCache(term)) && (isNotResource || ! sparqlWalk.isResource(Sparql.wrapStringAsResource(term)))) {
			if(! isNotResource) {
				notResource.add(nr);
				lodCacheService.saveNotResource(nr);
			}
			return false;
		}	
		return true;
	}
}
