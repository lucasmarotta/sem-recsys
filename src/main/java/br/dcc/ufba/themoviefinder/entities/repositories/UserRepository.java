package br.dcc.ufba.themoviefinder.entities.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.dcc.ufba.themoviefinder.entities.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
	User findById(Integer id);
	User findByName(String name);
	User findByEmail(String email);
}
