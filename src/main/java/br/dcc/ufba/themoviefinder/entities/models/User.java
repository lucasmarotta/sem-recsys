package br.dcc.ufba.themoviefinder.entities.models;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@Entity
public class User 
{
	@Id @GeneratedValue
	private Integer id;
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private String email;
	
	@ManyToMany(
		fetch = FetchType.LAZY,
		cascade = {CascadeType.PERSIST, CascadeType.MERGE}
	)
	@JoinTable(
		name = "user_movie", 
		joinColumns = {@JoinColumn(name = "user_id")},
		inverseJoinColumns = {@JoinColumn(name = "movie_id")}
	)
	private Set<Movie> movies;
    
	@ManyToMany(
		fetch = FetchType.LAZY,
		cascade = {CascadeType.PERSIST, CascadeType.MERGE}
	)
	@JoinTable(
		name = "cs_recomendation", 
		joinColumns = {@JoinColumn(name = "user_id")},
		inverseJoinColumns = {@JoinColumn(name = "movie_id")}
	)
	private Set<Movie> recomendedMovies;
    
	@ManyToMany(
		fetch = FetchType.LAZY,
		cascade = {CascadeType.PERSIST, CascadeType.MERGE}
	)
    @JoinTable(
		name = "extended_cs_recomendation", 
		joinColumns = {@JoinColumn(name = "user_id")},
		inverseJoinColumns = {@JoinColumn(name = "movie_id")}
    )
	private Set<Movie> extendedRecomendedMovies;
	
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
	
	public User() 
	{
		movies = new HashSet<Movie>();
		recomendedMovies = new HashSet<Movie>();
		extendedRecomendedMovies = new HashSet<Movie>();
	}
	
	public User(int id)
	{
		movies = new HashSet<Movie>();
		recomendedMovies = new HashSet<Movie>();
		extendedRecomendedMovies = new HashSet<Movie>();
		this.id = id;
	}

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

	public Set<Movie> getMovies() 
	{
		return movies;
	}
	
	public Set<Movie> getRecomendedMovies() 
	{
		return recomendedMovies;
	}

	public Set<Movie> getExtendedRecomendedMovies() 
	{
		return extendedRecomendedMovies;
	}
	
    @Override
    public boolean equals(Object o) 
    {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User m = (User) o;
        if(id == m.getId() && email == m.getEmail()) {
        	return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() 
    {
        int tmp = 0;
        if(id != null) {
            tmp = (id.intValue() + email).hashCode();	
        } else if(email != null) {
        	tmp = (email).hashCode();
        }
        return tmp;
    }

	@Override
	public String toString() 
	{
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", movies=" + movies + ", pass=" + pass
				+ ", salt=" + salt + ", profilePicture=" + profilePicture + ", facebookId=" + facebookId + ", active="
				+ active + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}
}
