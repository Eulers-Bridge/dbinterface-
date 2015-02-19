/**
 * 
 */
package com.eulersbridge.iEngage.core.services;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

import com.eulersbridge.iEngage.core.events.DeletedEvent;
import com.eulersbridge.iEngage.core.events.LikeEvent;
import com.eulersbridge.iEngage.core.events.LikedEvent;
import com.eulersbridge.iEngage.core.events.ReadAllEvent;
import com.eulersbridge.iEngage.core.events.ReadEvent;
import com.eulersbridge.iEngage.core.events.UpdatedEvent;
import com.eulersbridge.iEngage.core.events.events.CreateEventEvent;
import com.eulersbridge.iEngage.core.events.events.DeleteEventEvent;
import com.eulersbridge.iEngage.core.events.events.EventCreatedEvent;
import com.eulersbridge.iEngage.core.events.events.EventDetails;
import com.eulersbridge.iEngage.core.events.events.EventsReadEvent;
import com.eulersbridge.iEngage.core.events.events.ReadEventEvent;
import com.eulersbridge.iEngage.core.events.events.RequestReadEventEvent;
import com.eulersbridge.iEngage.core.events.events.UpdateEventEvent;
import com.eulersbridge.iEngage.database.domain.Event;
import com.eulersbridge.iEngage.database.domain.Institution;
import com.eulersbridge.iEngage.database.domain.Like;
import com.eulersbridge.iEngage.database.domain.NewsFeed;
import com.eulersbridge.iEngage.database.domain.User;
import com.eulersbridge.iEngage.database.domain.Fixture.DatabaseDataFixture;
import com.eulersbridge.iEngage.database.repository.EventRepository;
import com.eulersbridge.iEngage.database.repository.InstitutionRepository;

/**
 * @author Greg Newitt
 *
 */
public class EventEventHandlerTest
{
    private static Logger LOG = LoggerFactory.getLogger(EventEventHandlerTest.class);

    @Mock
	EventRepository eventRepository;
    @Mock
	InstitutionRepository institutionRepository;

