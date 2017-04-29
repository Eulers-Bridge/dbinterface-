package com.eulersbridge.iEngage.database.domain;

import com.eulersbridge.iEngage.core.events.newsArticles.NewsArticleDetails;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotNull;
import java.util.Calendar;
import java.util.List;

@NodeEntity
public class NewsArticle extends Likeable {
  private String title;
  private String content;
  @Relationship(type = DatabaseDomainConstants.HAS_PHOTO_LABEL)
//  @Fetch
  private List<Node> photos;
  @Index
  @NotNull
  private Long date;
  @Relationship(type = DatabaseDomainConstants.CREATED_BY_LABEL, direction = Relationship.OUTGOING)
  private Node creator;
  @Relationship(type = DatabaseDomainConstants.HAS_NEWS_LABEL, direction = Relationship.INCOMING)
  private Node newsFeed;
  private boolean inappropriateContent;

//  @Query("START n = node({self}) match (n)-[r:" + DatabaseDomainConstants.CREATED_BY_LABEL + "]-(c) RETURN c.email ")
//  private String creatorEmail;
//
//  @Query("START n=node({self}) " +
//    "match (n)-[r:" + DatabaseDomainConstants.HAS_NEWS_LABEL + "]" +
//    "-(f)-[r2:" + DatabaseDomainConstants.HAS_NEWS_FEED_LABEL + "]" +
//    "-(i:" + DatabaseDomainConstants.INSTITUTION + ") RETURN id(i)")
//  private Long InstitutionID;

  private static Logger LOG = LoggerFactory.getLogger(NewsArticle.class);

  public NewsArticle() {
    if (LOG.isDebugEnabled()) LOG.debug("Constructor");
  }

  public NewsArticle(String title, String content, Calendar date, User creator) {
    if (LOG.isTraceEnabled())
      LOG.trace("Constructor(" + title + ',' + content + ',' + photos + ',' + date.toString() + ',' + creator + ')');
    this.title = title;
    this.content = content;
    this.date = date.getTimeInMillis();
    this.creator = creator;
  }

  public String getTitle() {
    if (LOG.isDebugEnabled()) LOG.debug("getTitle() = " + title);
    return title;
  }

