package br.dcc.ufba.themoviefinder.entities.models;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import org.apache.commons.lang3.ObjectUtils;

@Entity
public class Rating 
{
	@EmbeddedId
	private RatingId id;	
	
	@ManyToOne(fetch=FetchType.EAGER)
	@MapsId("user_id")
    @JoinColumn(name = "user_id")
	private User user;
    
    @ManyToOne(fetch=FetchType.EAGER)
    @MapsId("movie_id")
    @JoinColumn(name = "movie_id")
	private Movie movie;
	
    @Column(nullable=false)
	private Double rating;
    
	public Rating()
	{
		this(null, null, 0d);
	}
	
	public Rating(User user, Movie movie, Double rating)
	{
		if(ObjectUtils.allNotNull(user, movie)) {
			id = new RatingId(user.getId(), movie.getId());	
		} else {
			id = new RatingId(null, null);
		}
		this.user = user;
		this.movie = movie;
		this.rating = rating;
	}
	
	public RatingId getId() 
	{
		return id;
	}

	public void setId(RatingId id) 
	{
		this.id = id;
	}
	
	public void setId(Integer userId, Integer movieId)
	{
		if(ObjectUtils.allNotNull(user, movie)) {
			id.userId = userId;
			id.movieId = movieId;	
		} else {
			throw new NullPointerException("userId and movieId must not be null");
		}
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
	
	public Double getRating() 
	{
		return rating;
	}
	
	public void setRating(Double rating) 
	{
		this.rating = rating;
	}

	@Override
	public String toString()
	{
		return "Rating [user=" + user + ", movie=" + movie + ", rating=" + rating + "]";
	}
}
