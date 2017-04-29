package com.eulersbridge.iEngage.core.events.events;

import com.eulersbridge.iEngage.core.events.photo.PhotoDetails;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * @author Yikai Gong
 */

public class UpdateEventEventTest {
    private Long eventId = new Long(0);
    private String name = "new event";
    private String location = "campus";
    private Long date = 1000000l;
    private Long ends =1000000l;
    private String description = "An event";
    private Iterable<PhotoDetails> photos = null;
    private String volunteerPositions[] = new String[]{"positon"};
    private Long created = new Long(0);
    private String organizer = "Unimelb";
    private String organizerEmail = "test@test.com";
    private Long modified = new Long(1);
    private Long institutionId=26l;;

    private EventDetails eventDetails;
    private UpdateEventEvent updateEventEvent;

    @Before
    public void setUp() throws Exception {
        eventDetails = new EventDetails();
        eventDetails.setEventId(eventId);
        eventDetails.setName(name);
        eventDetails.setLocation(location);
        eventDetails.setStarts(date);
        eventDetails.setEnds(ends);
        eventDetails.setDescription(description);
        eventDetails.setPhotos(photos);
        eventDetails.setVolunteerPositions(volunteerPositions);
        eventDetails.setCreated(created);
        eventDetails.setOrganizer(organizer);
        eventDetails.setOrganizerEmail(organizerEmail);
        eventDetails.setModified(modified);
        eventDetails.setInstitutionId(institutionId);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testConstructor() throws Exception{
        updateEventEvent = new UpdateEventEvent(eventId, eventDetails);
        assertNotNull("updateEventEvent is null", updateEventEvent);
    }
}