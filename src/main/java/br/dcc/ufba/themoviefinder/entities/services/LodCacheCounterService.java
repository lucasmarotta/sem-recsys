package br.dcc.ufba.themoviefinder.entities.services;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dcc.ufba.themoviefinder.entities.models.LodCounter;
import br.dcc.ufba.themoviefinder.entities.models.LodRelationCounter;
import br.dcc.ufba.themoviefinder.entities.models.LodRelationId;
import br.dcc.ufba.themoviefinder.entities.repositories.LodCounterRepository;
import br.dcc.ufba.themoviefinder.entities.repositories.LodRelationCounterRepository;

@Service
public class LodCacheCounterService 
{
	@Autowired
	private LodCounterRepository lodCounterRepo;
	
	@Autowired
	private LodRelationCounterRepository lodRelationCounterRepo;
	
	public LodCounter getResourceCache(String resource)
	{
		LodCounter lc = lodCounterRepo.findByResource(resource);
		if(lc != null) {
			return lc;
		}
		return null;
	}
	
	public void saveResourceToCache(LodCounter resource)
	{
		lodCounterRepo.save(resource);
	}
	
	public LodRelationCounter getResourceRelationCache(String resource1, String resource2)
	{
		try {
			return lodRelationCounterRepo.findById(new LodRelationId(resource1, resource2)).get();	
		} catch(NoSuchElementException e) {
			return null;
		}
	}

	public void saveResourceRelationToCache(LodRelationCounter lodRelation) 
	{
		lodRelationCounterRepo.save(lodRelation);
	}
}
