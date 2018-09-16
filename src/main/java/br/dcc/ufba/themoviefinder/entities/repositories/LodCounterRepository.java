package br.dcc.ufba.themoviefinder.entities.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.dcc.ufba.themoviefinder.entities.models.LodCounter;

@Repository
public interface LodCounterRepository extends JpaRepository<LodCounter, String>
{
	LodCounter findByResource(String resource);
	List<LodCounter> findByResourceIn(List<String> resources);
}
