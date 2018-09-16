package br.dcc.ufba.themoviefinder.services;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.dcc.ufba.themoviefinder.entities.models.LodCounter;
import br.dcc.ufba.themoviefinder.entities.models.LodRelationCounter;
import br.dcc.ufba.themoviefinder.entities.services.LodCacheCounterService;
import br.dcc.ufba.themoviefinder.lodweb.Sparql;
import br.dcc.ufba.themoviefinder.lodweb.SparqlWalk;

@Component
public class RLWSimilarity 
{
	@Autowired
	private SparqlWalk sparqlWalk;
	
	@Value("${app.rlw-use-cache: true}")
	public boolean useCache;
	
	@Autowired
	private LodCacheCounterService lodCacheService;
	
	public double getSimilarity(List<String> terms1, List<String> terms2, double directWeight, double indirectWeight)
	{
		if(ObjectUtils.allNotNull(terms1, terms2)) {
			if(terms1.equals(terms2)) {
				return 1;
			}
			terms1 = uniqueValues(terms1);
			terms2 = uniqueValues(terms2);
			double similarity = 0;
		
			System.out.println(terms1);
			System.out.println(terms2);
			
			for (String term1 : terms1) {
				for (String term2 : terms2) {
					double temp = getSimilarityBetween2Terms(term1, term2, directWeight, indirectWeight);
					//System.out.println(term1 + " " + term2 + " " + temp);
					similarity += temp;
				}
			}
			return Math.min(1, similarity / terms1.size());
		} else {
			throw new NullPointerException("terms1 and terms2 must not be null");
		}
	}
	
	public double getSimilarityBetween2Terms(String term1, String term2, double directWeight, double indirectWeight)
	{
		if(ObjectUtils.allNotNull(term1, term2)) {
			if(term1.equalsIgnoreCase(term2)) {
				return 1;
			}
			String term1Resource = Sparql.wrapStringAsResource(term1);
			String term2Resource = Sparql.wrapStringAsResource(term2);
			if(resourceExists(term1Resource) && resourceExists(term2Resource)) {
				
				double totalTermDirect = 0, totalTermIndirect = 0, totalDirect = 0, totalIndirect = 0; 
				
				if(useCache) {
					LodCounter lodCounter1 = lodCacheService.getResourceCache(term1Resource);
					LodCounter lodCounter2 = lodCacheService.getResourceCache(term2Resource);
					if(lodCounter1 == null) {
						lodCounter1 = new LodCounter(term1Resource);
						lodCounter1.setDirectLinks(sparqlWalk.countDirectLinksFromResource(term1Resource));
						lodCounter1.setIndirectLinks(sparqlWalk.countIndirectLinksFromResource(term1Resource));
						lodCacheService.saveResourceToCache(lodCounter1);
					}
					if(lodCounter2 == null) {
						lodCounter2 = new LodCounter(term2Resource);
						lodCounter2.setDirectLinks(sparqlWalk.countDirectLinksFromResource(term2Resource));
						lodCounter2.setIndirectLinks(sparqlWalk.countIndirectLinksFromResource(term2Resource));
						lodCacheService.saveResourceToCache(lodCounter2);
					}
					totalTermDirect = lodCounter1.getDirectLinks() + lodCounter2.getDirectLinks();
					totalTermIndirect = lodCounter1.getIndirectLinks() + lodCounter2.getIndirectLinks();
					
					LodRelationCounter lodRelation = lodCacheService.getResourceRelationCache(term1Resource, term2Resource);
					if(lodRelation == null) {
						lodRelation = new LodRelationCounter(term1Resource, term2Resource);
						lodRelation.setDirectLinks(sparqlWalk.countDirectLinksBetween2Resources(term1Resource, term2Resource));
						lodRelation.setIndirectLinks(sparqlWalk.countIndirectLinksBetween2Resources(term1Resource, term2Resource));
						lodCacheService.saveResourceRelationToCache(lodRelation);
					}
					
					totalDirect = lodRelation.getDirectLinks();
					totalIndirect = lodRelation.getIndirectLinks();
				} else {
					totalTermDirect = sparqlWalk.countDirectLinksFromResource(term1Resource) + sparqlWalk.countDirectLinksFromResource(term2Resource);
					totalTermIndirect = sparqlWalk.countIndirectLinksFromResource(term1Resource) + sparqlWalk.countIndirectLinksFromResource(term2Resource);
					totalDirect = sparqlWalk.countDirectLinksBetween2Resources(term1Resource, term2Resource);
					totalIndirect = sparqlWalk.countIndirectLinksBetween2Resources(term1Resource, term2Resource);
				}
				
				//System.out.println(totalTermDirect + " " + totalTermIndirect + " " + totalDirect + " " + totalIndirect);
				
				double sDirect = totalDirect / (1 + Math.log(totalTermDirect)) * directWeight;
				double sIndirect = totalIndirect / (1 + Math.log(totalTermIndirect)) * indirectWeight;
				return Math.min(1, (sDirect + sIndirect) / (directWeight + indirectWeight));
			}
		} else {
			throw new NullPointerException("terms1 and terms2 must not be null");
		}
		return 0;
	}
	
	private List<String> uniqueValues(List<String> listValues)
	{
		return new ArrayList<String>(new TreeSet<String>(listValues));
	}
	
	private boolean resourceExists(String resource)
	{
		if(useCache) {
			if(lodCacheService.getResourceCache(resource) != null) {
				return true;
			}
		}
		return sparqlWalk.resourceExists(resource);
	}
}
