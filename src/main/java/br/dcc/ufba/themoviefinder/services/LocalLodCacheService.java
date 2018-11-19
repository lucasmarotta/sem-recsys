package br.dcc.ufba.themoviefinder.services;

import java.util.List;

import br.dcc.ufba.themoviefinder.entities.models.LodCache;
import br.dcc.ufba.themoviefinder.entities.models.LodCacheRelation;
import br.dcc.ufba.themoviefinder.entities.models.NotResource;

public interface LocalLodCacheService 
{
	public void updateNotResourceCache();
	public void updateLocalLodCache(List<String> terms1, List<String> terms2);
	public void saveLodCache(LodCache lodCache);
	public void saveLodRelationCache(LodCacheRelation lodCacheRelation);
	public void saveNotResource(NotResource nr);
	public void clear();
	public LodCache findOnLocalLodCache(String resource);
	public LodCacheRelation findOnLocalLodCacheRelation(String resource1, String resource2);
	public boolean containsLodCache(LodCache lodCache);
	public boolean containsNotResource(NotResource nr);
}
