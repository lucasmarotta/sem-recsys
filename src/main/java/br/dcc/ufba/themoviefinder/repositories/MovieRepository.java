package br.dcc.ufba.themoviefinder.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.dcc.ufba.themoviefinder.models.Movie;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long>
{
	Movie findById(Integer id);
	Movie findFirstByTitle(String title);
	
	@Query("SELECT m FROM Movie m WHERE m.title LIKE %:title%")
	Page<Movie> findByLikeTitle(@Param("title") String title, Pageable pageable);
	
	@Query("SELECT m FROM Movie m WHERE m.title LIKE %:title%")
	List<Movie> findAllByLikeTitle(@Param("title") String title);
	
	List<Movie> findByIdNotInOrderByTitleAsc(List<Integer> movieIds);
	List<Movie> findTop30ByOrderByIdAsc();
}
