package com.eulersbridge.iEngage.core.events.badge;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Yikai Gong
 */

public class CreateBadgeEventTest {
    BadgeDetails badgeDetails;
    Long badgeId = new Long(0);
    String name = "badgename";
    boolean awarded = false;
    Long timestamp = new Long(0);
    Long xpValue = new Long(10);
    CreateBadgeEvent createBadgeEvent = null;

    @Before
    public void setUp() throws Exception {
        badgeDetails = new BadgeDetails();
        badgeDetails.setNodeId(badgeId);
        badgeDetails.setName(name);
        badgeDetails.setAwarded(awarded);
        badgeDetails.setTimestamp(timestamp);
        badgeDetails.setXpValue(xpValue);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testConstructor() throws Exception {
        createBadgeEvent = new CreateBadgeEvent(badgeDetails);
        assertNotNull("constructor returns null", createBadgeEvent);
    }
}