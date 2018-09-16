package br.dcc.ufba.themoviefinder.entities.models;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class LodRelationId implements Serializable
{
	private static final long serialVersionUID = -1626772151012384126L;

	@Column(name = "resource1")
	private String resource1;
	
	@Column(name = "resource2")
	private String resource2;
	
	public LodRelationId()
	{
		
	}

	public LodRelationId(String resource1, String resource2) 
	{
		if(! resource1.equalsIgnoreCase(resource2)) {
			this.resource1 = resource1;
			this.resource2 = resource2;	
		} else {
			throw new IllegalArgumentException("resource1 and resource2 must be different");
		}
	}
	
	public String getResource1() 
	{
		return resource1;
	}

	public void setResource1(String resource1) 
	{
		this.resource1 = resource1;
	}

	public String getResource2() 
	{
		return resource2;
	}

	public void setResource2(String resource2) 
	{
		this.resource2 = resource2;
	}
	
    @Override
    public boolean equals(Object o) 
    {
        if (this == o) return true;
        if (!(o instanceof LodRelationId)) return false;
        LodRelationId that = (LodRelationId) o;
        return Objects.equals(getResource1(), that.getResource1()) &&
                Objects.equals(getResource2(), that.getResource2());
    }
 
    @Override
    public int hashCode() 
    {
        return Objects.hash(getResource1(), getResource2());
    }
}