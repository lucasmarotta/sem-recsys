package br.dcc.ufba.themoviefinder.services.cache;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import br.dcc.ufba.themoviefinder.entities.models.LodCache;
import br.dcc.ufba.themoviefinder.entities.models.LodCacheRelation;
import br.dcc.ufba.themoviefinder.entities.services.LodCacheService;
import br.dcc.ufba.themoviefinder.lodweb.SparqlWalk;

@Service
@Scope("prototype")
public class LocalLodCacheServiceImpl //implements LocalLodCacheService
{
	@Autowired
	private LodCacheService lodCacheService;
	
	@Autowired
	private SparqlWalk sparqlWalk;
	
	private Set<LodCache> localLodCache;
	private Set<LodCacheRelation> localLodCacheRelation;
	private static final Logger LOGGER = LogManager.getLogger(LocalLodCacheServiceImpl.class);
	
	public LocalLodCacheServiceImpl()
	{
		localLodCache = new HashSet<LodCache>();
		localLodCacheRelation = new HashSet<LodCacheRelation>();
	}
	
	/*
	@Override
	public LodCache findOnLocalLodCache(String resource)
	{
		LodCache toFind = new LodCache(resource);
		for (LodCache lodCache : localLodCache) {
			if(toFind.equals(lodCache)) {
				return lodCache;
			}
		}
		toFind.setDirectLinks(sparqlWalk.countDirectLinksFromResource(Sparql.wrapStringAsResource(resource)));
		if(toFind.getDirectLinks() == 0) {
			toFind.setIndirectLinks(0);
		} else {
			toFind.setIndirectLinks(sparqlWalk.countIndirectLinksFromResource(Sparql.wrapStringAsResource(resource)));	
		}
		return saveLodCache(toFind);
	}
	
	@Override
	public LodCache saveLodCache(LodCache lodCache)
	{
		lodCacheService.saveResource(lodCache);
		localLodCache.add(lodCache);
		return lodCache;
	}
	
	@Override
	public LodCacheRelation findOnLocalLodCacheRelation(String resource1, String resource2)
	{
		LodCacheRelation toFind = new LodCacheRelation(resource1, resource2);
		for (LodCacheRelation lodCacheRelation : localLodCacheRelation) {
			if(toFind.equals(lodCacheRelation)) {
				return lodCacheRelation;
			}
		}
		resource1 = Sparql.wrapStringAsResource(resource1);
		resource2 = Sparql.wrapStringAsResource(resource2);
		if(sparqlWalk.isRedirect(resource1, resource2)) {
			toFind.setDirectLinks(1);
			toFind.setIndirectLinks(1);
		} else {
			toFind.setDirectLinks(sparqlWalk.countDirectLinksBetween2Resources(resource1, resource2));
			toFind.setIndirectLinks(sparqlWalk.countIndirectLinksBetween2Resources(resource1, resource2));	
		}
		return saveLodRelationCache(toFind);
	}
	
	@Override
	public LodCacheRelation saveLodRelationCache(LodCacheRelation lodCacheRelation)
	{
		lodCacheService.saveResourceRelation(lodCacheRelation);
		localLodCacheRelation.add(lodCacheRelation);
		return lodCacheRelation;
	}
	
	@Override
	public void updateLocalResourceCache(List<String> terms1, List<String> terms2)
	{
		List<String> allTerms = new ArrayList<String>(terms1);
		allTerms.addAll(terms2);
		localLodCache = new HashSet<LodCache>(lodCacheService.getResourceList(allTerms));
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
	
	@Override
	public void clear()
	{
		localLodCache.clear();
		localLodCacheRelation.clear();
	}
	*/
}
