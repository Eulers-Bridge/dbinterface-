/**
 * 
 */
package com.eulersbridge.iEngage.core.events.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Greg Newitt
 *
 */
public class UserDetails 
{

	private String email;
	private String givenName;
	private String familyName;
	private String gender;
	private String nationality;
	private String yearOfBirth;
	private String password;
	private boolean accountVerified=false;
	private	Long institutionId;
	
    private static Logger LOG = LoggerFactory.getLogger(UserDetails.class);
    
	public UserDetails() 
	{
	}

	public UserDetails(String email) 
	{
		this.email=email;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getYearOfBirth() {
		return yearOfBirth;
	}

	public void setYearOfBirth(String yearOfBirth) {
		this.yearOfBirth = yearOfBirth;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Long getInstitutionId() {
		return institutionId;
	}

	public void setInstitutionId(Long institutionId) {
		this.institutionId = institutionId;
	}

	public void setEmail(String email) 
	{
		this.email=email;
		
	}

	public String getEmail() {
		return email;
	}

	/**
	 * @return the firstName
	 */
	public String getGivenName() {
		return givenName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	/**
	 * @return the accountVerified
	 */
	public boolean isAccountVerified() {
		return accountVerified;
	}

	/**
	 * @param accountVerified the accountVerified to set
	 */
	public void setAccountVerified(boolean accountVerified) {
		this.accountVerified = accountVerified;
	}
	
	@Override
	public String toString()
	{
		StringBuffer buff=new StringBuffer("[ email = ");
		String retValue;
		buff.append(getEmail());
		buff.append(", givenName = ");
		buff.append(getGivenName());
		buff.append(", familyName = ");
		buff.append(getFamilyName());
		buff.append(", gender = ");
		buff.append(getGender());
		buff.append(", nationality = ");
		buff.append(getNationality());
		buff.append(", yearOfBirth = ");
		buff.append(getYearOfBirth());
		buff.append(", password = ");
		buff.append(getPassword());
		buff.append(", accountVerified = ");
		buff.append(isAccountVerified());
		buff.append(" ]");
		retValue=buff.toString();
		if (LOG.isDebugEnabled()) LOG.debug("toString() = "+retValue);
		return retValue;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (null == other) return false;
		if (other == this) return true;
		if (!(other instanceof UserDetails)) return false;
		UserDetails dets2=(UserDetails) other;
		if (dets2.getEmail()!=null)
		{
			if (dets2.getEmail().equals(getEmail()))
			return true;
		}
		return false;
	}
}
