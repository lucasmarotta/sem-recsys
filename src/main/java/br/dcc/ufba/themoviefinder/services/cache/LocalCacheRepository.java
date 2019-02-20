package br.dcc.ufba.themoviefinder.services.cache;

import br.dcc.ufba.themoviefinder.entities.models.LodCache;
import br.dcc.ufba.themoviefinder.entities.models.LodCacheRelation;
import br.dcc.ufba.themoviefinder.entities.models.LodRelationId;

public interface LocalCacheRepository 
{
	public LodCache getAndSaveLodCache(LodCache lodCache);
	public LodCacheRelation getAndSaveLodCacheRelation(LodRelationId lodRelationId);
}
