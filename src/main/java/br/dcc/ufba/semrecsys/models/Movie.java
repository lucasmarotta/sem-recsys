package br.dcc.ufba.semrecsys.models;

import java.util.ArrayList;
import java.util.List;

public class Movie 
{
	private String title;
	private String description;
	private String dbPediaURI;
	private List<String> tokens;
	
	public Movie()
	{
		tokens = new ArrayList<String>();
	}
	
	public Movie(String title)
	{
		this.title = title;
		tokens = new ArrayList<String>();
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
	
	public String getDbPediaURI() 
	{
		return dbPediaURI;
	}
	
	public void setDbPediaURI(String dbPediaURI) 
	{
		this.dbPediaURI = dbPediaURI;
	}
	
	public List<String> getTokens() 
	{
		return tokens;
	}
	
	public void setTokens(List<String> tokens) 
	{
		this.tokens = tokens;
	}

	@Override
	public String toString() 
	{
		return "Movie [title=" + title + ", description=" + description + ", dbPediaURI=" + dbPediaURI + ", tokens="
				+ tokens + "]";
	}
}
