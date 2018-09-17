package br.dcc.ufba.themoviefinder.entities.models;

import java.util.Objects;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class LodCacheRelation 
{
	@EmbeddedId
	private LodRelationId id;
	private Integer directLinks;
	private Integer indirectLinks;
	
	public LodCacheRelation()
	{
		directLinks = 0;
		indirectLinks = 0;
	}
	
	public LodCacheRelation(LodRelationId id)
	{
		this.id = id;
	}
	
	public LodCacheRelation(String resource1, String resource2)
	{
		this();
		id = new LodRelationId(resource1, resource2);
	}
	
	public LodCacheRelation(String resource1, String resource2, Integer directLinks, Integer indirectLinks) 
	{
		this.id = new LodRelationId(resource1, resource2);
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

	@Override
	public String toString() 
	{
		return "LodCacheRelation [id=" + id + ", directLinks=" + directLinks + ", indirectLinks=" + indirectLinks + "]";
	}
	
	@Override
    public boolean equals(Object o) 
    {
        if (this == o) return true;
        if (!(o instanceof LodCacheRelation)) return false;
        LodCacheRelation that = (LodCacheRelation) o;
        return (Objects.equals(that.getId(), id));
    }
 
    @Override
    public int hashCode() 
    {
        return Objects.hash(id);
    }
}
