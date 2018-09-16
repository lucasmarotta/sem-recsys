package br.dcc.ufba.themoviefinder.entities.models;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class LodRelationCounter 
{
	@EmbeddedId
	private LodRelationId id;
	private Integer directLinks;
	private Integer indirectLinks;
	
	public LodRelationCounter()
	{
		
	}
	
	public LodRelationCounter(String resource1, String resource2) 
	{
		id = new LodRelationId(resource1, resource2);
	}
	
	public LodRelationCounter(String resource1, String resource2, Integer directLinks, Integer indirectLinks)
	{
		this(new LodRelationId(resource1, resource2), directLinks, indirectLinks);
	}
	
	public LodRelationCounter(LodRelationId id, Integer directLinks, Integer indirectLinks) 
	{
		this.id = id;
		this.directLinks = directLinks;
		this.indirectLinks = indirectLinks;
	}

	public LodRelationId getId() 
	{
		return id;
	}
	
	public void setId(LodRelationId id) 
	{
		this.id = id;
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
}
