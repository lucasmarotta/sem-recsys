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
	public boolean isLodCacheCached(LodCache lodCache) 
	{
		if(lodCache != null) {
			return localCacheRepo.findLodCache(lodCache) != null;
		}
		return false;
	}

	@Override
	public boolean isLodCacheRelationCached(LodRelationId lodRelationId) 
	{
		if(lodRelationId != null) {
			return localCacheRepo.findLodCacheRelation(lodRelationId) != null;
		}
		return false;
	}

	@Override
	public void updateLocalCache(List<String> terms1, List<String> terms2)
	{
		List<String> allTerms = new ArrayList<String>(terms1);
		allTerms.addAll(terms2);
		allTerms = TFIDFCalculator.uniqueValues(allTerms).stream().filter(term -> ! isLodCacheCached(new LodCache(term))).collect(Collectors.toList());
		
		/**
		 * Find what is on DB and put on cache
		 */
		List<LodCache> lodCaches = lodCacheService.getResourceList(allTerms);
		lodCaches.forEach(lodCache -> {
			cacheManager.getCache("lodCache").putIfAbsent(lodCache, lodCache);	
		});
				
		/**
		 * Build all possible relation ids
		 */
		List<LodRelationId> lodRelationIds = new ArrayList<LodRelationId>();
		for (String term1 : terms1) {
			for (String term2 : terms2) {
				if(! term1.equalsIgnoreCase(term2)) {
					LodRelationId lodId = new LodRelationId(term1, term2);
					if(! isLodCacheRelationCached(lodId)) {
						lodRelationIds.add(lodId);	
					}
				}
			}
		}
		
		/**
		 * Find what is on DB and put on cache
		 */
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
		List<LodCache> lodCaches = findAllLodCacheSaveLater();
		List<LodCacheRelation> lodCacheRelations = findAllLodCacheRelationSaveLater();
		
		if(LOGGER.isDebugEnabled() && ! lodCaches.isEmpty() && ! lodCacheRelations.isEmpty()) {
			LOGGER.debug("Save later rotine");
			LOGGER.debug(lodCaches);
			LOGGER.debug(lodCacheRelations);
		}
		
		lodCacheService.saveAllResources(lodCaches);	
		lodCacheService.saveAllResourceRelations(lodCacheRelations);
		for (String cacheName : cacheManager.getCacheNames()) {
			cacheManager.getCache(cacheName).clear();
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<LodCache> findAllLodCacheSaveLater() 
	{
		List<LodCache> lodCaches = new ArrayList<LodCache>();
		((Map) cacheManager.getCache("lodCacheSaveLater").getNativeCache()).forEach((key, value) -> {
			lodCaches.add((LodCache) value);
		});
		return lodCaches;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<LodCacheRelation> findAllLodCacheRelationSaveLater() 
	{
		List<LodCacheRelation> lodCacheRelations = new ArrayList<LodCacheRelation>();
		((Map) cacheManager.getCache("lodCacheRelationSaveLater").getNativeCache()).forEach((key, value) -> {
			lodCacheRelations.add((LodCacheRelation) value);
		});
		return lodCacheRelations;
	}
	
}
