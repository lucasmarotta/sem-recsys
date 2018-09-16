package br.dcc.ufba.themoviefinder.entities.models;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class LodCounter 
{
	@Id
	private String resource;
	private Integer directLinks;
	private Integer indirectLinks;
	
	public LodCounter()
	{
		
	}
	
	public LodCounter(String resource)
	{
		this.resource = resource;
	}
	
	public LodCounter(String resource, Integer directLink, Integer indirectLink) 
	{
		this.resource = resource;
		this.directLinks = directLink;
		this.indirectLinks = indirectLink;
	}

	public String getResource() 
	{
		return resource;
	}
	
	public void setResource(String resource) 
	{
		this.resource = resource;
	}

	public Integer getDirectLinks() 
	{
		return directLinks;
	}

	public void setDirectLinks(Integer directLinks) 
	{
		this.directLinks = directLinks;
	}

	public Integer getIndirectLinks() 
	{
		return indirectLinks;
	}

	public void setIndirectLinks(Integer indirectLinks) 
	{
		this.indirectLinks = indirectLinks;
	}

	@Override
	public String toString() 
	{
		return "LodCounter [resource=" + resource + ", directLinks=" + directLinks + ", indirectLinks=" + indirectLinks
				+ "]";
	}
}
