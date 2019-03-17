package br.dcc.ufba.themoviefinder.entities.models;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class UserRecomendationId implements Serializable
{
	private static final long serialVersionUID = 3062545770329358459L;
	
    @Column(name = "user_id")
	public Integer userId;
    
    @Column(name = "movie_id")
    public Integer movieId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "similarity")
    public RecomendationType similarity;

    public UserRecomendationId()
    {
    	
    }
    
	public UserRecomendationId(Integer userId, Integer movieId, RecomendationType similarity) 
	{
		this.userId = userId;
		this.movieId = movieId;
		this.similarity = similarity;
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
        if (!(o instanceof UserRecomendationId)) return false;
        UserRecomendationId that = (UserRecomendationId) o;
        return Objects.equals(that, this);
	}
}
