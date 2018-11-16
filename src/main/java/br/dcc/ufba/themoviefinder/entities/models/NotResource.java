package br.dcc.ufba.themoviefinder.entities.models;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class NotResource 
{
	@Id
	private String resource;

	public NotResource()
	{
		
	}
	
	public NotResource(String resource)
	{
		this.resource = resource;
	}	

	public String getResource()
	{
		return resource;
	}

	public void setResource(String resource) 
	{
		this.resource = resource;
	}
	
    public boolean equals(Object o) 
    {
        if (this == o) return true;
        if (!(o instanceof NotResource)) return false;
        NotResource that = (NotResource) o;
        return resource.equalsIgnoreCase(that.getResource());
    }
    
    @Override
    public int hashCode() 
    {
    	return Objects.hash(resource);
    }

	@Override
	public String toString() 
	{
		return resource;
	}
}
