package br.dcc.ufba.themoviefinder.entities.models;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import org.apache.commons.lang3.ObjectUtils;

@Entity
public class UserRecomendation 
{
	@EmbeddedId
	private UserRecomendationId id;
	
	@ManyToOne(fetch=FetchType.EAGER)
    @MapsId("user_id")
    @JoinColumn(name = "user_id")
	private User user;
    
    @ManyToOne(fetch=FetchType.EAGER)
    @MapsId("movie_id")
    @JoinColumn(name = "movie_id")
	private Movie movie;
    
	private Double score;
	
	public UserRecomendation()
	{
		this(null, null, RecomendationType.RLW, null);
	}
	
	public UserRecomendation(User user, Movie movie, RecomendationType similarity, Double score)
	{
		if(ObjectUtils.allNotNull(user, movie)) {
			id = new UserRecomendationId(user.getId(), movie.getId(), similarity);	
		} else {
			id = new UserRecomendationId(null, null, similarity);
		}
		this.user = user;
		this.movie = movie;
		this.score = score;
	}

	public UserRecomendationId getId() 
	{
		return id;
	}

	public void setId(UserRecomendationId id) 
	{
		this.id = id;
	}

	public User getUser() 
	{
		return user;
	}

	public void setUser(User user) 
	{
		this.user = user;
	}

	public Movie getMovie() 
	{
		return movie;
	}

	public void setMovie(Movie movie) 
	{
		this.movie = movie;
	}

	public RecomendationType getSimilarity() 
	{
		return id.similarity;
	}

	public void setSimilarity(RecomendationType similarity) 
	{
		id.similarity = similarity;
	}

	public Double getScore() 
	{
		return score;
	}

	public void setScore(Double score) 
	{
		this.score = score;
	}

	@Override
	public String toString() {
		return "UserRecomendation [user=" + user + ", movie=" + movie + ", similarity=" + id.similarity + ", score=" + score + "]";
	}
}
