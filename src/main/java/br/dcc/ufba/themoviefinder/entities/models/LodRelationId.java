package br.dcc.ufba.themoviefinder.entities.models;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.ObjectUtils;

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
		if(ObjectUtils.allNotNull(resource1, resource2)) {
			if(! resource1.equalsIgnoreCase(resource2)) {
				this.resource1 = resource1;
				this.resource2 = resource2;
			} else {
				throw new IllegalArgumentException("resource1 and resource2 must be different");
			}	
		} else {
			throw new NullPointerException("resource1 and resource2 must not be null");
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
	public String toString() 
	{
		return "LodRelationId [resource1=" + resource1 + ", resource2=" + resource2 + "]";
	}

	@Override
    public boolean equals(Object o) 
    {
        if (this == o) return true;
        if (!(o instanceof LodRelationId)) return false;
        LodRelationId that = (LodRelationId) o;
        if(ObjectUtils.allNotNull(resource1, resource2)) {
            return (resource1.equalsIgnoreCase(that.getResource1()) &&
            		resource2.equalsIgnoreCase(that.getResource2())) ||
            		(resource1.equalsIgnoreCase(that.getResource2()) &&
            		resource2.equalsIgnoreCase(that.getResource1()));	
        } else {
            return (Objects.equals(getResource1(), that.getResource1()) &&
                    Objects.equals(getResource2(), that.getResource2())) ||
            		(Objects.equals(getResource1(), that.getResource2()) &&
            		Objects.equals(getResource2(), that.getResource1()));
        }
    }
 
    @Override
    public int hashCode() 
    {
        int hash1 = Objects.hash(getResource1().toLowerCase(), getResource2().toLowerCase());
        int hash2 = Objects.hash(getResource2().toLowerCase(), getResource1().toLowerCase());
        return (hash1 < hash2) ? hash1 : hash2;
    }
}
