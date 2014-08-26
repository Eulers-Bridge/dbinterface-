package com.eulersbridge.iEngage.core.events.newsArticles;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Yikai Gong
 */

public class ReadNewsArticlesEventTest {
    final Long instId = new Long(1);
    final Long syId = new Long(2);
    ReadNewsArticlesEvent readNewsArticlesEvent = null;

    @Before
    public void setUp() throws Exception {
        readNewsArticlesEvent = new ReadNewsArticlesEvent(syId, instId);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testReadNewsArticlesEvent() throws Exception {
        assertNotNull("readNewsArticlesEvent is null", readNewsArticlesEvent);
    }

    @Test
    public void testGetInstId() throws Exception {
        assertEquals("instId does not match", instId, readNewsArticlesEvent.getInstId());
    }

    @Test
    public void testSetInstId() throws Exception {
        Long instId1 = new Long(3);
        readNewsArticlesEvent.setInstId(instId);
        assertEquals("instId does not match", instId1, readNewsArticlesEvent.getInstId());
    }

    @Test
    public void testGetSyId() throws Exception {
        assertEquals("syId does not match", syId, readNewsArticlesEvent.getSyId());
    }

    @Test
    public void testSetSyId() throws Exception {
        Long syId1 = new Long(4);
        readNewsArticlesEvent.setSyId(syId1);
        assertEquals("syId does not match", syId1, readNewsArticlesEvent.getSyId());
    }
}