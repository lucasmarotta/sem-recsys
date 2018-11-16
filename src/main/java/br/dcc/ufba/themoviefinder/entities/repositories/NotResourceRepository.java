package br.dcc.ufba.themoviefinder.entities.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.dcc.ufba.themoviefinder.entities.models.NotResource;

@Repository
public interface NotResourceRepository extends JpaRepository<NotResource, String>
{
	List<NotResource> findByResourceIn(List<String> resources);
}
