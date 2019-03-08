package br.dcc.ufba.themoviefinder.entities.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import br.dcc.ufba.themoviefinder.utils.ItemValue;
import br.dcc.ufba.themoviefinder.utils.TFIDFCalculator;

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
	private List<Movie> movies;
    
	@ManyToMany(
		fetch = FetchType.LAZY,
		cascade = {CascadeType.PERSIST, CascadeType.MERGE}
	)
	@JoinTable(
		name = "user_recomendation", 
		joinColumns = {@JoinColumn(name = "user_id")},
		inverseJoinColumns = {@JoinColumn(name = "movie_id")}
	)
	private List<Movie> recomendedMovies;
	
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
		movies = new ArrayList<Movie>();
		recomendedMovies = new ArrayList<Movie>();
	}
	
	public User(int id)
	{
		this();
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

	public List<Movie> getMovies() 
	{
		return movies;
	}
	
	public List<Movie> getRecomendedMovies() 
	{
		return recomendedMovies;
	}
	
	public List<String> getUserBestTerms()
	{
		return getUserBestTerms(-1);
	}
	
	public List<String> getUserBestTerms(int qtTerms)
	{
		if(! movies.isEmpty()) {
			List<List<String>> listOfDocs = new ArrayList<List<String>>();
			List<String> uniqueValues = new ArrayList<String>();
			
			for(Movie movie : movies) {
				listOfDocs.add(movie.getTokensList());
				uniqueValues.addAll(movie.getTokensList());
			}
			
			if(qtTerms == -1) {
				qtTerms = listOfDocs.stream().mapToInt(doc -> doc.size()).max().getAsInt();
			}
			uniqueValues = TFIDFCalculator.uniqueValues(uniqueValues);
			
			List<ItemValue<String>> termValueList = new ArrayList<ItemValue<String>>();
			for(String term : uniqueValues) {
				for (Movie movie : movies) {
					termValueList.add(new ItemValue<String>(term, TFIDFCalculator.tfIdf(movie.getTokensList(), listOfDocs, term)));
				}
			}
			uniqueValues.clear();
			
			termValueList.sort((ItemValue<String> a, ItemValue<String> b) -> a.compareTo(b));
			List<ItemValue<String>> bestTerms = new ArrayList<ItemValue<String>>();
			for (ItemValue<String> termValue : termValueList) {
				if(bestTerms.size() < qtTerms) {
					if(! bestTerms.stream().anyMatch(value -> bestTerms.stream().anyMatch(term -> value.item.equals(termValue.item)))) {
						bestTerms.add(termValue);	
					}
				} else {
					break;
				}
			}
			return bestTerms.stream().map(term -> term.item).collect(Collectors.toList());	
		} 
		return new ArrayList<String>();
	}
	
	public List<String> getUserMovieTokens()
	{
		List<String> userTokens = new ArrayList<String>();
		for(Movie userMovie : movies) {
			userTokens.addAll(userMovie.getTokensList());
		}
		return userTokens;
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
