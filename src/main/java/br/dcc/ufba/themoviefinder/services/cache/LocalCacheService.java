package br.dcc.ufba.themoviefinder.services.cache;

import java.util.List;

import br.dcc.ufba.themoviefinder.entities.models.LodCache;
import br.dcc.ufba.themoviefinder.entities.models.LodCacheRelation;
import br.dcc.ufba.themoviefinder.entities.models.LodRelationId;

public interface LocalCacheService 
{
	public LodCache findLodCache(String resource);
	public List<LodCache> findAllLodCache();
	public LodCacheRelation findLodCacheRelation(String resource1, String resource2);
	public List<LodCacheRelation> findAllLodCacheRelation();
	public void updateLocalCache(List<String> terms1, List<String> terms2) throws Exception;
	public boolean isLodCacheCached(LodCache lodCache);
	public boolean isLodCacheRelationCached(LodRelationId lodRelationId);
	public void clear();
}
