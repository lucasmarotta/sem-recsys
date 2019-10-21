package br.dcc.ufba.themoviefinder.entities.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

@Entity
public class Movie 
{
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false)
	private String title;
	
	@Column(nullable = false)
	@Type(type="text")
	private String description;
	
	@Type(type="text")
	private String tokens;
	
	private Double imdbRating;
	private Integer imdbId;
	private Integer tmdbId;
	
	@Transient
	private List<String> tokensList;
	
	public Movie() {}
	
	public Movie(int id)
	{
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

	public String getTitle() 
	{
		return title;
	}

	public void setTitle(String title) 
	{
		this.title = title;
	}

	public String getDescription() 
	{
		return description;
	}

	public void setDescription(String description) 
	{
		this.description = description;
	}

	public String getTokens() 
	{
		return tokens;
	}

	public List<String> getTokensList()
	{
		if(tokensList == null) {
			tokensList = stringToList(tokens);
		}
		return tokensList;
	}

	public void setTokens(String tokens) 
	{
		this.tokens = tokens;
	}
	
	public void setTokens(List<String> tokens)
	{
		tokensList = tokens;
		this.tokens = listToString(tokens);
	}

	public Double getImdbRating() 
	{
		return imdbRating;
	}

	public void setImdbRating(Double imdbRating) 
	{
		this.imdbRating = imdbRating;
	}

	public Integer getImdbId() 
	{
		return imdbId;
	}

	public void setImdbId(Integer imdbId) 
	{
		this.imdbId = imdbId;
	}

	public Integer getTmdbId() 
	{
		return tmdbId;
	}

	public void setTmdbId(Integer tmdbId) 
	{
		this.tmdbId = tmdbId;
	}
	
	private List<String> stringToList(String str)
	{
		if(str != null) {
			List<String> list = new ArrayList<String>();
			String[] splitArr = str.split(",");
			for (String string : splitArr) {
				if(! string.isEmpty()) {
					list.add(string);
				}
			}
			return list;
		}
		return new ArrayList<String>();
	}
	
	private String listToString(List<String> list)
	{
		if(list != null && !list.isEmpty()) {
			return String.join(",", list);
		}
		return null;
	}
	
    @Override
    public boolean equals(Object o) 
    {
        if (this == o) return true;
        if (!(o instanceof Movie)) return false;
        Movie m = (Movie) o;
        if(id.intValue() == m.getId().intValue()) {
        	return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() 
    {
        int tmp = 0;
        if(id != null) {
            tmp = (id.intValue() + title).hashCode();	
        } else if(title != null) {
        	tmp = (title).hashCode();
        }
        return tmp;
    }

	@Override
	public String toString() 
	{
		return "Movie [id=" + id + ", title=" + title + ", imdbId=" + imdbId + ", tmdbId=" + tmdbId + "]";
	}
}
