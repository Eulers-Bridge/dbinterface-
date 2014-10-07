package com.eulersbridge.iEngage.database.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;

import com.eulersbridge.iEngage.database.domain.Like;
import com.eulersbridge.iEngage.database.domain.NewsArticle;
import com.eulersbridge.iEngage.database.domain.NewsFeed;
import com.eulersbridge.iEngage.database.domain.User;

public interface NewsArticleRepository extends GraphRepository<NewsArticle> 
{
	static Logger LOG = LoggerFactory.getLogger(NewsArticleRepository.class);
	   
	Page<NewsArticle> findByStudentYear(Pageable p,NewsFeed sy);
	Iterable<NewsArticle> findByCreator(User creator);
	@Query("Match (n:`StudentYear`)-[r]-(a:NewsArticle) where id(n)={syId} return a")
	Page<NewsArticle> findByStudentYearId(@Param("syId")Long syId,Pageable p);
	@Query("Match (a:`User`),(b) where a.email={email} and id(b)={likedId} CREATE UNIQUE a-[r:LIKES]-b SET r.timestamp=coalesce(r.timestamp,timestamp()),r.__type__='Like' return r")
	Like likeArticle(@Param("email")String email,@Param("likedId")Long likedId);
	@Query("Match (a:`User`)-[r:LIKES]-(b) where a.email={email} and id(b)={likedId} delete r")
	void unlikeArticle(@Param("email")String email,@Param("likedId")Long likedId);
}
