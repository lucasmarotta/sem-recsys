package br.dcc.ufba.themoviefinder.services.cache;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import br.dcc.ufba.themoviefinder.entities.models.LodCache;
import br.dcc.ufba.themoviefinder.entities.models.LodCacheRelation;
import br.dcc.ufba.themoviefinder.entities.models.LodRelationId;
import br.dcc.ufba.themoviefinder.entities.services.LodCacheService;
import br.dcc.ufba.themoviefinder.lodweb.Sparql;
import br.dcc.ufba.themoviefinder.lodweb.SparqlWalk;

@Component
public class LocalCacheRepositoryImpl implements LocalCacheRepository
{
	@Autowired
	private SparqlWalk sparqlWalk;
	
	@Autowired
	private LodCacheService lodCacheService;
	
	private static final Logger LOGGER = LogManager.getLogger(LocalCacheRepositoryImpl.class);
	
	@Override
	@Cacheable("lodCache")
	public LodCache getAndSaveLodCache(LodCache lodCache) 
	{
		lodCache.setDirectLinks(sparqlWalk.countDirectLinksFromResource(Sparql.wrapStringAsResource(lodCache.getResource())));
		if(lodCache.getDirectLinks() == 0) {
			lodCache.setIndirectLinks(0);
		} else {
			lodCache.setIndirectLinks(sparqlWalk.countIndirectLinksFromResource(Sparql.wrapStringAsResource(lodCache.getResource())));	
		}
		if(LOGGER.isTraceEnabled()) {
			LOGGER.trace("Save to Cache: " + lodCache);
		}
		return lodCacheService.saveResource(lodCache);
	}

	@Override
	@Cacheable("lodCacheRelation")
	public LodCacheRelation getAndSaveLodCacheRelation(LodRelationId lodRelationId) 
	{
		String resource1 = Sparql.wrapStringAsResource(lodRelationId.getResource1());
		String resource2 = Sparql.wrapStringAsResource(lodRelationId.getResource2());
		LodCacheRelation lodCacheRelation = new LodCacheRelation(lodRelationId);
		if(sparqlWalk.isRedirect(resource1, resource2)) {
			lodCacheRelation.setDirectLinks(1);
			lodCacheRelation.setIndirectLinks(1);
		} else {
			lodCacheRelation.setDirectLinks(sparqlWalk.countDirectLinksBetween2Resources(resource1, resource2));
			lodCacheRelation.setIndirectLinks(sparqlWalk.countIndirectLinksBetween2Resources(resource1, resource2));	
		}
		if(LOGGER.isTraceEnabled()) {
			LOGGER.trace("Save to Cache: " + lodCacheRelation);
		}
		return lodCacheService.saveResourceRelation(lodCacheRelation);
	}
}
