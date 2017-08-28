package com.eulersbridge.iEngage.rest.controller;

import com.eulersbridge.iEngage.core.events.*;
import com.eulersbridge.iEngage.core.events.likes.LikeableObjectLikesEvent;
import com.eulersbridge.iEngage.core.events.likes.LikesLikeableObjectEvent;
import com.eulersbridge.iEngage.core.events.polls.*;
import com.eulersbridge.iEngage.core.services.interfacePack.LikesService;
import com.eulersbridge.iEngage.core.services.interfacePack.PollService;
import com.eulersbridge.iEngage.core.services.interfacePack.UserService;
import com.eulersbridge.iEngage.rest.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;

/**
 * @author Yikai Gong
 */

@RestController
@RequestMapping(ControllerConstants.API_PREFIX)
public class PollController {

  @Autowired
  PollService pollService;

  @Autowired
  UserService userService;

  @Autowired
  LikesService likesService;

  public PollController() {
  }

  private static Logger LOG = LoggerFactory.getLogger(PollController.class);

  // Get
  @RequestMapping(method = RequestMethod.GET, value = ControllerConstants.POLL_LABEL
    + "/{pollId}")
  public @ResponseBody
  ResponseEntity<Poll> findPoll(@PathVariable Long pollId) {
    if (LOG.isInfoEnabled())
      LOG.info(pollId + " attempting to get poll. ");
    RequestReadPollEvent requestReadPollEvent = new RequestReadPollEvent(
      pollId);
    ReadEvent readPollEvent = pollService
      .requestReadPoll(requestReadPollEvent);
    if (readPollEvent.isEntityFound()) {
      Poll poll = Poll.fromPollDetails((PollDetails) readPollEvent
        .getDetails());
      return new ResponseEntity<Poll>(poll, HttpStatus.OK);
    } else {
      return new ResponseEntity<Poll>(HttpStatus.NOT_FOUND);
    }
  }

