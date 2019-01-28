package br.dcc.ufba.themoviefinder.services.similarity;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import br.dcc.ufba.themoviefinder.entities.models.LodCache;
import br.dcc.ufba.themoviefinder.entities.models.LodCacheRelation;
import br.dcc.ufba.themoviefinder.entities.models.NotResource;
import br.dcc.ufba.themoviefinder.exception.ResourceNotFoundException;
import br.dcc.ufba.themoviefinder.lodweb.Sparql;
import br.dcc.ufba.themoviefinder.lodweb.SparqlWalk;
import br.dcc.ufba.themoviefinder.services.LocalLodCacheService;
import br.dcc.ufba.themoviefinder.utils.TFIDFCalculator;

@Service
@Scope("prototype")
public class RLWSimilarity
{
	@Autowired
	private SparqlWalk sparqlWalk;
	private LocalLodCacheService localCache;
	private double directWeight = 0.1;
	private double indirectWeight = 0.9;
	private static final Logger LOGGER = LogManager.getLogger(RLWSimilarity.class);

	public LocalLodCacheService getLocalCache()
	{
		return localCache;
	}

	public void setLocalCache(LocalLodCacheService localCache)
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

	public double getSimilarity(List<String> terms1, List<String> terms2)
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
			localCache.updateLocalLodCache(terms1, terms2);
			int combinations = 0;
			for (String term1 : terms1) {
				for (String term2 : terms2) {
					try {
						similarity += getSimilarityBetween2Terms(term1, term2);
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
				LodCache lodCache1 = localCache.findOnLocalLodCache(term1);
				LodCache lodCache2 = localCache.findOnLocalLodCache(term2);
				if((lodCache1 != null && lodCache2 != null) || (isResource(term2) && isResource(term1))) {
					if(lodCache1 == null) {
						lodCache1 = new LodCache(term1);
						lodCache1.setDirectLinks(sparqlWalk.countDirectLinksFromResource(term1Resource));
						lodCache1.setIndirectLinks(sparqlWalk.countIndirectLinksFromResource(term1Resource));
						localCache.saveLodCache(lodCache1);
					}
					if(lodCache2 == null) {
						lodCache2 = new LodCache(term2);
						lodCache2.setDirectLinks(sparqlWalk.countDirectLinksFromResource(term2Resource));
						lodCache2.setIndirectLinks(sparqlWalk.countIndirectLinksFromResource(term2Resource));
						localCache.saveLodCache(lodCache2);
					}
					totalDirect = lodCache1.getDirectLinks() + lodCache2.getDirectLinks();
					totalIndirect = lodCache1.getIndirectLinks() + lodCache2.getIndirectLinks();
					LodCacheRelation lodCacheRelation = localCache.findOnLocalLodCacheRelation(term1, term2);
					if(lodCacheRelation == null) {
						lodCacheRelation = new LodCacheRelation(term1, term2);
						if(sparqlWalk.isRedirect(term1Resource, term2Resource)) {
							lodCacheRelation.setDirectLinks((int) totalDirect);
							lodCacheRelation.setIndirectLinks((int) totalIndirect);
						} else {
							lodCacheRelation.setDirectLinks(sparqlWalk.countDirectLinksBetween2Resources(term1Resource, term2Resource));
							lodCacheRelation.setIndirectLinks(sparqlWalk.countIndirectLinksBetween2Resources(term1Resource, term2Resource));
						}
						localCache.saveLodRelationCache(lodCacheRelation);
						if(LOGGER.isTraceEnabled()) {
							LOGGER.trace(lodCacheRelation);
						}
					}
					totalBetweenDirect = lodCacheRelation.getDirectLinks();
					totalBetweenIndirect = lodCacheRelation.getIndirectLinks();
				} else {
					throw new ResourceNotFoundException(String.format("%s and/or %s not found on dbpedia", term1, term2));
				}
			} else if(sparqlWalk.isResource(term1Resource) && sparqlWalk.isResource(term2Resource)) {
				if(sparqlWalk.isRedirect(term1Resource, term2Resource)) {
					return 1;
				} else {
					totalDirect = sparqlWalk.countDirectLinksFromResource(term1Resource) + sparqlWalk.countDirectLinksFromResource(term2Resource);
					totalIndirect = sparqlWalk.countIndirectLinksFromResource(term1Resource) + sparqlWalk.countIndirectLinksFromResource(term2Resource);
					totalBetweenDirect = sparqlWalk.countDirectLinksBetween2Resources(term1Resource, term2Resource);
					totalBetweenIndirect = sparqlWalk.countIndirectLinksBetween2Resources(term1Resource, term2Resource);
				}
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
			} else {
				return 0;
			}
		} else {
			throw new NullPointerException("terms1 and terms2 must not be null");
		}
	}

	private boolean isResource(String term)
	{
		if(localCache != null) {
			NotResource nr = new NotResource(term);
			boolean isNotResource = localCache.containsNotResource(nr);
			if(! localCache.containsLodCache(new LodCache(term)) && (isNotResource || ! sparqlWalk.isResource(Sparql.wrapStringAsResource(term)))) {
				if(! isNotResource) {
					localCache.saveNotResource(nr);
				}
				return false;
			}
			return true;
		}
		return sparqlWalk.isResource(Sparql.wrapStringAsResource(term));
	}
}
