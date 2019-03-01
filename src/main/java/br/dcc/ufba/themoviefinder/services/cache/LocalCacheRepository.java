package br.dcc.ufba.themoviefinder.services.cache;

import br.dcc.ufba.themoviefinder.entities.models.LodCache;
import br.dcc.ufba.themoviefinder.entities.models.LodCacheRelation;
import br.dcc.ufba.themoviefinder.entities.models.LodRelationId;

public interface LocalCacheRepository 
{
	public LodCache getLodCache(LodCache lodCache);
	public LodCacheRelation getLodCacheRelation(LodRelationId lodRelationId);
	public LodCache findLodCache(LodCache lodCache);
	public LodCacheRelation findLodCacheRelation(LodRelationId lodRelationId);
}
