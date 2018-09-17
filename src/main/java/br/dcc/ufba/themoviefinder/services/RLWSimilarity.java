package br.dcc.ufba.themoviefinder.services;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.dcc.ufba.themoviefinder.entities.models.LodCache;
import br.dcc.ufba.themoviefinder.entities.models.LodCacheRelation;
import br.dcc.ufba.themoviefinder.entities.models.LodRelationId;
import br.dcc.ufba.themoviefinder.entities.services.LodCacheService;
import br.dcc.ufba.themoviefinder.lodweb.Sparql;
import br.dcc.ufba.themoviefinder.lodweb.SparqlWalk;

@Component
public class RLWSimilarity 
{
	@Autowired
	private SparqlWalk sparqlWalk;
	
	@Autowired
	private LodCacheService lodCacheService;
	
	private List<LodCache> localLodCache;
	private List<LodCacheRelation> localLodCacheRelation;
	private double directWeight = 0.65;
	private double indirectWeight = 0.35;
	
	public RLWSimilarity()
	{
		localLodCache = new ArrayList<LodCache>();
		localLodCacheRelation = new ArrayList<LodCacheRelation>();
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
			terms1 = uniqueValues(terms1);
			terms2 = uniqueValues(terms2);
			System.out.println(terms1);
			System.out.println(terms2);
	
			updateLocalCache(terms1, terms2);
			for (String term1 : terms1) {
				for (String term2 : terms2) {
					double temp = getSimilarityBetween2Terms(term1, term2, useCache);
					System.out.println(term1 + " " + term2 + " " + temp);
					similarity += temp;
				}
			}
			return Math.min(1, similarity / terms1.size());
		} else {
			throw new NullPointerException("terms1 and terms2 must not be null");
		}
	}
	
	public double getSimilarityBetween2Terms(String term1, String term2, boolean useCache)
	{
		if(ObjectUtils.allNotNull(term1, term2)) {
			if(term1.equalsIgnoreCase(term2)) {
				return 1;
			}
			double totalTermDirect = 0, totalTermIndirect = 0, totalDirect = 0, totalIndirect = 0;
			term1 = Sparql.wrapStringAsResource(term1);
			term2 = Sparql.wrapStringAsResource(term2);
			
			if(useCache) {
				LodCache lodCache1 = findOnLocalLodCache(term1);
				LodCache lodCache2 = findOnLocalLodCache(term2);
				if((lodCache1 != null && lodCache2 != null) || (sparqlWalk.resourceExists(term1) && sparqlWalk.resourceExists(term2))) {
					if(lodCache1 == null) {
						lodCache1 = new LodCache(term1);
						lodCache1.setDirectLinks(sparqlWalk.countDirectLinksFromResource(term1)); 
						lodCache1.setIndirectLinks(sparqlWalk.countIndirectLinksFromResource(term1));
						lodCacheService.saveResource(lodCache1);
						localLodCache.add(lodCache1);
					}
					if(lodCache2 == null) {
						lodCache2 = new LodCache(term2);
						lodCache2.setDirectLinks(sparqlWalk.countDirectLinksFromResource(term2));
						lodCache2.setIndirectLinks(sparqlWalk.countIndirectLinksFromResource(term2));
						lodCacheService.saveResource(lodCache2);
						localLodCache.add(lodCache2);
					}
					totalTermDirect = lodCache1.getDirectLinks() + lodCache2.getDirectLinks();
					totalTermIndirect = lodCache1.getIndirectLinks() + lodCache2.getIndirectLinks();
					LodCacheRelation lodCacheRelation = findOnLocalLodCacheRelation(term1, term2);
					if(lodCacheRelation == null) {
						lodCacheRelation = new LodCacheRelation(term1, term2);
						lodCacheRelation.setDirectLinks(sparqlWalk.countDirectLinksBetween2Resources(term1, term2));
						lodCacheRelation.setIndirectLinks(sparqlWalk.countIndirectLinksBetween2Resources(term1, term2));
						lodCacheService.saveResourceRelation(lodCacheRelation);
						localLodCacheRelation.add(lodCacheRelation);
					}
					totalDirect = lodCacheRelation.getDirectLinks();
					totalIndirect = lodCacheRelation.getIndirectLinks();	
				}	
			} else if(sparqlWalk.resourceExists(term1) && sparqlWalk.resourceExists(term2)) {
				totalTermDirect = sparqlWalk.countDirectLinksFromResource(term1) + sparqlWalk.countDirectLinksFromResource(term2);
				totalTermIndirect = sparqlWalk.countIndirectLinksFromResource(term1) + sparqlWalk.countIndirectLinksFromResource(term2);
				totalDirect = sparqlWalk.countDirectLinksBetween2Resources(term1, term2);
				totalIndirect = sparqlWalk.countIndirectLinksBetween2Resources(term1, term2);	
			}
			
			double sDirect = totalDirect / (1 + Math.log(totalTermDirect)) * directWeight;
			double sIndirect = totalIndirect / (1 + Math.log(totalTermIndirect)) * indirectWeight;
			//return Math.min(1, (sDirect + sIndirect) / (directWeight + indirectWeight));
			return 1 - 1 / (1 + (sDirect + sIndirect) / (directWeight + indirectWeight));
		} else {
			throw new NullPointerException("terms1 and terms2 must not be null");
		}
	}
	
	public void updateLocalCache(List<String> terms1, List<String> terms2)
	{
		List<String> allTerms = new ArrayList<String>(terms1);
		allTerms.addAll(terms2);
		localLodCache = lodCacheService.getResourceList(allTerms);
		localLodCacheRelation = getLodCacheRelationList(terms1, terms2);		
	}
	
	public void clearLocalCache()
	{
		localLodCache.clear();
		localLodCacheRelation.clear();
	}
	
	private List<String> uniqueValues(List<String> listValues)
	{
		return new ArrayList<String>(new TreeSet<String>(listValues));
	}
	
	private LodCache findOnLocalLodCache(String resource)
	{
		int index = localLodCache.indexOf(new LodCache(resource));
		if(index >= 0) {
			return localLodCache.get(index);
		}
		return null;
	}
	
	private LodCacheRelation findOnLocalLodCacheRelation(String resource1, String resource2)
	{
		int index = localLodCacheRelation.indexOf(new LodCacheRelation(resource1, resource2));
		if(index >= 0) {
			return localLodCacheRelation.get(index);
		}
		return null;
	}
	
	private List<LodCacheRelation> getLodCacheRelationList(List<String> resources1, List<String> resources2)
	{
		List<LodRelationId> lodRelationIds = new ArrayList<LodRelationId>();
		for (String resource1 : resources1) {
			for (String resource2 : resources2) {
				lodRelationIds.add(new LodRelationId(resource1, resource2));
			}
		}
		return lodCacheService.getResourceRelationList(lodRelationIds);
	}
}
