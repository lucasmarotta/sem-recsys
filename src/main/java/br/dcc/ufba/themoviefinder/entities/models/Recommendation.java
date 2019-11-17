package br.dcc.ufba.themoviefinder.entities.models;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import org.apache.commons.lang3.ObjectUtils;

import br.dcc.ufba.themoviefinder.utils.TFIDFCalculator;

@Entity
public class Recommendation 
{
	@EmbeddedId
	private RecommendationId id;	
	
	@ManyToOne(fetch=FetchType.EAGER)
    @MapsId("user_id")
    @JoinColumn(name = "user_id")
	private User user;
    
    @ManyToOne(fetch=FetchType.EAGER)
    @MapsId("movie_id")
    @JoinColumn(name = "movie_id")
	private Movie movie;
    
    @Column(nullable = false)
	private Double score;
    
    private Double rate;
	
	public Recommendation()
	{
		this(null, null, RecommendationType.RLWS_DIRECT, null);
	}
	
	public Recommendation(User user, Movie movie, RecommendationType similarity, Double score)
	{
		if(ObjectUtils.allNotNull(user, movie)) {
			id = new RecommendationId(user.getId(), movie.getId(), similarity);	
		} else {
			id = new RecommendationId(null, null, similarity);
		}		
		this.user = user;
		this.movie = movie;
		this.score = score;
	}
	
	public RecommendationId getRecId() 
	{
		return id;
	}

	public void setRecId(RecommendationId recId) 
	{
		this.id = recId;
	}

	public RecommendationId getId() 
	{
		return id;
	}

	public void setId(RecommendationId id) 
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

	public RecommendationType getSimilarity() 
	{
		return id.similarity;
	}

	public void setSimilarity(RecommendationType similarity) 
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
	
	public Double getRate() 
	{
		return rate;
	}

	public void setRate(Double rate) 
	{
		this.rate = rate;
	}

	@Override
	public String toString()
	{
		return "Recommendation [User=" + user.getEmail() + ", similarity = " + id.similarity + "]"
				+ "\n[" + movie + ", score=" + score + "]\n" + TFIDFCalculator.uniqueValues(movie.getTokensList());
	}
}
