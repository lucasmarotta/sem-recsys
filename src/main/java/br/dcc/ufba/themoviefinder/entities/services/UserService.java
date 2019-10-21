package br.dcc.ufba.themoviefinder.entities.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dcc.ufba.themoviefinder.entities.models.User;
import br.dcc.ufba.themoviefinder.entities.repositories.UserRepository;

@Service
public class UserService 
{
	@Autowired
	private UserRepository userRepo;
	
	public User save(User user)
	{
		if(user != null) {
			return userRepo.save(user);
		}
		return user;
	}
	
	public void removeById(int id)
	{
		userRepo.deleteById((long) id);
	}
	
	public User findById(int id)
	{
		return userRepo.findById(id);
	}
	
	public User findByName(String name)
	{
		if(name != null) {
			return userRepo.findByName(name);
		}
		return null;
	}
	
	public User findByEmail(String email)
	{
		if(email != null) {
			return userRepo.findByEmail(email);
		}
		return null;
	}
	
	public List<User> getOnlineUsers()
	{
		return userRepo.findTop50ByOnline(true);
	}
	
	public List<User> getOfflineUsers()
	{
		return userRepo.findTop50ByOnline(false);
	}
	
	public List<User> getOfflineUsersToRecomendation() 
	{
		return userRepo.findTop50ToRecomendation(false);
	}
	
	public List<User> getOnlineUsersToRecomendation() 
	{
		return userRepo.findTop50ToRecomendation(true);
	}
	
	public List<User> findAll()
	{
		return userRepo.findAll();
	}
}
