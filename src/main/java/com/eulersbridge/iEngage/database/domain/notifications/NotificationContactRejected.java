/**
 * 
 */
package com.eulersbridge.iEngage.database.domain.notifications;

import java.util.HashMap;

import org.neo4j.graphdb.Direction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.springframework.data.neo4j.repository.GraphRepository;

import com.eulersbridge.iEngage.core.events.contactRequest.ContactRequestDetails;
import com.eulersbridge.iEngage.core.events.notifications.NotificationDetails;
import com.eulersbridge.iEngage.database.domain.ContactRequest;
import com.eulersbridge.iEngage.database.domain.DatabaseDomainConstants;
import com.eulersbridge.iEngage.database.domain.User;
import com.eulersbridge.iEngage.database.repository.ContactRequestRepository;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author Greg Newitt
 *
 */
public class NotificationContactRejected extends Notification implements NotificationInterface
{
	@RelatedTo(type = DatabaseDomainConstants.HAS_NOTIFICATION_DETAILS_LABEL, direction=Direction.OUTGOING) @Fetch
	ContactRequest contactRequest;

    static Logger LOG = LoggerFactory.getLogger(NotificationContactRejected.class);


    @Override
    public Boolean setupForSave(HashMap<String,GraphRepository<?>> repos)
    {
    	if (LOG.isDebugEnabled()) LOG.debug("setupForSave()");
		Boolean response=false;
		ContactRequest result=null;
    	response = super.setupForSave(repos);
    	if (response)
    	{
    		if (LOG.isDebugEnabled()) LOG.debug("Super method successful.");
	    	response=false;
    		ContactRequestRepository crRepo=(ContactRequestRepository)repos.get(ContactRequestRepository.class.getSimpleName());
	    	if (crRepo!=null)
			{
	    		if (LOG.isDebugEnabled()) LOG.debug("CR repository retrieved. - "+getContactRequest());
				if (getContactRequest()!=null)
				{	
					if (getContactRequest().getNodeId()!=null)
					{
			    		if (LOG.isDebugEnabled()) LOG.debug("NodeId present. - ");
						result=crRepo.findOne(getContactRequest().getNodeId());
					}
					else if ((getContactRequest().getUser()!=null)&&(getContactRequest().getUser().getNodeId()!=null)&&(getContactRequest().getContactDetails()!=null))
					{
			    		if (LOG.isDebugEnabled()) LOG.debug("other info present - "+getContactRequest().getUser().getNodeId()+" , "+getContactRequest().getContactDetails());
						result=crRepo.findContactRequestByUserIdContactInfo(getContactRequest().getUser().getNodeId(), getContactRequest().getContactDetails());
					}
					if (result!=null)
					{
						setContactRequest(result);
						response=true;
					}
				}
			}
    	}
		return response;
    	
    }
    
    @Override
	public NotificationDetails toNotificationDetails()
	{
		if (LOG.isDebugEnabled()) LOG.debug("toNotificationDetails()");
		Long userId=null;
		if (user!=null) userId=user.getNodeId();
		
		ContactRequestDetails contactRequestDetails=contactRequest.toContactRequestDetails();
		NotificationDetails dets=new NotificationDetails(nodeId, userId, timestamp, isRead(), type, contactRequestDetails);
		return dets;
	}
	
	public static NotificationContactRejected fromNotificationDetails(NotificationDetails nDets)
	{
		NotificationContactRejected notif=new NotificationContactRejected();
		if (nDets!=null)
		{
			if (NotificationConstants.CONTACT_REJECTED.equals(nDets.getType()))
			{
				ContactRequestDetails crd=(ContactRequestDetails)nDets.getNotificationBody();
				ContactRequest cr=ContactRequest.fromContactRequestDetails(crd);
				notif.setContactRequest(cr);
			}
			notif.setType(nDets.getType());
			notif.setNodeId(nDets.getNodeId());
			User user=new User(nDets.getUserId());
			notif.setUser(user);
			notif.setRead(nDets.getRead());
			notif.setTimestamp(nDets.getTimestamp());
		}
		return notif;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "NotificationContactRejected [contactRequest=" + contactRequest
				+ ", nodeId=" + nodeId + ", read=" + isRead() + ", timestamp="
				+ timestamp + ", type=" + type + ", user=" + user + "]";
	}

	public static ContactRequestDetails populateFields(JsonNode node) throws JsonMappingException
	{
		return NotificationContactRequest.populateFields(node);
	}

	/**
	 * @return the contactRequest
	 */
	public ContactRequest getContactRequest()
	{
		return contactRequest;
	}

	/**
	 * @param contactRequest the contactRequest to set
	 */
	public void setContactRequest(ContactRequest contactRequest)
	{
		this.contactRequest = contactRequest;
	}

}
