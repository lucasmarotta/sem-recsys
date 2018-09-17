package br.dcc.ufba.themoviefinder.entities.models;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class LodCache 
{
	@Id
	private String resource;
	private Integer directLinks;
	private Integer indirectLinks;
	
	public LodCache()
	{
		directLinks = 0;
		indirectLinks = 0;
	}
	
	public LodCache(String resource)
	{
		this();
		this.resource = resource;
	}
	
	public LodCache(String resource, Integer directLink, Integer indirectLink) 
	{
		this();
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
		return "LodCache [resource=" + resource + ", directLinks=" + directLinks + ", indirectLinks=" + indirectLinks
				+ "]";
	}
	
	@Override
    public boolean equals(Object o) 
    {
        if (this == o) return true;
        if (!(o instanceof LodCache)) return false;
        LodCache that = (LodCache) o;
        return (Objects.equals(that.getResource(), this.getResource()));
    }
 
    @Override
    public int hashCode() 
    {
        return Objects.hash(resource);
    }	
}
