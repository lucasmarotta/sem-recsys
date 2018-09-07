package br.dcc.ufba.themoviefinder.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.dcc.ufba.themoviefinder.models.Idf;

public interface IdfRepository extends JpaRepository<Idf, Long>
{
	List<Idf> findAllByOrderByTermAsc();
	Idf findByTerm(String term);
	List<Idf> findByTermIn(String[] toCompareTerms);
}
