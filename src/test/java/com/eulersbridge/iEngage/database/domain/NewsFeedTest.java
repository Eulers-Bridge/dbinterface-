/**
 * 
 */
package com.eulersbridge.iEngage.database.domain;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eulersbridge.iEngage.core.events.newsFeed.NewsFeedDetails;
import com.eulersbridge.iEngage.database.domain.Fixture.DatabaseDataFixture;

/**
 * @author Greg Newitt
 *
 */
public class NewsFeedTest 
{
	NewsFeed nf;
	
    private static Logger LOG = LoggerFactory.getLogger(NewsFeedTest.class);
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception 
	{
		nf=DatabaseDataFixture.populateNewsFeed2();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.eulersbridge.iEngage.database.domain.NewsFeed#toDetails()}.
	 */
	@Test
	public void testToDetails() 
	{
		NewsFeedDetails dets = nf.toDetails();
		if (LOG.isDebugEnabled()) LOG.debug("dets node id "+dets.getNodeId()+" nf inst id "+nf.getNodeId());
		assertEquals("NodeIds don't match",dets.getNodeId(),nf.getNodeId());
		if (LOG.isDebugEnabled()) LOG.debug("dets inst id "+dets.getInstitutionId()+" nf inst id "+nf.getInstitution().getNodeId());
		assertEquals("InstitutionIds don't match",dets.getInstitutionId(),nf.getInstitution().getNodeId());
	}

	/**
	 * Test method for {@link com.eulersbridge.iEngage.database.domain.NewsFeed#fromDetails(com.eulersbridge.iEngage.core.events.newsFeed.NewsFeedDetails)}.
	 */
	@Test
	public void testFromDetails() 
	{
		NewsFeedDetails dets = nf.toDetails();
		NewsFeed nf2=NewsFeed.fromDetails(dets);
		assertEquals("NodeIds don't match",dets.getNodeId(),nf2.getNodeId());
		if (LOG.isDebugEnabled()) LOG.debug("dets inst id "+dets.getInstitutionId()+" nf inst id "+nf.getInstitution().getNodeId());
		assertEquals("InstitutionIds don't match",dets.getInstitutionId(),nf2.getInstitution().getNodeId());
	}

	/**
	 * Test method for {@link com.eulersbridge.iEngage.database.domain.NewsFeed#getNodeId()}.
	 */
	@Test
	public void testGetNodeId() 
	{
		assertEquals("",nf.getNodeId(),new Long(1));
	}

	/**
	 * Test method for {@link com.eulersbridge.iEngage.database.domain.NewsFeed#setNodeId(java.lang.Long)}.
	 */
	@Test
	public void testSetNodeId() 
	{
		nf.setNodeId(new Long(3));
		assertEquals("",nf.getNodeId(),new Long(3));
		nf.setNodeId(new Long(1));
		assertEquals("",nf.getNodeId(),new Long(1));
	}

	/**
	 * Test method for {@link com.eulersbridge.iEngage.database.domain.NewsFeed#getInstitution()}.
	 */
	@Test
	public void testGetInstitution() 
	{
		Institution inst=nf.getInstitution(),ddfinst=DatabaseDataFixture.populateNewsFeed2().getInstitution();
		assertEquals(inst.getName(),ddfinst.getName());
		assertEquals(inst.getCampus(),ddfinst.getCampus());
		assertEquals(inst.getState(),ddfinst.getState());
		assertEquals(inst.getCountry().getCountryName(),ddfinst.getCountry().getCountryName());
	}

	/**
	 * Test method for {@link com.eulersbridge.iEngage.database.domain.NewsFeed#setInstitution(com.eulersbridge.iEngage.database.domain.Institution)}.
	 */
	@Test
	public void testSetInstitution() 
	{
		Institution inst=nf.getInstitution();
		String name="UCLA",campus="LA",state="California";
		Institution inst2=DatabaseDataFixture.populateInst(name,campus,state , DatabaseDataFixture.populateCountryAust());
		nf.setInstitution(inst2);
		assertEquals("",inst2.getName(),name);
		assertEquals("",inst2.getCampus(),campus);
		assertEquals("",inst2.getState(),state);
		assertEquals("",inst2.getCountry().getCountryName(),"Australia");
		nf.setInstitution(inst);
	}

	/**
	 * Test method for {@link com.eulersbridge.iEngage.database.domain.NewsFeed#getNews()}.
	 */
	@Test
	public void testGetNews() 
	{
		Set<NewsArticle> news;
		news=nf.getNews();
		assertNull(news);
	}

	/**
	 * Test method for {@link com.eulersbridge.iEngage.database.domain.NewsFeed#setNews(java.util.Set)}.
	 */
	@Test
	public void testSetNews() 
	{
		Set<NewsArticle> news=nf.getNews();
		nf.setNews(null);
		assertNull(nf.getNews());
		nf.setNews(news);
		assertEquals(nf.getNews(),news);
	}

	/**
	 * Test method for {@link com.eulersbridge.iEngage.database.domain.NewsFeed#toString()}.
	 */
	@Test
	public void testToString() 
	{
		assertNotNull(nf.toString());
	}

}
