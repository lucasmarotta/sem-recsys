package br.dcc.ufba.themoviefinder.entities.models;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Idf 
{
	@Id
	private String term;
	private Float value;
	
	public Idf()
	{
		value = 0f;
	}
	
	public Idf(String term, Float value)
	{
		this.term = term;
		this.value = value;
	}
	
	public String getTerm() 
	{
		return term;
	}
	
	public void setTerm(String term) 
	{
		this.term = term;
	}
	
	public Float getValue() 
	{
		return value;
	}
	
	public void setValue(Float value) 
	{
		this.value = value;
	}

	@Override
	public String toString() 
	{
		return "Idf [term=" + term + ", value=" + value + "]";
	}
}
