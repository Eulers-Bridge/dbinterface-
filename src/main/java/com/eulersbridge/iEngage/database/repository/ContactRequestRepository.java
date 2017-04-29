/**
 * 
 */
package com.eulersbridge.iEngage.database.repository;

import com.eulersbridge.iEngage.database.domain.ContactRequest;
import com.eulersbridge.iEngage.database.domain.DatabaseDomainConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author Greg Newitt
 *
 */
public interface ContactRequestRepository extends
		GraphRepository<ContactRequest>
{
	@Query ("MATCH (u:`"+DatabaseDomainConstants.USER+"`)-[r:`"+DatabaseDomainConstants.CONTACT_REQUEST_LABEL+"`]-(c:`"+DatabaseDomainConstants.CONTACT_REQUEST+"`) where id(u)={userId} and c.contactDetails={contactInfo} RETURN c")
	ContactRequest findContactRequestByUserIdContactInfo(@Param("userId")Long userId, @Param("contactInfo")String contactInfo);

	@Query ("MATCH (u:`"+DatabaseDomainConstants.USER+"`), (c:`"+DatabaseDomainConstants.CONTACT_REQUEST+"`) where id(u)={userId} and ((c.contactDetails=u.contactNumber)or(c.contactDetails=u.email)) RETURN c")
	Page<ContactRequest> findReceivedRequestsByUserId(@Param("userId")Long userId, Pageable pageable);

	@Query ("MATCH (u:`"+DatabaseDomainConstants.USER+"`)-[r:`"+DatabaseDomainConstants.CONTACT_REQUEST_LABEL+"`]-(c:`"+DatabaseDomainConstants.CONTACT_REQUEST+"`) where id(u)={userId} RETURN c")
	Page<ContactRequest> findSentRequests(@Param("userId")Long userId, Pageable pageable);

}
