package br.dcc.ufba.themoviefinder.services.cache;

import java.util.List;

import br.dcc.ufba.themoviefinder.entities.models.LodCache;
import br.dcc.ufba.themoviefinder.entities.models.LodCacheRelation;

public interface LocalCacheService 
{
	public LodCache findLodCache(String resource);
	public List<LodCache> findAllLodCache();
	public LodCache saveLodCache(LodCache lodCache);
	public LodCacheRelation findLodCacheRelation(String resource1, String resource2);
	public List<LodCacheRelation> findAllLodCacheRelation();
	public LodCacheRelation saveLodCacheRelation(LodCacheRelation lodCacheRelation);
	public void updateLocalCache(List<String> terms1, List<String> terms2);
	public void clear();
}