  // Get
  @RequestMapping(method = RequestMethod.GET, value = ControllerConstants.POLL_LABEL
    + "/{pollId}/results")
  public @ResponseBody
  ResponseEntity<PollResultDetails> getPollResults(@PathVariable Long pollId) {
    if (LOG.isInfoEnabled())
      LOG.info(pollId + " attempting to get poll results. ");
    ReadPollResultEvent readPollResultEvent = new ReadPollResultEvent(pollId);
    ReadEvent pollResultReadEvent = pollService
      .readPollResult(readPollResultEvent);
    if (pollResultReadEvent.isEntityFound()) {
      PollResultDetails poll = ((PollResultDetails) pollResultReadEvent.getDetails());
      return new ResponseEntity<PollResultDetails>(poll, HttpStatus.OK);
    } else {
      return new ResponseEntity<PollResultDetails>(HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Is passed all the necessary data to read polls from the database. The
   * request must be a GET with the ownerId presented as the final portion of
   * the URL.
   * <p/>
   * This method will return the polls read from the database.
   *
   * @param ownerId the ownerId of the poll objects to be read.
   * @return the polls.
   */
  @RequestMapping(method = RequestMethod.GET, value = ControllerConstants.POLLS_LABEL
    + "/{ownerId}")
  public @ResponseBody
  ResponseEntity<Polls> findPolls(
    @PathVariable(value = "") Long ownerId,
    @RequestParam(value = "direction", required = false, defaultValue = ControllerConstants.DIRECTION) String direction,
    @RequestParam(value = "page", required = false, defaultValue = ControllerConstants.PAGE_NUMBER) String page,
    @RequestParam(value = "pageSize", required = false, defaultValue = ControllerConstants.PAGE_LENGTH) String pageSize) {
    int pageNumber = 0;
    int pageLength = 10;
    pageNumber = Integer.parseInt(page);
    pageLength = Integer.parseInt(pageSize);
    if (LOG.isInfoEnabled())
      LOG.info("Attempting to retrieve photoAlbums for owner " + ownerId + '.');

    Direction sortDirection = Direction.DESC;
    if (direction.equalsIgnoreCase("asc")) sortDirection = Direction.ASC;

    ResponseEntity<Polls> response;

    AllReadEvent pollEvent = pollService.findPolls(
      new ReadAllEvent(ownerId), sortDirection, pageNumber,
      pageLength);

    if (!pollEvent.isEntityFound()) {
      response = new ResponseEntity<Polls>(HttpStatus.NOT_FOUND);
    } else {
      Iterator<Poll> pollIter = Poll.toPollsIterator(pollEvent.getDetails().iterator());
      Polls polls = Polls.fromPollsIterator(pollIter,
        pollEvent.getTotalItems(), pollEvent.getTotalPages());
      response = new ResponseEntity<Polls>(polls, HttpStatus.OK);
    }
    return response;
  }

  // Create
  @RequestMapping(method = RequestMethod.POST, value = ControllerConstants.POLL_LABEL)
  public @ResponseBody
  ResponseEntity<Poll> createPoll(@RequestBody Poll poll) {
    if (LOG.isInfoEnabled()) LOG.info("attempting to create poll " + poll);
    CreatePollEvent createPollEvent = new CreatePollEvent(
      poll.toPollDetails());
    PollCreatedEvent pollCreatedEvent = pollService
      .createPoll(createPollEvent);
    ResponseEntity<Poll> response;
    if (null == pollCreatedEvent) {
      response = new ResponseEntity<Poll>(HttpStatus.BAD_REQUEST);
    } else if (!(pollCreatedEvent.isOwnerFound())) {
      response = new ResponseEntity<Poll>(HttpStatus.NOT_FOUND);
    } else if (!(pollCreatedEvent.isCreatorFound())) {
      response = new ResponseEntity<Poll>(HttpStatus.NOT_FOUND);
    } else if ((null == pollCreatedEvent.getDetails())
      || (null == pollCreatedEvent.getDetails().getNodeId())) {
      response = new ResponseEntity<Poll>(HttpStatus.BAD_REQUEST);
    } else {
      Poll result = Poll.fromPollDetails((PollDetails) pollCreatedEvent
        .getDetails());
      if (LOG.isDebugEnabled()) LOG.debug("poll" + result.toString());
      return new ResponseEntity<Poll>(result, HttpStatus.CREATED);
    }
    return response;
  }

  // Update
  @RequestMapping(method = RequestMethod.PUT, value = ControllerConstants.POLL_LABEL
    + "/{pollId}")
  public @ResponseBody
  ResponseEntity<Poll> updatePoll(
    @PathVariable Long pollId, @RequestBody Poll poll) {
    if (LOG.isInfoEnabled())
      LOG.info("Attempting to update poll. " + pollId);
    UpdatedEvent pollUpdatedEvent = pollService
      .updatePoll(new UpdatePollEvent(pollId, poll.toPollDetails()));
    if (null != pollUpdatedEvent) {
      if (LOG.isDebugEnabled())
        LOG.debug("pollUpdatedEvent - " + pollUpdatedEvent);
      if (pollUpdatedEvent.isEntityFound()) {
        Poll resultPoll = Poll
          .fromPollDetails((PollDetails) pollUpdatedEvent
            .getDetails());
        if (LOG.isDebugEnabled())
          LOG.debug("resultPoll = " + resultPoll);
        return new ResponseEntity<Poll>(resultPoll, HttpStatus.OK);
      } else {
        return new ResponseEntity<Poll>(HttpStatus.NOT_FOUND);
      }
    } else return new ResponseEntity<Poll>(HttpStatus.BAD_REQUEST);
  }

  // Delete
  @RequestMapping(method = RequestMethod.DELETE, value = ControllerConstants.POLL_LABEL
    + "/{pollId}")
  public @ResponseBody
  ResponseEntity<Response> deletePoll(
    @PathVariable Long pollId) {
    if (LOG.isInfoEnabled())
      LOG.info("Attempting to delete poll. " + pollId);
    ResponseEntity<Response> response;
    DeletedEvent pollDeletedEvent = pollService
      .deletePoll(new DeletePollEvent(pollId));
    Response restEvent;
    if (!pollDeletedEvent.isEntityFound()) {
      restEvent = Response.failed("Not found");
      response = new ResponseEntity<Response>(restEvent, HttpStatus.NOT_FOUND);
    } else {
      if (pollDeletedEvent.isDeletionCompleted()) {
        restEvent = new Response();
        response = new ResponseEntity<Response>(restEvent, HttpStatus.OK);
      } else {
        restEvent = Response.failed("Could not delete");
        response = new ResponseEntity<Response>(restEvent, HttpStatus.GONE);
      }
    }
    return response;
  }

  // Answer Poll
  @RequestMapping(method = RequestMethod.PUT, value = ControllerConstants.POLL_LABEL + "/{pollId}/answer")
  public @ResponseBody
  ResponseEntity<PollAnswer> answerPoll(@PathVariable Long pollId, @RequestBody PollAnswer pollAnswer) {
    if (LOG.isInfoEnabled())
      LOG.info("attempting to answer poll " + pollId + " answer - " + pollAnswer);
    ResponseEntity<PollAnswer> response;
    if ((null == pollAnswer) || (null == pollId) || (null == pollAnswer.getPollId()) || (null == pollAnswer.getAnswererId())) {
      response = new ResponseEntity<PollAnswer>(HttpStatus.BAD_REQUEST);
    } else {
      CreatePollAnswerEvent createPollAnswerEvent = new CreatePollAnswerEvent(
        pollAnswer.toPollAnswerDetails());
      PollAnswerCreatedEvent pollAnswerCreatedEvent = pollService.answerPoll(createPollAnswerEvent);
      if (null == pollAnswerCreatedEvent) {
        response = new ResponseEntity<PollAnswer>(HttpStatus.BAD_REQUEST);
      } else if (!(pollAnswerCreatedEvent.isPollFound())) {
        response = new ResponseEntity<PollAnswer>(HttpStatus.NOT_FOUND);
      } else if (!(pollAnswerCreatedEvent.isAnswererFound())) {
        LOG.debug("Answerer not found- ID:"+ pollAnswer.getAnswererId());
        response = new ResponseEntity<PollAnswer>(HttpStatus.NOT_FOUND);
      } else if (!(pollAnswerCreatedEvent.isAnswerValid())) {
        response = new ResponseEntity<PollAnswer>(HttpStatus.BAD_REQUEST);
      } else if ((null == pollAnswerCreatedEvent.getDetails())
        || (null == pollAnswerCreatedEvent.getDetails().getNodeId())) {
        response = new ResponseEntity<PollAnswer>(HttpStatus.BAD_REQUEST);
      } else {
        PollAnswer result = PollAnswer.fromPollAnswerDetails((PollAnswerDetails) pollAnswerCreatedEvent
          .getDetails());
        if (LOG.isDebugEnabled()) LOG.debug(result.toString());
        return new ResponseEntity<PollAnswer>(result, HttpStatus.CREATED);
      }
    }
    return response;
  }

  // like
  @RequestMapping(method = RequestMethod.PUT, value = ControllerConstants.POLL_LABEL
    + "/{pollId}" + ControllerConstants.LIKED_BY_LABEL + "/{email}/")
  public @ResponseBody
  ResponseEntity<Response> likePoll(
    @PathVariable Long pollId, @PathVariable String email) {
    if (LOG.isInfoEnabled())
      LOG.info("Attempting to have " + email + " like poll. " + pollId);
    LikedEvent likedPollEvent = likesService.like(new LikeEvent(pollId,
      email));
    ResponseEntity<Response> response;
    if (!likedPollEvent.isEntityFound()) {
      response = new ResponseEntity<Response>(HttpStatus.GONE);
    } else if (!likedPollEvent.isUserFound()) {
      response = new ResponseEntity<Response>(HttpStatus.NOT_FOUND);
    } else {
      Response restEvent;
      if (likedPollEvent.isResultSuccess())
        restEvent = new Response();
      else
        restEvent = Response.failed("Could not like.");
      response = new ResponseEntity<Response>(restEvent, HttpStatus.OK);
    }
    return response;
  }

  // unlike
  @RequestMapping(method = RequestMethod.DELETE, value = ControllerConstants.POLL_LABEL
    + "/{pollId}" + ControllerConstants.LIKED_BY_LABEL + "/{email}/")
  public @ResponseBody
  ResponseEntity<Response> unlikePoll(
    @PathVariable Long pollId, @PathVariable String email) {
    if (LOG.isInfoEnabled())
      LOG.info("Attempting to have " + email + " unlike poll. " + pollId);
    LikedEvent unlikedPollEvent = likesService.unlike(new LikeEvent(pollId,
      email));
    ResponseEntity<Response> response;
    if (!unlikedPollEvent.isEntityFound()) {
      response = new ResponseEntity<Response>(HttpStatus.GONE);
    } else if (!unlikedPollEvent.isUserFound()) {
      response = new ResponseEntity<Response>(HttpStatus.NOT_FOUND);
    } else {
      Response restEvent;
      if (unlikedPollEvent.isResultSuccess())
        restEvent = new Response();
      else
        restEvent = Response.failed("Could not unlike.");
      response = new ResponseEntity<Response>(restEvent, HttpStatus.OK);
    }
    return response;
  }

  // likes
  @RequestMapping(method = RequestMethod.GET, value = ControllerConstants.POLL_LABEL
    + "/{pollId}" + ControllerConstants.LIKES_LABEL)
  public @ResponseBody
  ResponseEntity<Iterator<LikeInfo>> findLikes(
    @PathVariable Long pollId,
    @RequestParam(value = "direction", required = false, defaultValue = ControllerConstants.DIRECTION) String direction,
    @RequestParam(value = "page", required = false, defaultValue = ControllerConstants.PAGE_NUMBER) String page,
    @RequestParam(value = "pageSize", required = false, defaultValue = ControllerConstants.PAGE_LENGTH) String pageSize) {
    int pageNumber = 0;
    int pageLength = 10;
    pageNumber = Integer.parseInt(page);
    pageLength = Integer.parseInt(pageSize);
    if (LOG.isInfoEnabled())
      LOG.info("Attempting to retrieve liked users from poll " + pollId
        + '.');
    Direction sortDirection = Direction.DESC;
    if (direction.equalsIgnoreCase("asc")) sortDirection = Direction.ASC;
    LikeableObjectLikesEvent likeableObjectLikesEvent = likesService.likes(
      new LikesLikeableObjectEvent(pollId), sortDirection,
      pageNumber, pageLength);
    Iterator<LikeInfo> likes = User
      .toLikesIterator(likeableObjectLikesEvent.getUserDetails()
        .iterator());
    if (likes.hasNext() == false) {
      ReadEvent readPollEvent = pollService
        .requestReadPoll(new RequestReadPollEvent(pollId));
      if (!readPollEvent.isEntityFound())
        return new ResponseEntity<Iterator<LikeInfo>>(
          HttpStatus.NOT_FOUND);
      else return new ResponseEntity<Iterator<LikeInfo>>(likes,
        HttpStatus.OK);
    } else return new ResponseEntity<Iterator<LikeInfo>>(likes, HttpStatus.OK);
  }
}
