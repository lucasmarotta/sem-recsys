package br.dcc.ufba.themoviefinder.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import br.dcc.ufba.themoviefinder.entities.models.LodCache;
import br.dcc.ufba.themoviefinder.entities.models.LodCacheRelation;
import br.dcc.ufba.themoviefinder.entities.models.LodRelationId;
import br.dcc.ufba.themoviefinder.entities.models.NotResource;
import br.dcc.ufba.themoviefinder.entities.services.LodCacheService;
import br.dcc.ufba.themoviefinder.utils.TFIDFCalculator;

@Service
@Scope("prototype")
public class LocalLodCacheServiceImpl implements LocalLodCacheService
{
	@Autowired
	private LodCacheService lodCacheService;
	private Set<LodCache> localLodCache;
	private Set<LodCacheRelation> localLodCacheRelation;
	private static final Logger LOGGER = LogManager.getLogger(LocalLodCacheServiceImpl.class);
	
	public LocalLodCacheServiceImpl()
	{
		localLodCache = new HashSet<LodCache>();
		localLodCacheRelation = new HashSet<LodCacheRelation>();
	}
	
	@Override
	public void clear()
	{
		localLodCache.clear();
		localLodCacheRelation.clear();
		lodCacheService.getNotResourceCache().clear();
	}
	
	@Override
	public void updateNotResourceCache()
	{
		lodCacheService.updateNotResourceCache();
	}

	@Override
	public void updateLocalLodCache(List<String> terms1, List<String> terms2)
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
	
	@Override
	public void saveLodCache(LodCache lodCache)
	{
		lodCacheService.saveResource(lodCache);
		localLodCache.add(lodCache);		
	}
	
	@Override
	public void saveLodRelationCache(LodCacheRelation lodCacheRelation)
	{
		lodCacheService.saveResourceRelation(lodCacheRelation);
		localLodCacheRelation.add(lodCacheRelation);
	}
	
	@Override
	public void saveNotResource(NotResource nr)
	{
		lodCacheService.saveNotResource(nr);
		lodCacheService.getNotResourceCache().add(nr);
	}
	
	@Override
	public LodCache findOnLocalLodCache(String resource)
	{
		LodCache toFind = new LodCache(resource);
		for (LodCache lodCache : localLodCache) {
			if(toFind.equals(lodCache)) {
				return lodCache;
			}
		}
		return null;
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
		return null;
	}
	
	@Override
	public boolean containsLodCache(LodCache lodCache)
	{
		return localLodCache.contains(lodCache);
	}
	
	@Override
	public boolean containsNotResource(NotResource nr)
	{
		return lodCacheService.getNotResourceCache().contains(nr);
	}
}