    EventEventHandler service;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		service=new EventEventHandler(eventRepository,institutionRepository);
	}

	/**
	 * Test method for {@link com.eulersbridge.iEngage.core.services.EventEventHandler#EventEventHandler(com.eulersbridge.iEngage.database.repository.EventRepository)}.
	 */
	@Test
	public final void testEventEventHandler()
	{
		assertNotNull("Not yet implemented",service);
	}

	/**
	 * Test method for {@link com.eulersbridge.iEngage.core.services.EventEventHandler#createEvent(com.eulersbridge.iEngage.core.events.events.CreateEventEvent)}.
	 */
	@Test
	public final void testCreateEvent()
	{
		if (LOG.isDebugEnabled()) LOG.debug("CreatingEvent()");
		Event testData=DatabaseDataFixture.populateEvent1();
		Institution testInst=DatabaseDataFixture.populateInstUniMelb();
		NewsFeed testNf=DatabaseDataFixture.populateNewsFeed1();
		when(institutionRepository.findOne(any(Long.class))).thenReturn(testInst);
		when(institutionRepository.findNewsFeed(any(Long.class))).thenReturn(testNf);
		when(eventRepository.save(any(Event.class))).thenReturn(testData);
		EventDetails dets=testData.toEventDetails();
		CreateEventEvent createEventEvent=new CreateEventEvent(dets);
		EventCreatedEvent evtData = service.createEvent(createEventEvent);
		EventDetails returnedDets = (EventDetails)evtData.getDetails();
		assertEquals(returnedDets,testData.toEventDetails());
		assertEquals(evtData.getEventId(),returnedDets.getEventId());
		assertNotNull(evtData.getDetails());
	}

	@Test
	public final void testCreateEventInstNotFound() 
	{
		if (LOG.isDebugEnabled()) LOG.debug("CreatingEvent()");
		Event testData=DatabaseDataFixture.populateEvent1();
		Institution testInst=null;
		when(institutionRepository.findOne(any(Long.class))).thenReturn(testInst);
		when(eventRepository.save(any(Event.class))).thenReturn(testData);
		EventDetails dets=testData.toEventDetails();
		CreateEventEvent createEventEvent=new CreateEventEvent(dets);
		EventCreatedEvent evtData = service.createEvent(createEventEvent);
		assertFalse(evtData.isInstitutionFound());
		assertEquals(evtData.getEventId(),testData.getNewsFeed().getInstitution().getNodeId());
		assertNull(evtData.getDetails());
	}

	/**
	 * Test method for {@link com.eulersbridge.iEngage.core.services.EventEventHandler#requestReadEvent(com.eulersbridge.iEngage.core.events.events.RequestReadEventEvent)}.
	 */
	@Test
	public final void testReadEvent()
	{
		if (LOG.isDebugEnabled()) LOG.debug("ReadingEvent()");
		Event testData=DatabaseDataFixture.populateEvent1();
		when(eventRepository.findOne(any(Long.class))).thenReturn(testData);
		RequestReadEventEvent readElectionEvent=new RequestReadEventEvent(testData.getEventId());
		ReadEventEvent evtData = (ReadEventEvent) service.readEvent(readElectionEvent);
		EventDetails returnedDets = (EventDetails)evtData.getDetails();
		assertEquals(returnedDets,testData.toEventDetails());
		assertEquals(evtData.getNodeId(),returnedDets.getEventId());
		assertTrue(evtData.isEntityFound());
	}

	@Test
	public final void testReadEventNotFound() 
	{
		if (LOG.isDebugEnabled()) LOG.debug("ReadingEvent()");
		Event testData=null;
		Long nodeId=1l;
		when(eventRepository.findOne(any(Long.class))).thenReturn(testData);
		RequestReadEventEvent readElectionEvent=new RequestReadEventEvent(nodeId);
		ReadEvent evtData = service.readEvent(readElectionEvent);
		assertNull(evtData.getDetails());
		assertEquals(nodeId,evtData.getNodeId());
		assertFalse(evtData.isEntityFound());
	}

	/**
	 * Test method for {@link com.eulersbridge.iEngage.core.services.EventEventHandler#updateEvent(com.eulersbridge.iEngage.core.events.events.UpdateEventEvent)}.
	 */
	@Test
	public final void testUpdateEvent()
	{
		if (LOG.isDebugEnabled()) LOG.debug("UpdatingEvent()");
		Event testData=DatabaseDataFixture.populateEvent1();
		when(eventRepository.findOne(any(Long.class))).thenReturn(testData);
		when(eventRepository.save(any(Event.class))).thenReturn(testData);
		EventDetails dets=testData.toEventDetails();
		UpdateEventEvent createEventEvent=new UpdateEventEvent(dets.getEventId(), dets);
		UpdatedEvent evtData = service.updateEvent(createEventEvent);
		EventDetails returnedDets = (EventDetails) evtData.getDetails();
		assertEquals(returnedDets,testData.toEventDetails());
		assertEquals(evtData.getNodeId(),returnedDets.getEventId());
		assertTrue(evtData.isEntityFound());
		assertNotNull(evtData.getNodeId());
	}
	@Test
	public final void testUpdateEventNotFound() 
	{
		if (LOG.isDebugEnabled()) LOG.debug("UpdatingEvent()");
		Event testData=DatabaseDataFixture.populateEvent1();
		when(eventRepository.findOne(any(Long.class))).thenReturn(null);
		when(eventRepository.save(any(Event.class))).thenReturn(testData);
		EventDetails dets=testData.toEventDetails();
		UpdateEventEvent createEventEvent=new UpdateEventEvent(dets.getEventId(), dets);
		UpdatedEvent evtData = service.updateEvent(createEventEvent);
		assertNull(evtData.getDetails());
		assertEquals(evtData.getNodeId(),testData.getEventId());
		assertFalse(evtData.isEntityFound());
		assertNotNull(evtData.getNodeId());
	}

	/**
	 * Test method for {@link com.eulersbridge.iEngage.core.services.EventEventHandler#deleteEvent(com.eulersbridge.iEngage.core.events.events.DeleteEventEvent)}.
	 */
	@Test
	public final void testDeleteEvent()
	{
		if (LOG.isDebugEnabled()) LOG.debug("DeletingEvent()");
		Event testData=DatabaseDataFixture.populateEvent1();
		when(eventRepository.findOne(any(Long.class))).thenReturn(testData);
		doNothing().when(eventRepository).delete((any(Long.class)));
		DeleteEventEvent deleteEventEvent=new DeleteEventEvent(testData.getEventId());
		DeletedEvent evtData = service.deleteEvent(deleteEventEvent);
		assertTrue(evtData.isEntityFound());
		assertTrue(evtData.isDeletionCompleted());
		assertEquals(testData.getEventId(),evtData.getNodeId());
	}
	@Test
	public final void testDeleteElectionNotFound() 
	{
		if (LOG.isDebugEnabled()) LOG.debug("DeletingEvent()");
		Event testData=DatabaseDataFixture.populateEvent1();
		when(eventRepository.findOne(any(Long.class))).thenReturn(null);
		doNothing().when(eventRepository).delete((any(Long.class)));
		DeleteEventEvent deleteEventEvent=new DeleteEventEvent(testData.getEventId());
		DeletedEvent evtData = service.deleteEvent(deleteEventEvent);
		assertFalse(evtData.isEntityFound());
		assertFalse(evtData.isDeletionCompleted());
		assertEquals(testData.getEventId(),evtData.getNodeId());
	}

	/**
	 * Test method for {@link com.eulersbridge.iEngage.core.services.EventEventHandler#readEvents(com.eulersbridge.iEngage.core.events.events.ReadAllEvent,Direction,int,int)}.
	 */
	@Test
	public final void testReadEvents()
	{
		if (LOG.isDebugEnabled()) LOG.debug("ReadingEvents()");
		HashMap<Long, Event> events = DatabaseDataFixture.populateEvents();
		ArrayList<Event> evts=new ArrayList<Event>();
		Iterator<Event> iter=events.values().iterator();
		while (iter.hasNext())
		{
			Event na=iter.next();
			evts.add(na);
		}

		
		Long institutionId=1l;
		ReadAllEvent evt=new ReadAllEvent(institutionId);
		int pageLength=10;
		int pageNumber=0;
		
		Pageable pageable=new PageRequest(pageNumber,pageLength,Direction.ASC,"a.date");
		Page<Event> testData=new PageImpl<Event>(evts,pageable,evts.size());
		when(eventRepository.findByInstitutionId(any(Long.class),any(Pageable.class))).thenReturn(testData);

		EventsReadEvent evtData = service.readEvents(evt, Direction.ASC, pageNumber, pageLength);
		assertNotNull(evtData);
		assertEquals(evtData.getTotalPages(),new Integer(1));
		assertEquals(evtData.getTotalItems(),new Long(evts.size()));
	}

	@Test
	public final void testReadEventsNoneAvailable()
	{
		if (LOG.isDebugEnabled()) LOG.debug("ReadingEvents()");
		ArrayList<Event> evts=new ArrayList<Event>();
		
		Long institutionId=1l;
		ReadAllEvent evt=new ReadAllEvent(institutionId);
		int pageLength=10;
		int pageNumber=0;
		
		Pageable pageable=new PageRequest(pageNumber,pageLength,Direction.ASC,"a.date");
		Page<Event> testData=new PageImpl<Event>(evts,pageable,evts.size());
		when(eventRepository.findByInstitutionId(any(Long.class),any(Pageable.class))).thenReturn(testData);
		Institution inst=DatabaseDataFixture.populateInstUniMelb();
		when(institutionRepository.findOne(any(Long.class))).thenReturn(inst);
				
		EventsReadEvent evtData = service.readEvents(evt, Direction.ASC, pageNumber, pageLength);
		assertNotNull(evtData);
		assertEquals(evtData.getTotalPages().intValue(),0);
		assertEquals(evtData.getTotalItems().longValue(),0);
	}

	@Test
	public final void testReadEventsNoValidInst()
	{
		if (LOG.isDebugEnabled()) LOG.debug("ReadingEvents()");
		ArrayList<Event> evts=new ArrayList<Event>();
		
		Long institutionId=1l;
		ReadAllEvent evt=new ReadAllEvent(institutionId);
		int pageLength=10;
		int pageNumber=0;
		
		Pageable pageable=new PageRequest(pageNumber,pageLength,Direction.ASC,"a.date");
		Page<Event> testData=new PageImpl<Event>(evts,pageable,evts.size());
		when(eventRepository.findByInstitutionId(any(Long.class),any(Pageable.class))).thenReturn(testData);
		when(institutionRepository.findOne(any(Long.class))).thenReturn(null);
				
		EventsReadEvent evtData = service.readEvents(evt, Direction.ASC, pageNumber, pageLength);
		assertNotNull(evtData);
		assertFalse(evtData.isInstitutionFound());
		assertEquals(evtData.getTotalPages(),null);
		assertEquals(evtData.getTotalItems(),null);
	}

	@Test
	public final void testReadEventsNullReturned()
	{
		if (LOG.isDebugEnabled()) LOG.debug("ReadingEvents()");
		
		Long institutionId=1l;
		ReadAllEvent evt=new ReadAllEvent(institutionId);
		
		Page<Event> testData=null;
		when(eventRepository.findByInstitutionId(any(Long.class),any(Pageable.class))).thenReturn(testData);

		int pageLength=10;
		int pageNumber=0;
		EventsReadEvent evtData = service.readEvents(evt, Direction.ASC, pageNumber, pageLength);
		assertNotNull(evtData);
		assertFalse(evtData.isInstitutionFound());
	}
}
