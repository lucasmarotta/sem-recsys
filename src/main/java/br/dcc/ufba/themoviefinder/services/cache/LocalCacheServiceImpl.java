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
import br.dcc.ufba.themoviefinder.utils.BatchWorkLoad;
import br.dcc.ufba.themoviefinder.utils.TFIDFCalculator;

@Service
public class LocalCacheServiceImpl implements LocalCacheService
{
	
	public int batchSize = 5;
	
	@Autowired
	private LodCacheService lodCacheService;
	
	@Autowired
	private LocalCacheRepository localCacheRepo;
	
	@Autowired
	private CacheManager cacheManager;
	
	private static final Logger LOGGER = LogManager.getLogger(LocalCacheServiceImpl.class);
	
	@Override
	public void setBatchSize(int batchSize) 
	{
		this.batchSize = batchSize;
	}

	@Override
	public int getBatchSize() 
	{
		return batchSize;
	}

	@Override
	public LodCache findLodCache(String resource) 
	{
		return localCacheRepo.getAndSaveLodCache(new LodCache(resource));
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
		return localCacheRepo.getAndSaveLodCacheRelation(new LodRelationId(resource1, resource2));
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
	public void updateLocalCache(List<String> terms1, List<String> terms2) throws Exception
	{
		List<String> allTerms = new ArrayList<String>(terms1);
		allTerms.addAll(terms2);
		allTerms = TFIDFCalculator.uniqueValues(allTerms);

		/**
		 * Find all lodCaches on DB and then get those that were not found submit to localCacheRepo
		 */
		List<LodCache> lodCaches = lodCacheService.getResourceList(allTerms);
		lodCaches.forEach(lodCache -> {
			cacheManager.getCache("lodCache").put(lodCache, lodCache);
		});
		List<LodCache> lodCacheNotFound = allTerms.stream().map(term -> {
			return new LodCache(term);
		}).filter(lodCache -> ! lodCaches.contains(lodCache)).collect(Collectors.toList());
		(new BatchWorkLoad<LodCache>(batchSize, lodCacheNotFound)).run(lodCache -> {
			localCacheRepo.getAndSaveLodCache(lodCache);
			return null;
		});

		/**
		 * Find all lodCacheRelations on DB and then get those that were not found submit to localCacheRepo
		 */
		List<LodRelationId> lodRelationIds = new ArrayList<LodRelationId>();
		for (String term1 : terms1) {
			for (String term2 : terms2) {
				if(! term1.equalsIgnoreCase(term2)) {
					lodRelationIds.add(new LodRelationId(term1, term2));
				}
			}
		}
		List<LodCacheRelation> lodCacheRelations = lodCacheService.getResourceRelationList(lodRelationIds);
		lodCacheRelations.forEach(lodCacheRelation -> {
			cacheManager.getCache("lodCacheRelation").put(lodCacheRelation.getId(), lodCacheRelation);
		});
		List<LodRelationId> lodRelationIdNotFound = lodRelationIds.stream().filter(
				lodId -> ! lodCacheRelations.contains(new LodCacheRelation(lodId))
		).collect(Collectors.toList());		
		(new BatchWorkLoad<LodRelationId>(batchSize, lodRelationIdNotFound)).run(lodId -> {
			localCacheRepo.getAndSaveLodCacheRelation(lodId);
			return null;
		});
		
		if(LOGGER.isTraceEnabled()) {
			LOGGER.trace(String.format("%d/%d found", lodRelationIds.size(), TFIDFCalculator.uniqueValues(allTerms).size()));
			LOGGER.trace(String.format("%d/%d found", lodCacheRelations.size(), lodRelationIds.size()));
		}
	}

	@Override
	public void clear()
	{
		for (String cacheName : cacheManager.getCacheNames()) {
			cacheManager.getCache(cacheName).clear();
		}
	}
}
