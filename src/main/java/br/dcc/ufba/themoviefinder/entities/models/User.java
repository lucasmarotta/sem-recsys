package br.dcc.ufba.themoviefinder.entities.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import br.dcc.ufba.themoviefinder.services.RecommendationModel;
import br.dcc.ufba.themoviefinder.utils.ItemValue;
import br.dcc.ufba.themoviefinder.utils.TFIDFCalculator;

@Entity
public class User 
{
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private String email;
	
	@OneToMany(
		mappedBy = "user",
		fetch = FetchType.LAZY,
		cascade = {CascadeType.PERSIST, CascadeType.MERGE}
	)
	private List<Rating> ratings;
    
	@OneToMany(
		mappedBy = "user", 
		fetch = FetchType.LAZY, 
		cascade = {CascadeType.PERSIST, CascadeType.MERGE}
	)
	private List<Recomendation> recomendations;
	
	private String password;
	
	private String profilePicture;
	
	private String facebookId;
	
	@Column(nullable=false)
	private boolean online = false;
	
	@Column(nullable = false)
	private Boolean active;

	@Column(nullable = false)
	private LocalDateTime createdAt;
	
	@Column(nullable = false)
	private LocalDateTime updatedAt;
	
	public User() 
	{
		ratings = new ArrayList<Rating>();
		recomendations = new ArrayList<Recomendation>();
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

	public String getPasseword() 
	{
		return password;
	}

	public void setPasseword(String passeword) 
	{
		this.password = passeword;
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
	
	public boolean isOnline() 
	{
		return online;
	}

	public void setOnline(boolean online) 
	{
		this.online = online;
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

	public List<Rating> getRatings() 
	{
		return ratings;
	}
	
	public List<Movie> getMovies()
	{
		return ratings.stream().map(rating -> rating.getMovie()).collect(Collectors.toList());
	}
	
	public List<Movie> getMoviesRecModel(RecommendationModel recModel)
	{
		List<Movie> movies = ratings.stream().filter(rating -> rating.getRating() >= recModel.relevanceThreshold).map(Rating::getMovie).collect(Collectors.toList());
		int max = movies.size();
		if(recModel.userPreferencesSize > 0) {
			max = Math.min(recModel.userPreferencesSize, max);
		}
		return movies.subList(0, max);
	}
	
	public List<Recomendation> getRecomendations()
	{
		return recomendations;
	}
	
	public List<Recomendation> getRecomendations(RecommendationType similarity) 
	{
		return recomendations.stream().filter(recomendation -> similarity.equals(recomendation.getSimilarity())).collect(Collectors.toList());
	}
	
	public void setRecomendations(List<Recomendation> recomendations)
	{
		this.recomendations = recomendations;
	}
	
	public List<String> getUserBestTerms(RecommendationModel recModel)
	{
		List<ItemValue<String>> tfIdfValues = TFIDFCalculator.bulkTfIdf(getMoviesRecModel(recModel).stream().map(Movie::getTokensList).collect(Collectors.toList()));
		Collections.sort(tfIdfValues, Collections.reverseOrder());
		return tfIdfValues.stream().limit(recModel.userModelSize).map(tfIdf -> tfIdf.item).collect(Collectors.toList());
	}
	
	public List<String> getMovieTokens()
	{
		return ratings.stream().map(rating -> rating.getMovie().getTokensList()).flatMap(List::stream).collect(Collectors.toList());
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
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", ratings=" + ratings + ", recomendations="
				+ recomendations + ", password=" + password + ", profilePicture=" + profilePicture + ", facebookId="
				+ facebookId + ", online=" + online + ", active=" + active + ", createdAt=" + createdAt + ", updatedAt="
				+ updatedAt + "]";
	}
}
