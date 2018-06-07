package br.dcc.ufba.semrecsys.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.dcc.ufba.semrecsys.models.Movie;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long>
{
	Movie findById(Integer id);
	Movie findFirstByTitle(String title);
	List<Movie> findTop30ByOrderByIdAsc();
}
