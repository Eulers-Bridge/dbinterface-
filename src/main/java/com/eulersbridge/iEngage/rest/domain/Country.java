package com.eulersbridge.iEngage.rest.domain;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import org.springframework.hateoas.ResourceSupport;

import com.eulersbridge.iEngage.core.events.countrys.CountryDetails;
import com.eulersbridge.iEngage.rest.controller.CountryController;

public class Country extends ResourceSupport
{
	Long countryId;
	String countryName;
	Institution universities[];
	
	public Country(Long id,String name,Institution unis[])
	{
		countryId=id;
		countryName=name;
		universities=unis;
	}
	
	public Country() 
	{

	}

	public Long getCountryId()
	{
		return countryId;
	}
	
	public String getCountryName()
	{
		return countryName;
	}
	
	public Institution[] getUniversities()
	{
		return universities;
	}
	
	public CountryDetails toCountryDetails() 
	  {
		CountryDetails details = new CountryDetails(countryId);

	    details.setCountryName(getCountryName());

	    return details;
	  }

	  // {!begin fromOrderDetails}
	  public static Country fromCountryDetails(CountryDetails readCountry) 
	  {
		  Country country = new Country();

		country.countryId = readCountry.getCountryId();
		country.countryName = readCountry.getCountryName();
	    
	    //TODOCUMENT.  Adding the library, the above extends ResourceSupport and
	    //this section is all that is actually needed in our model to add hateoas support.

	    //Much of the rest of the framework is helping deal with the blending of domains that happens in many spring apps
	    //We have explicitly avoided that.
	    // {!begin selfRel}
	    country.add(linkTo(CountryController.class).slash(country.countryId).withSelfRel());
	    // {!end selfRel}

	    return country;
	  }

	public void setId(Long countryId) 
	{
		this.countryId=countryId;		
	}

}
