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
	List<User> findTop30ByOnline(boolean online);
	
	@Query(value="SELECT u.*\r\n" + 
			"FROM User u \r\n" + 
			"WHERE u.online = :online AND NOT EXISTS\r\n" + 
			"(\r\n" + 
			"	SELECT r.*\r\n" + 
			"    FROM Recommendation r\r\n" + 
			"    WHERE similarity = :similarity AND r.user_id = u.id\r\n" + 
			")\r\n" + 
			"LIMIT 30", nativeQuery=true)
	List<User> findTop30ToRecommendation(@Param("online") boolean online, @Param("similarity") String similarity);
}
