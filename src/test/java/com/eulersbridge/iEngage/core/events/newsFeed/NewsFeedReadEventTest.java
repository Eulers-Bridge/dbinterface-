package com.eulersbridge.iEngage.core.events.newsFeed;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.eulersbridge.iEngage.core.events.newsFeed.NewsFeedDetails;
import com.eulersbridge.iEngage.core.events.newsFeed.NewsFeedReadEvent;

import static org.junit.Assert.*;

/**
 * @author Yikai Gong
 */

public class NewsFeedReadEventTest {
    final Long nodeId = new Long(1);
    final Long institutionId = new Long(1);
    NewsFeedDetails newsFeedDetails = null;
    NewsFeedReadEvent newsFeedReadEvent = null;
    final Long id = new Long(1);

    @Before
    public void setUp() throws Exception {
        newsFeedDetails = new NewsFeedDetails(institutionId);
        newsFeedDetails.setNodeId(nodeId);
        newsFeedReadEvent = new NewsFeedReadEvent(id,newsFeedDetails);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testStudentYearReadEvent() throws Exception {
        assertNotNull("newsFeedReadEvent is null", newsFeedReadEvent);
    }

    @Test
    public void testGetNewsArticleId() throws Exception {
        assertEquals("id does not match", id, newsFeedReadEvent.getNewsArticleId());
    }

    @Test
    public void testGetReadNewsFeedDetails() throws Exception {
        assertEquals("newsFeedDetails does not match", newsFeedDetails, newsFeedReadEvent.getReadNewsFeedDetails());
    }

    @Test
    public void testNotFound() throws Exception {
        NewsFeedReadEvent newsFeedReadEvent1 = NewsFeedReadEvent.notFound(id);
        assertNotNull("testNotFound() returns null", newsFeedReadEvent1);
        assertFalse("entityFound is not false", newsFeedReadEvent1.isEntityFound());
    }
}