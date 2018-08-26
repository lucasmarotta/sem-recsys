package br.dcc.ufba.semrecsys.models;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

@Entity
public class User 
{
	@Id @GeneratedValue
	private Integer id;
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private String email;
	
    @OneToMany(cascade=CascadeType.ALL)  
    @JoinTable(name="user_movie",  
              joinColumns={@JoinColumn(name="user_id", 
               referencedColumnName="id")},  
              inverseJoinColumns={@JoinColumn(name="movie_id", 
                referencedColumnName="id")}) 
	private List<Movie> movies;
	
	private String pass;
	
	@Column(nullable = false)
	private String salt;
	
	private String profilePicture;
	
	private String facebookId;
	
	@Column(nullable = false)
	private Boolean active;

	@Column(nullable = false)
	private LocalDateTime createdAt;
	
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	public Integer getId() 
	{
		return id;
	}

	public void setId(Integer id) 
	{
		this.id = id;
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}

	public String getEmail() 
	{
		return email;
	}

	public void setEmail(String email) 
	{
		this.email = email;
	}

	public String getPass() 
	{
		return pass;
	}

	public void setPass(String pass) 
	{
		this.pass = pass;
	}

	public String getSalt() 
	{
		return salt;
	}

	public void setSalt(String salt) 
	{
		this.salt = salt;
	}

	public String getProfilePicture() 
	{
		return profilePicture;
	}

	public void setProfilePicture(String profilePicture) 
	{
		this.profilePicture = profilePicture;
	}

	public String getFacebookId() 
	{
		return facebookId;
	}

	public void setFacebookId(String facebookId) 
	{
		this.facebookId = facebookId;
	}

	public Boolean getActive() 
	{
		return active;
	}

	public void setActive(Boolean active) 
	{
		this.active = active;
	}

	public LocalDateTime getCreatedAt() 
	{
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() 
	{
		return updatedAt;
	}

	public List<Movie> getMovies() 
	{
		return movies;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", movies=" + movies + ", pass=" + pass
				+ ", salt=" + salt + ", profilePicture=" + profilePicture + ", facebookId=" + facebookId + ", active="
				+ active + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}
}
