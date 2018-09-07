package br.dcc.ufba.themoviefinder.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.dcc.ufba.themoviefinder.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
	User findById(Integer id);
	User findByName(String name);
	User findByEmail(String email);
}
