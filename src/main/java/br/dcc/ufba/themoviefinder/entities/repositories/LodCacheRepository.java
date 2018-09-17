package br.dcc.ufba.themoviefinder.entities.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.dcc.ufba.themoviefinder.entities.models.LodCache;

@Repository
public interface LodCacheRepository extends JpaRepository<LodCache, String>
{
	LodCache findByResource(String resource);
	List<LodCache> findByResourceIn(List<String> resources);
}
