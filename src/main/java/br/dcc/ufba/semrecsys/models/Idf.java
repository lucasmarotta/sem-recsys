package br.dcc.ufba.semrecsys.models;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Idf 
{
	@Id
	private String term;
	private Float value;
	private Float extendedValue;
	
	public Idf()
	{
		
	}
	
	public Idf(String term, Float value, Float extendedValue)
	{
		this.term = term;
		this.value = value;
		this.extendedValue = extendedValue;
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
	
	public Float getExtendedValue() 
	{
		return extendedValue;
	}
	
	public void setExtendedValue(Float extendedValue) 
	{
		this.extendedValue = extendedValue;
	}

	@Override
	public String toString() 
	{
		return "Idf [term=" + term + ", value=" + value + ", extendedValue=" + extendedValue + "]";
	}
}
