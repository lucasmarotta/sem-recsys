package br.dcc.ufba.themoviefinder.entities.services;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dcc.ufba.themoviefinder.entities.models.LodCache;
import br.dcc.ufba.themoviefinder.entities.models.LodCacheRelation;
import br.dcc.ufba.themoviefinder.entities.models.LodRelationId;
import br.dcc.ufba.themoviefinder.entities.models.NotResource;
import br.dcc.ufba.themoviefinder.entities.repositories.LodCacheRepository;
import br.dcc.ufba.themoviefinder.entities.repositories.LodRelationRepository;
import br.dcc.ufba.themoviefinder.entities.repositories.NotResourceRepository;

@Service
public class LodCacheService 
{
	@Autowired
	private LodCacheRepository lodCacheRepo;
	
	@Autowired
	private LodRelationRepository lodRelationRepo;
	
	@Autowired
	private NotResourceRepository notResourceRepo;
	
	public LodCache getResource(String resource)
	{
		LodCache lc = lodCacheRepo.findByResource(resource);
		if(lc != null) {
			return lc;
		}
		return null;
	}
	
	public List<LodCache> getResourceList(List<String> resources)
	{
		return lodCacheRepo.findByResourceIn(resources);
	}
	
	public List<NotResource> getAllNotResource()
	{
		return notResourceRepo.findAll();
	}
	
	public boolean isNotResource(LodCache lodCache)
	{
		return notResourceRepo.findById(lodCache.getResource()) != null;
	}
	
	public boolean isNotResource(String resource)
	{
		return notResourceRepo.findById(resource) != null;
	}
	
	public LodCache saveResource(LodCache resource)
	{
		return lodCacheRepo.save(resource);
	}
	
	public NotResource saveNotResource(NotResource notResource)
	{
		return notResourceRepo.save(notResource);
	}
	
	public LodCacheRelation getResourceRelation(String resource1, String resource2)
	{
		LodCacheRelation lr = null;
		try {
			lr = lodRelationRepo.findById(new LodRelationId(resource1, resource2)).get();
		} catch(NoSuchElementException e) {
			lr = null;
		}
		
		if(lr == null) {
			try {
				lr = lodRelationRepo.findById(new LodRelationId(resource2, resource1)).get();
			} catch(NoSuchElementException e) {
				lr = null;
			}
		}
		return lr;
	}
	
	public LodCacheRelation getResourceRelation(LodRelationId id)
	{
		LodCacheRelation lr = null;
		try {
			lr = lodRelationRepo.findById(id).get();
		} catch(NoSuchElementException e) {
			lr = null;
		}
		
		if(lr == null) {
			try {
				lr = lodRelationRepo.findById(new LodRelationId(id.getResource2(), id.getResource1())).get();
			} catch(NoSuchElementException e) {
				lr = null;
			}
		}
		return lr;
	}

	public List<LodCacheRelation> getResourceRelationList(List<LodRelationId> ids)
	{
		List<LodRelationId> expandedIds = new ArrayList<LodRelationId>();
		for (LodRelationId lodRelationId : ids) {
			expandedIds.add(lodRelationId);
			expandedIds.add(new LodRelationId(lodRelationId.getResource2(), lodRelationId.getResource1()));
		}
		return lodRelationRepo.findByIdIn(expandedIds);
	}
	
	public void saveResourceRelation(LodCacheRelation lodRelation) 
	{
		lodRelationRepo.save(lodRelation);
	}
	
	public LodCacheRelation saveResourceRelation(String resource1, String resource2) 
	{
		if(ObjectUtils.allNotNull(resource1, resource2)) {
			return lodRelationRepo.save(new LodCacheRelation(resource1, resource2));
		}
		return null;
	}
}
