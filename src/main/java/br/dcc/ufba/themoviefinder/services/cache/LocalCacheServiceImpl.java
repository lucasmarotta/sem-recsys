package br.dcc.ufba.themoviefinder.services.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import br.dcc.ufba.themoviefinder.entities.models.LodCache;
import br.dcc.ufba.themoviefinder.entities.models.LodCacheRelation;
import br.dcc.ufba.themoviefinder.entities.models.LodRelationId;
import br.dcc.ufba.themoviefinder.entities.services.LodCacheService;
import br.dcc.ufba.themoviefinder.utils.TFIDFCalculator;

@Service
public class LocalCacheServiceImpl implements LocalCacheService
{
	@Autowired
	private LodCacheService lodCacheService;
	
	@Autowired
	private LocalCacheRepository localCacheRepo;
	
	@Autowired
	private CacheManager cacheManager;
	
	private static final Logger LOGGER = LogManager.getLogger(LocalCacheServiceImpl.class);
	
	@Override
	public LodCache findLodCache(String resource) 
	{
		return localCacheRepo.getLodCache(new LodCache(resource));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<LodCache> findAllLodCache() 
	{
		List<LodCache> lodCaches = new ArrayList<LodCache>();
		((Map) cacheManager.getCache("lodCache").getNativeCache()).forEach((key, value) -> {
			lodCaches.add((LodCache) value);
		});
		return lodCaches;
	}

	@Override
	public LodCache saveLodCache(LodCache lodCache) 
	{
		return lodCacheService.saveResource(lodCache);
	}

	@Override
	public LodCacheRelation findLodCacheRelation(String resource1, String resource2) 
	{
		return localCacheRepo.getLodCacheRelation(new LodRelationId(resource1, resource2));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<LodCacheRelation> findAllLodCacheRelation() 
	{
		List<LodCacheRelation> lodCacheRelations = new ArrayList<LodCacheRelation>();
		((Map) cacheManager.getCache("lodCacheRelation").getNativeCache()).forEach((key, value) -> {
			lodCacheRelations.add((LodCacheRelation) value);
		});
		return lodCacheRelations;
	}

	@Override
	public LodCacheRelation saveLodCacheRelation(LodCacheRelation lodCacheRelation) 
	{
		return lodCacheService.saveResourceRelation(lodCacheRelation);
	}

	@Override
	public void updateLocalCache(List<String> terms1, List<String> terms2)
	{
		List<String> allTerms = new ArrayList<String>(terms1);
		allTerms.addAll(terms2);
		
		allTerms.removeAll(findAllLodCache().stream().map(lodCache -> {
			return lodCache.getResource();
		}).collect(Collectors.toList()));
		
		List<LodCache> lodCaches = lodCacheService.getResourceList(allTerms);
		lodCaches.forEach(lodCache -> {
			cacheManager.getCache("lodCache").putIfAbsent(lodCache, lodCache);
		});
		List<LodRelationId> lodRelationIds = new ArrayList<LodRelationId>();
		for (String term1 : terms1) {
			for (String term2 : terms2) {
				if(! term1.equalsIgnoreCase(term2)) {
					lodRelationIds.add(new LodRelationId(term1, term2));
				}
			}
		}
		
		lodRelationIds.removeAll(findAllLodCacheRelation().stream().map(lodCacheRelation -> {
			return lodCacheRelation.getId();
		}).collect(Collectors.toList()));
		
		List<LodCacheRelation> lodCacheRelations = lodCacheService.getResourceRelationList(lodRelationIds);
		lodCacheRelations.forEach(lodCacheRelation -> {
			cacheManager.getCache("lodCacheRelation").putIfAbsent(lodCacheRelation.getId(), lodCacheRelation);
		});
		if(LOGGER.isTraceEnabled()) {
			LOGGER.trace(String.format("%d/%d found", lodRelationIds.size(), TFIDFCalculator.uniqueValues(allTerms).size()));
			LOGGER.trace(String.format("%d/%d found", lodCacheRelations.size(), lodRelationIds.size()));
		}
	}

	@Override
	public void clear()
	{
		if(LOGGER.isTraceEnabled()) {
			LOGGER.trace(String.format("Save %d lodCache", lodCacheService.saveAllResources(findAllLodCache()).size()));
			LOGGER.trace(String.format("Save %d lodCacheRelation", lodCacheService.saveAllResourceRelations(findAllLodCacheRelation())));
		} else {
			lodCacheService.saveAllResources(findAllLodCache());
			lodCacheService.saveAllResourceRelations(findAllLodCacheRelation());
		}
		for (String cacheName : cacheManager.getCacheNames()) {
			cacheManager.getCache(cacheName).clear();
		}
	}
}
