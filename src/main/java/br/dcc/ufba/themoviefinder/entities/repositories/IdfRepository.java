package br.dcc.ufba.themoviefinder.entities.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.dcc.ufba.themoviefinder.entities.models.Idf;

public interface IdfRepository extends JpaRepository<Idf, String>
{
	List<Idf> findAllByOrderByTermAsc();
	Idf findByTerm(String term);
	List<Idf> findByTermIn(String[] toCompareTerms);
}
