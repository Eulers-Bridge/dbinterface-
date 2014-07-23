package com.eulersbridge.iEngage.database.domain;

import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import com.eulersbridge.iEngage.core.events.countrys.CountryDetails;

@NodeEntity
public class Country 
{
	@GraphId Long nodeId;
	private String countryName;
	@RelatedTo(type = "HAS_INSTITUTIONS", direction=Direction.OUTGOING)
	private
	Set<Institution>  institutions; 

    private static Logger LOG = LoggerFactory.getLogger(Country.class);

    public Country(String countryName)
	{
    	if (LOG.isDebugEnabled()) LOG.debug("Constructor("+countryName+')');
		this.countryName=countryName;
	}
	
	public Long getNodeId() 
	{
		return nodeId;
	}

	public String getCountryName() {
		return countryName;
	}
	
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	
	public String toString()
	{
		StringBuffer buff=new StringBuffer("[ nodeId = ");
		String retValue;
		buff.append(getNodeId());
		buff.append(", name = ");
		buff.append(getCountryName());
		buff.append(" ]");
		retValue=buff.toString();
		if (LOG.isDebugEnabled()) LOG.debug("toString() = "+retValue);
		return retValue;
	}
	
	public CountryDetails toCountryDetails() 
	{
	    if (LOG.isTraceEnabled()) LOG.trace("toCountryDetails()");
	    
	    CountryDetails details = new CountryDetails(getNodeId());
	    if (LOG.isTraceEnabled()) LOG.trace("country "+this);

	    BeanUtils.copyProperties(this, details);
	    if (LOG.isTraceEnabled()) LOG.trace("countryDetails "+details);

	    return details;
	}

	  public static Country fromCountryDetails(CountryDetails countryDetails) 
	  {
		    if (LOG.isTraceEnabled()) LOG.trace("fromCountryDetails()");

		    if (LOG.isTraceEnabled()) LOG.trace("countryDetails "+countryDetails);
		    Country country = new Country(countryDetails.getCountryName());
		    country.nodeId=countryDetails.getCountryId();
		    if (LOG.isTraceEnabled()) LOG.trace("country "+country);

		    return country;
		  }
	  
	  public boolean equals(Country country2)
	  {
		  if ((nodeId!=null)&&(nodeId.equals(country2.nodeId))) return true;
		  else return false;
	  }

	public void setNodeId(Long id) 
	{
		this.nodeId=id;
	}
}
