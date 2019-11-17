package br.dcc.ufba.themoviefinder.entities.models;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class RecommendationId implements Serializable
{
	private static final long serialVersionUID = 3062545770329358459L;
	
    @Column(name = "user_id")
	public Integer userId;
    
    @Column(name = "movie_id")
    public Integer movieId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "similarity")
    public RecommendationType similarity;

    public RecommendationId()
    {
    	
    }
    
	public RecommendationId(Integer userId, Integer movieId, RecommendationType similarity) 
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
        if (!(o instanceof RecommendationId)) return false;
        RecommendationId that = (RecommendationId) o;
        return Objects.equals(that, this);
	}

	@Override
	public String toString() 
	{
		return "RecomendationId [userId=" + userId + ", movieId=" + movieId + ", similarity=" + similarity + "]";
	}
}
