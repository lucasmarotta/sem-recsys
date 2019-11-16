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
public class Recomendation 
{
	@EmbeddedId
	private RecomendationId id;	
	
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
	
	public Recomendation()
	{
		this(null, null, RecomendationType.RLWS_DIRECT, null);
	}
	
	public Recomendation(User user, Movie movie, RecomendationType similarity, Double score)
	{
		if(ObjectUtils.allNotNull(user, movie)) {
			id = new RecomendationId(user.getId(), movie.getId(), similarity);	
		} else {
			id = new RecomendationId(null, null, similarity);
		}		
		this.user = user;
		this.movie = movie;
		this.score = score;
	}
	
	public RecomendationId getRecId() 
	{
		return id;
	}

	public void setRecId(RecomendationId recId) 
	{
		this.id = recId;
	}

	public RecomendationId getId() 
	{
		return id;
	}

	public void setId(RecomendationId id) 
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
		return "Recomendation [User=" + user.getEmail() + ", similarity = " + id.similarity + "]"
				+ "\n[" + movie + ", score=" + score + "]\n" + TFIDFCalculator.uniqueValues(movie.getTokensList());
	}
}
