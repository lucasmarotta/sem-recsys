package br.dcc.ufba.themoviefinder.entities.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.dcc.ufba.themoviefinder.entities.models.LodRelationCounter;
import br.dcc.ufba.themoviefinder.entities.models.LodRelationId;

@Repository
public interface LodRelationCounterRepository extends JpaRepository<LodRelationCounter, LodRelationId>
{
	List<LodRelationCounter> findByIdIn(List<String> lodRelationIds);
}
