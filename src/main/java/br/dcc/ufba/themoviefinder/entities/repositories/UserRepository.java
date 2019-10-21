package br.dcc.ufba.themoviefinder.entities.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.dcc.ufba.themoviefinder.entities.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
	User findById(Integer id);
	User findByName(String name);
	User findByEmail(String email);
	List<User> findTop50ByOnline(boolean online);
	
	@Query(value="SELECT u.*\r\n" + 
			"FROM User u \r\n" + 
			"WHERE u.online = :online AND NOT EXISTS\r\n" + 
			"(\r\n" + 
			"	SELECT r.*\r\n" + 
			"    FROM Recomendation r\r\n" + 
			"    WHERE r.user_id = u.id\r\n" + 
			")\r\n" + 
			"LIMIT 50", nativeQuery=true)
	List<User> findTop50ToRecomendation(@Param("online") boolean online);
}
