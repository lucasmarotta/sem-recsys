package br.dcc.ufba.themoviefinder.entities.services;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dcc.ufba.themoviefinder.entities.models.LodCache;
import br.dcc.ufba.themoviefinder.entities.models.LodCacheRelation;
import br.dcc.ufba.themoviefinder.entities.models.LodRelationId;
import br.dcc.ufba.themoviefinder.entities.repositories.LodCacheRepository;
import br.dcc.ufba.themoviefinder.entities.repositories.LodRelationRepository;

@Service
public class LodCacheService 
{
	@Autowired
	private LodCacheRepository lodCacheRepo;
	
	@Autowired
	private LodRelationRepository lodRelationRepo;
	
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
		if(! resources.isEmpty()) {
			return lodCacheRepo.findByResourceIn(resources);	
		}
		return new ArrayList<LodCache>();
	}
	
	public LodCache saveResource(LodCache resource)
	{
		return lodCacheRepo.save(resource);
	}
	
	public List<LodCache> saveAllResources(List<LodCache> lodCaches)
	{
		if(! lodCaches.isEmpty()) {
			return lodCacheRepo.saveAll(lodCaches);	
		}
		return lodCaches;
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
		if(! ids.isEmpty()) {
			List<LodRelationId> expandedIds = new ArrayList<LodRelationId>();
			ids.forEach(lodRelationId -> {
				expandedIds.add(lodRelationId);
				expandedIds.add(new LodRelationId(lodRelationId.getResource2(), lodRelationId.getResource1()));
			});
			return lodRelationRepo.findByIdResource1InAndIdResource2In(expandedIds.stream().map(lodId -> {
				return lodId.getResource1();
			}).distinct().collect(Collectors.toList()), expandedIds.stream().map(lodId -> {
				return lodId.getResource2();
			}).distinct().collect(Collectors.toList()));
		}
		return new ArrayList<LodCacheRelation>();
	}
	
	public List<LodCacheRelation> saveAllResourceRelations(List<LodCacheRelation> lodCacheRelations)
	{
		if(! lodCacheRelations.isEmpty()) {
			return lodRelationRepo.saveAll(lodCacheRelations); 
		}
		return lodCacheRelations;
	}
	
	public LodCacheRelation saveResourceRelation(LodCacheRelation lodRelation) 
	{
		return lodRelationRepo.save(lodRelation);
	}
	
	public LodCacheRelation saveResourceRelation(String resource1, String resource2) 
	{
		if(ObjectUtils.allNotNull(resource1, resource2)) {
			return lodRelationRepo.save(new LodCacheRelation(resource1, resource2));
		}
		return null;
	}
}