  /**
   * @param title the title to set
   */
  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    if (LOG.isDebugEnabled()) LOG.debug("getContent() = " + content);
    return content;
  }

  /**
   * @param content the content to set
   */
  public void setContent(String content) {
    this.content = content;
  }

  public Iterable<Photo> getPhotos() {
    if (LOG.isDebugEnabled()) LOG.debug("getPhotos() = " + photos);
    return castList(photos, Photo.class);
  }

  public void setPhotos(List<Node> picture) {
    this.photos = picture;

  }

  public Long getDate() {
    if (LOG.isDebugEnabled()) LOG.debug("getDate() = " + date.toString());
    return date;
  }

  /**
   * @param date the date to set
   */
  public void setDate(Long date) {
    this.date = date;
  }

  public User getCreator() {
    if (LOG.isDebugEnabled()) LOG.debug("getCreator() = " + creator);
    return (User) creator;
  }

  public void setCreator(Node creator) {
    this.creator = creator;
  }

  /**
   * @return the studentYear
   */
  public NewsFeed getNewsFeed() {
    return (NewsFeed) newsFeed;
  }

  /**
   * @param newsFeed the studentYear to set
   */
  public void setNewsFeed(Node newsFeed) {
    this.newsFeed = newsFeed;
  }

  /**
   * @return the inappropriateContent
   */
  public boolean isInappropriateContent() {
    return inappropriateContent;
  }

  /**
   * @param inappropriateContent the inappropriateContent to set
   */
  public void setInappropriateContent(boolean inappropriateContent) {
    this.inappropriateContent = inappropriateContent;
  }

  public String toString() {
    String buff = "[ nodeId = " + getNodeId() +
      ", title = " +
      getTitle() +
      ", content = " +
      getContent() +
      ", photos = " +
      getPhotos() +
      ", date = " +
      getDate() +
      ", creator = " +
      getCreator() +
      ", studentYear = " +
      getNewsFeed() +
      ", pictures = " +
      getPhotos() +
      " ]";
    String retValue;
    retValue = buff;
    if (LOG.isDebugEnabled()) LOG.debug("toString() = " + retValue);
    return retValue;
  }

  public NewsArticleDetails toNewsArticleDetails() {
    if (LOG.isTraceEnabled()) LOG.trace("toNewsArtDetails()");

    NewsArticleDetails details = new NewsArticleDetails();
    details.setNewsArticleId(getNodeId());
    if (LOG.isTraceEnabled()) LOG.trace("newsArticle " + this);

    BeanUtils.copyProperties(this, details);
    if (getCreator() != null) details.setCreatorEmail(getCreator().getEmail());
    if (getNewsFeed() != null) {
      if (getNewsFeed().getInstitution().getNodeId() != null)
        details.setInstitutionId(getNewsFeed().getInstitution().getNodeId());
    }
    if (getNumOfLikes() != null)
      details.setLikes(0);
    else details.setLikes(getNumOfLikes().intValue());
    details.setPhotos(Photo.photosToPhotoDetails(getPhotos()));

    if (LOG.isTraceEnabled()) LOG.trace("newsArticleDetails " + details);

    return details;
  }

  public static NewsArticle fromNewsArticleDetails(NewsArticleDetails newsArtDetails) {
    if (LOG.isTraceEnabled()) LOG.trace("fromNewsArticleDetails()");

    NewsArticle newsArt = new NewsArticle();
    if (LOG.isTraceEnabled()) LOG.trace("newsArtDetails " + newsArtDetails);
    newsArt.nodeId = newsArtDetails.getNewsArticleId();
    newsArt.title = newsArtDetails.getTitle();
    newsArt.content = newsArtDetails.getContent();
    newsArt.date = newsArtDetails.getDate();
    User creator = new User(newsArtDetails.getCreatorEmail(), null, null, null, null, null, null, null);
    newsArt.creator = creator;
    NewsFeed nf = new NewsFeed();
    newsArt.newsFeed = nf;
    newsArt.setInappropriateContent(newsArtDetails.isInappropriateContent());
    if (LOG.isTraceEnabled()) LOG.trace("newsArt " + newsArt);

    return newsArt;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    if (nodeId != null) {
      result = prime * result + nodeId.hashCode();
    } else {
      result = prime * result + ((content == null) ? 0 : content.hashCode());
      result = prime * result + ((creator == null) ? 0 : creator.hashCode());
      result = prime * result + ((date == null) ? 0 : date.hashCode());
      result = prime * result
        + ((newsFeed == null) ? 0 : newsFeed.hashCode());
      result = prime * result + ((photos == null) ? 0 : photos.hashCode());
      result = prime * result + ((title == null) ? 0 : title.hashCode());
    }
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    NewsArticle other = (NewsArticle) obj;
    if (nodeId != null) {
      return nodeId.equals(other.nodeId);
    } else {
      if (other.nodeId != null)
        return false;
      if (content == null) {
        if (other.content != null)
          return false;
      } else if (!content.equals(other.content))
        return false;
      if (creator == null) {
        if (other.creator != null)
          return false;
      } else if (!creator.equals(other.creator))
        return false;
      if (date == null) {
        if (other.date != null)
          return false;
      } else if (!date.equals(other.date))
        return false;
      if (newsFeed == null) {
        if (other.newsFeed != null)
          return false;
      } else if (!newsFeed.equals(other.newsFeed))
        return false;
      if (photos == null) {
        if (other.photos != null)
          return false;
      } else if (!photos.equals(other.photos))
        return false;
      if (title == null) {
        if (other.title != null)
          return false;
      } else if (!title.equals(other.title))
        return false;
    }
    return true;
  }
}
