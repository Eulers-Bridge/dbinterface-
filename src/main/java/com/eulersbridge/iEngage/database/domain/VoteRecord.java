package com.eulersbridge.iEngage.database.domain;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

@RelationshipEntity(type=DatabaseDomainConstants.VRECORD_LABEL)
public class VoteRecord 
{
	@GraphId private Long nodeId;
	@StartNode private User voter;
	@EndNode private Election election;
	private Long date;
	private String location;
	private Long timestamp;
	
    private static Logger LOG = LoggerFactory.getLogger(VoteRecord.class);

    public VoteRecord() 
	{
		if (LOG.isTraceEnabled()) LOG.trace("Constructor");
	}

	public VoteRecord(Long date,String location)
	{
		if (LOG.isTraceEnabled()) LOG.trace("Constructor("+date+','+location+')');
		this.date=date;
		this.location=location;
		timestamp=Calendar.getInstance().getTimeInMillis();
	}
	
	public Long getNodeId()
	{
		return nodeId;
	}
	
	public Long getDate()
	{
		if (LOG.isDebugEnabled()) LOG.debug("getYear() = "+date);
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Long date) {
		this.date = date;
	}

	public String getLocation()
	{
		if (LOG.isDebugEnabled()) LOG.debug("getEnd() = "+location);
		return location;
	}
	
	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the voter
	 */
	public User getVoter() {
		return voter;
	}

	/**
	 * @param voter the voter to set
	 */
	public void setVoter(User voter) {
		this.voter = voter;
	}

	/**
	 * @return the election
	 */
	public Election getElection() {
		return election;
	}

	/**
	 * @param election the election to set
	 */
	public void setElection(Election election) {
		this.election = election;
	}

	/**
	 * @return the timestamp
	 */
	public Long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "VoteReminder [nodeId=" + nodeId + ", voter=" + voter
				+ ", election=" + election + ", date=" + date
				+  ", location=" + location + ", timestamp=" + timestamp
				+ "]";
	}	

	@Override
	public boolean equals(Object other)
	{
		if (null == other) return false;
		if (other == this) return true;
		if (!(other instanceof VoteRecord)) return false;
		VoteRecord answer2=(VoteRecord) other;

		if (getNodeId()!=null)
		{
				if (getNodeId().equals(answer2.getNodeId())) return true;
		}
		else
		{
			if (null==answer2.getNodeId())
			{
				if ((this.getDate().equals(answer2.getDate()))&&
					(this.getTimestamp().equals(answer2.getTimestamp()))&&
					(this.getVoter().equals(answer2.getVoter()))&&
					(this.getElection().equals(answer2.getElection()))&&
					(this.getLocation().equals(answer2.getLocation())))
					return true;
			}
		}
		return false;
	}
}
