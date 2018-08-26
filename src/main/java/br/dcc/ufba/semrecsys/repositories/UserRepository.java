package br.dcc.ufba.semrecsys.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.dcc.ufba.semrecsys.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
	User findByName(String name);
	User findByEmail(String email);
}
