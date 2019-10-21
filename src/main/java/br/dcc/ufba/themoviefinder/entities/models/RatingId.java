package br.dcc.ufba.themoviefinder.entities.models;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class RatingId implements Serializable
{
	private static final long serialVersionUID = 8730052910049865613L;
	
    @Column(name = "user_id")
	public Integer userId;
    
    @Column(name = "movie_id")
    public Integer movieId;	
	
    public RatingId()
    {
    	
    }
    
	public RatingId(Integer userId, Integer movieId) 
	{
		this.userId = userId;
		this.movieId = movieId;
	}  
    
	@Override
	public int hashCode() 
	{
		return Objects.hash(this);
	}

	@Override
	public boolean equals(Object o) 
	{
        if (this == o) return true;
        if (!(o instanceof RatingId)) return false;
        RatingId that = (RatingId) o;
        return Objects.equals(that, this);
	}
}
