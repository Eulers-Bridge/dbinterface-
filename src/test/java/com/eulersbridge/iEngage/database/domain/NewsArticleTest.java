/**
 * 
 */
package com.eulersbridge.iEngage.database.domain;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.eulersbridge.iEngage.core.events.newsArticles.NewsArticleDetails;
import com.eulersbridge.iEngage.database.domain.Fixture.DatabaseDataFixture;

/**
 * @author Greg Newitt
 *
 */
public class NewsArticleTest 
{
	NewsArticle news;
	final String title="The title";
	final String content="The content.";
	final Calendar date=Calendar.getInstance();
	final User creator=DatabaseDataFixture.populateUserGnewitt();
	final NewsFeed year=DatabaseDataFixture.populateNewsFeed2();
	final Long node1=new Long(1);
	final Long node2=new Long(2);
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
		Iterable<String> picture=null;
		news=new NewsArticle(title, content, picture, date, creator);
		news.setNodeId(node1);
		news.setNewsFeed(year);
		Set<Like> likes1=new HashSet<Like>();
		news.setLikes(likes1);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.eulersbridge.iEngage.database.domain.NewsArticle#NewsArticle()}.
	 */
	@Test
	public void testNewsArticle() 
	{
		NewsArticle news2=new NewsArticle();
		assertNotNull("Empty constructor did not create object.",news2);
	}

	/**
	 * Test method for {@link com.eulersbridge.iEngage.database.domain.NewsArticle#NewsArticle(java.lang.String, java.lang.String, java.lang.Iterable, java.util.Calendar, com.eulersbridge.iEngage.database.domain.User)}.
	 */
	@Test
	public void testNewsArticleStringStringIterableOfStringCalendarUser() 
	{
		Iterable<String> picture=null;
		NewsArticle news2=new NewsArticle(title, content, picture, date, creator);
		assertNotNull("Loaded Constructor did not create object.",news2);
	}

	/**
	 * Test method for {@link com.eulersbridge.iEngage.database.domain.NewsArticle#getNodeId()}.
	 */
	@Test
	public void testGetNodeId() 
	{
		assertEquals("node ids don't match.",node1,news.getNodeId());
	}

	/**
	 * Test method for {@link com.eulersbridge.iEngage.database.domain.NewsArticle#setNodeId(java.lang.Long)}.
	 */
	@Test
	public void testSetNodeId() 
	{
		news.setNodeId(node2);
		assertEquals("node ids don't match.",node2,news.getNodeId());
	}

	/**
	 * Test method for {@link com.eulersbridge.iEngage.database.domain.NewsArticle#getTitle()}.
	 */
	@Test
	public void testGetTitle() 
	{
		assertEquals("Titles don't match",title,news.getTitle());
	}

	/**
	 * Test method for {@link com.eulersbridge.iEngage.database.domain.NewsArticle#getContent()}.
	 */
	@Test
	public void testGetContent() 
	{
		assertEquals("Content doesn't match.",content,news.getContent());
	}

	/**
	 * Test method for {@link com.eulersbridge.iEngage.database.domain.NewsArticle#getPicture()}.
	 */
	@Test
	public void testGetPicture() 
	{
		assertNull("pictures don't match.",news.getPicture());
	}

	/**
	 * Test method for {@link com.eulersbridge.iEngage.database.domain.NewsArticle#getLikers()}.
	 */
	@Test
	public void testGetLikes() 
	{
		assertEquals("No likers at this point.",0,news.getLikes().size());
	}

	/**
	 * Test method for {@link com.eulersbridge.iEngage.database.domain.NewsArticle#getDate()}.
	 */
	@Test
	public void testGetDate() 
	{
		assertEquals("Dates don't match.",new Long(date.getTimeInMillis()),news.getDate());
	}

	/**
	 * Test method for {@link com.eulersbridge.iEngage.database.domain.NewsArticle#getCreator()}.
	 */
	@Test
	public void testGetCreator() 
	{
		assertEquals("",news.getCreator(),creator);
	}

	/**
	 * Test method for {@link com.eulersbridge.iEngage.database.domain.NewsArticle#setCreator(com.eulersbridge.iEngage.database.domain.User)}.
	 */
	@Test
	public void testSetCreator() 
	{
		User creator2=DatabaseDataFixture.populateUserGnewitt2();
		news.setCreator(creator2);
		assertNotEquals("",news.getCreator(),creator);
		assertEquals("",news.getCreator(),creator2);
	}

	/**
	 * Test method for {@link com.eulersbridge.iEngage.database.domain.NewsArticle#toNewsArticleDetails()}.
	 */
	@Test
	public void testToNewsArticleDetails() 
	{
		NewsArticleDetails dets = news.toNewsArticleDetails();
		assertEquals("Titles don't match.",dets.getTitle(),news.getTitle());
		assertEquals("Contents don't match.",dets.getContent(),news.getContent());
		assertEquals("Dates don't match.",dets.getDate(),news.getDate());
		assertEquals("Creator emails don't match.",dets.getCreatorEmail(),news.getCreator().getEmail());
		assertEquals("IDs don't match.",dets.getNewsArticleId(),news.getNodeId());
		assertEquals("Pictures don't match.",dets.getPicture().size(),0);
		assertEquals("Likes don't match.",dets.getLikes().intValue(),news.getLikes().size());
	}

	/**
	 * Test method for {@link com.eulersbridge.iEngage.database.domain.NewsArticle#fromNewsArticleDetails(com.eulersbridge.iEngage.core.events.newsArticles.NewsArticleDetails)}.
	 */
	@Test
	public void testFromNewsArticleDetails() 
	{
		NewsArticleDetails dets = news.toNewsArticleDetails();
		NewsArticle news2 = NewsArticle.fromNewsArticleDetails(dets);
		assertEquals("Titles don't match.",news2.getTitle(),news.getTitle());
		assertEquals("Contents don't match.",news2.getContent(),news.getContent());
		assertEquals("Dates don't match.",news2.getDate(),news.getDate());
		assertEquals("Creator emails don't match.",news2.getCreator().getEmail(),news.getCreator().getEmail());
		assertEquals("IDs don't match.",news2.getNodeId(),news.getNodeId());
		assertEquals("Pictures don't match.",news2.getPicture(),dets.getPicture());
		assertNull("Likers don't match.",news2.getLikes());
	}

	/**
	 * Test method for {@link com.eulersbridge.iEngage.database.domain.NewsArticle#equals(com.eulersbridge.iEngage.database.domain.NewsArticle)}.
	 */
	@Test
	public void testEqualsNewsArticle() 
	{
		NewsArticleDetails dets = news.toNewsArticleDetails();
		NewsArticle news2 = NewsArticle.fromNewsArticleDetails(dets);
		assertEquals("Objects with same nodeId should match.",news,news2);
	}

}
