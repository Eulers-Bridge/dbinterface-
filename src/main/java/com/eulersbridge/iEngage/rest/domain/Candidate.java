package com.eulersbridge.iEngage.rest.domain;

import com.eulersbridge.iEngage.core.events.candidate.CandidateDetails;
import com.eulersbridge.iEngage.rest.controller.CandidateController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

/**
 * @author Yikai Gong
 */

public class Candidate extends ResourceSupport{
    private Long candidateId;
    private String information;
    private String policyStatement;
    private Set<String> pictures;
    private Long userId;
    private Long positionId;
    private Ticket ticket;

    private static Logger LOG = LoggerFactory.getLogger(Candidate.class);

    public Candidate() {
        if (LOG.isDebugEnabled()) LOG.debug("constructor()");
    }

    public static Candidate fromCandidateDetails(CandidateDetails candidateDetails){
        Candidate candidate = new Candidate();
        String simpleName = Candidate.class.getSimpleName();
        String name = simpleName.substring(0, 1).toLowerCase()
                + simpleName.substring(1);

        candidate.setCandidateId(candidateDetails.getNodeId());
        candidate.setInformation(candidateDetails.getInformation());
        candidate.setPolicyStatement(candidateDetails.getPolicyStatement());
        candidate.setPictures(candidateDetails.getPictures());
        candidate.setUserId(candidateDetails.getUserId());
        candidate.setPositionId(candidateDetails.getPositionId());
        candidate.setTicket(Ticket.fromTicketDetails(candidateDetails.getTicketDetails()));

        // {!begin selfRel}
        candidate.add(linkTo(CandidateController.class).slash(name)
                .slash(candidate.candidateId).withSelfRel());
        // {!end selfRel}
        // {!begin readAll}
        candidate.add(linkTo(CandidateController.class).slash(name + 's')
                .withRel(RestDomainConstants.READALL_LABEL));
        // {!end readAll}

        return candidate;
    }

    public CandidateDetails toCandidateDetails(){
        CandidateDetails candidateDetails = new CandidateDetails();
        candidateDetails.setNodeId(getCandidateId());
         candidateDetails.setInformation(getInformation());
        candidateDetails.setPolicyStatement(getPolicyStatement());
        candidateDetails.setPictures(getPictures());
        candidateDetails.setUserId(getUserId());
        candidateDetails.setPositionId(getPositionId());
        candidateDetails.setTicketDetails(ticket.toTicketDetails());
        return candidateDetails;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getPolicyStatement() {
        return policyStatement;
    }

    public void setPolicyStatement(String policyStatement) {
        this.policyStatement = policyStatement;
    }

    public Set<String> getPictures() {
        return pictures;
    }

    public void setPictures(Set<String> pictures) {
        this.pictures = pictures;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getPositionId()
    {
        return positionId;
    }

    public void setPositionId(Long positionId)
    {
        this.positionId = positionId;
    }
    
	public static Iterator<Candidate> toCandidatesIterator(
			Iterator<CandidateDetails> iter)
	{
		if (null==iter) return null;
		ArrayList <Candidate> elections=new ArrayList<Candidate>();
		while(iter.hasNext())
		{
			CandidateDetails dets=iter.next();
			Candidate thisCandidate=Candidate.fromCandidateDetails(dets);
			Link self = thisCandidate.getLink("self");
			thisCandidate.removeLinks();
			thisCandidate.add(self);
			elections.add(thisCandidate);		
		}
		return elections.iterator();
	}

}
