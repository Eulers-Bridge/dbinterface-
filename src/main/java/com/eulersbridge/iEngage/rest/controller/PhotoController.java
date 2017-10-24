/**
 * 
 */
package com.eulersbridge.iEngage.rest.controller;

import com.eulersbridge.iEngage.core.events.*;
import com.eulersbridge.iEngage.core.events.likes.LikeableObjectLikesEvent;
import com.eulersbridge.iEngage.core.events.likes.LikesLikeableObjectEvent;
import com.eulersbridge.iEngage.core.events.photo.*;
import com.eulersbridge.iEngage.core.events.photoAlbums.*;
import com.eulersbridge.iEngage.core.services.interfacePack.LikesService;
import com.eulersbridge.iEngage.core.services.interfacePack.PhotoService;
import com.eulersbridge.iEngage.core.services.interfacePack.UserService;
import com.eulersbridge.iEngage.rest.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Iterator;

/**
 * @author Greg Newitt
 *
 */
@RestController
@RequestMapping(ControllerConstants.API_PREFIX)
public class PhotoController
{
	private static Logger LOG = LoggerFactory.getLogger(PhotoController.class);

	@Autowired
	PhotoService photoService;
	@Autowired
	UserService userService;
	@Autowired
	LikesService likesService;

	public PhotoController()
	{
		if (LOG.isDebugEnabled()) LOG.debug("PhotoController()");
	}

	// Get
	@RequestMapping(method = RequestMethod.GET, value = ControllerConstants.PHOTO_LABEL
			+ "/{photoId}")
	public @ResponseBody ResponseEntity<PhotoDomain> findPhoto(
			@PathVariable Long photoId)
	{
		if (LOG.isInfoEnabled())
			LOG.info(photoId + " attempting to get photo. ");
		ReadPhotoEvent readPhotoEvent = new ReadPhotoEvent(photoId);
		ReadEvent photoReadEvent = photoService.readPhoto(readPhotoEvent);
		if (photoReadEvent.isEntityFound())
		{
			PhotoDomain photo = PhotoDomain.fromPhotoDetails((PhotoDetails) photoReadEvent
					.getDetails());
			return new ResponseEntity<PhotoDomain>(photo, HttpStatus.OK);
		}
		else
		{
			return new ResponseEntity<PhotoDomain>(HttpStatus.NOT_FOUND);
		}
	}

	// Get
	@RequestMapping(method = RequestMethod.GET, value = ControllerConstants.PHOTO_ALBUM_LABEL
			+ "/{photoAlbumId}")
	public @ResponseBody ResponseEntity<PhotoAlbum> findPhotoAlbum(
			@PathVariable Long photoAlbumId)
	{
		if (LOG.isInfoEnabled())
			LOG.info(photoAlbumId + " attempting to get photo. ");
		RequestReadEvent readPhotoAlbumEvent = new RequestReadEvent(
				photoAlbumId);
		ReadEvent photoAlbumReadEvent = photoService
				.readPhotoAlbum(readPhotoAlbumEvent);
		if (photoAlbumReadEvent.isEntityFound())
		{
			PhotoAlbum photoAlbum = PhotoAlbum
					.fromPhotoAlbumDetails((PhotoAlbumDetails) photoAlbumReadEvent
							.getDetails());
			return new ResponseEntity<PhotoAlbum>(photoAlbum, HttpStatus.OK);
		}
		else
		{
			return new ResponseEntity<PhotoAlbum>(HttpStatus.NOT_FOUND);
		}
	}

	// Create
	@RequestMapping(method = RequestMethod.POST, value = ControllerConstants.PHOTO_LABEL)
	public @ResponseBody ResponseEntity<PhotoDomain> createPhoto(
			@RequestBody PhotoDomain photo)
	{
		if (LOG.isInfoEnabled())
			LOG.info("attempting to create photo " + photo);
		ResponseEntity<PhotoDomain> response;
		if (null==photo)
		{
			response = new ResponseEntity<PhotoDomain>(HttpStatus.BAD_REQUEST);
		}
		else
		{
			photo.setDate(new Date().getTime());
			PhotoCreatedEvent photoCreatedEvent = photoService
					.createPhoto(new CreatePhotoEvent(photo.toPhotoDetails()));
			if (LOG.isDebugEnabled()) LOG.debug("photoCreatedEvent "+photoCreatedEvent);
			if ((null == photoCreatedEvent)
					|| (null == photoCreatedEvent.getNodeId()))
			{
				response = new ResponseEntity<PhotoDomain>(HttpStatus.BAD_REQUEST);
			}
			else if (!(photoCreatedEvent.isOwnerFound()))
			{
				response = new ResponseEntity<PhotoDomain>(HttpStatus.NOT_FOUND);
			}
			else
			{
				PhotoDomain result = PhotoDomain
						.fromPhotoDetails((PhotoDetails) photoCreatedEvent
								.getDetails());
				if (LOG.isDebugEnabled()) LOG.debug("photo " + result.toString());
				response = new ResponseEntity<PhotoDomain>(result, HttpStatus.CREATED);
			}
		}
		return response;
	}

	// Create
	@RequestMapping(method = RequestMethod.POST, value = ControllerConstants.PHOTO_ALBUM_LABEL)
	public @ResponseBody ResponseEntity<PhotoAlbum> createPhotoAlbum(
			@RequestBody PhotoAlbum photoAlbum)
	{
		if (LOG.isInfoEnabled())
			LOG.info("attempting to create photoAlbum " + photoAlbum);
		ResponseEntity<PhotoAlbum> response;
		if (null==photoAlbum)
		{
			response = new ResponseEntity<PhotoAlbum>(HttpStatus.BAD_REQUEST);
		}
		else
		{
			PhotoAlbumCreatedEvent photoAlbumCreatedEvent = photoService
					.createPhotoAlbum(new CreatePhotoAlbumEvent(photoAlbum
							.toPhotoAlbumDetails()));
			if (null == photoAlbumCreatedEvent)
			{
				response = new ResponseEntity<PhotoAlbum>(HttpStatus.BAD_REQUEST);
			}
			else if (!(photoAlbumCreatedEvent.isOwnerFound()))
			{
				response = new ResponseEntity<PhotoAlbum>(HttpStatus.NOT_FOUND);
			}
			else if (!(photoAlbumCreatedEvent.isCreatorFound()))
			{
				response = new ResponseEntity<PhotoAlbum>(HttpStatus.NOT_FOUND);
			}
			else if ((null == photoAlbumCreatedEvent.getDetails())
					|| (null == photoAlbumCreatedEvent.getDetails().getNodeId()))
			{
				response = new ResponseEntity<PhotoAlbum>(HttpStatus.BAD_REQUEST);
			}
			else
			{
				PhotoAlbum result = PhotoAlbum
						.fromPhotoAlbumDetails((PhotoAlbumDetails) photoAlbumCreatedEvent
								.getDetails());
				if (LOG.isDebugEnabled())
					LOG.debug("photoAlbum " + result.toString());
				response = new ResponseEntity<PhotoAlbum>(result,
						HttpStatus.CREATED);
			}
		}
		return response;
	}

	// Delete
	@RequestMapping(method = RequestMethod.DELETE, value = ControllerConstants.PHOTO_LABEL
			+ "/{photoId}")
	public @ResponseBody ResponseEntity<Response> deletePhoto(
			@PathVariable Long photoId)
	{
		if (LOG.isInfoEnabled())
			LOG.info("Attempting to delete photo. " + photoId);
		ResponseEntity<Response> response;
		DeletedEvent elecEvent = photoService.deletePhoto(new DeletePhotoEvent(
				photoId));
        Response restEvent;
        if (!elecEvent.isEntityFound()){
            restEvent = Response.failed("Not found");
            response = new ResponseEntity<Response>(restEvent, HttpStatus.NOT_FOUND);
        }
        else{
            if (elecEvent.isDeletionCompleted()){
                restEvent = new Response();
                response=new ResponseEntity<Response>(restEvent,HttpStatus.OK);
            }
            else {
                restEvent = Response.failed("Could not delete");
                response=new ResponseEntity<Response>(restEvent,HttpStatus.GONE);
            }
        }
        return response;
	}

	// Delete
	@RequestMapping(method = RequestMethod.DELETE, value = ControllerConstants.PHOTO_ALBUM_LABEL
			+ "/{photoAlbumId}")
	public @ResponseBody ResponseEntity<Response> deletePhotoAlbum(
			@PathVariable Long photoAlbumId)
	{
		if (LOG.isInfoEnabled())
			LOG.info("Attempting to delete photo. " + photoAlbumId);
		ResponseEntity<Response> response;
		DeletedEvent elecEvent = photoService.deletePhotoAlbum(new DeletePhotoAlbumEvent(
				photoAlbumId));
        Response restEvent;
        if (!elecEvent.isEntityFound()){
            restEvent = Response.failed("Not found");
            response = new ResponseEntity<Response>(restEvent, HttpStatus.NOT_FOUND);
        }
        else{
            if (elecEvent.isDeletionCompleted()){
                restEvent = new Response();
                response=new ResponseEntity<Response>(restEvent,HttpStatus.OK);
            }
            else {
                restEvent = Response.failed("Could not delete");
                response=new ResponseEntity<Response>(restEvent,HttpStatus.GONE);
            }
        }
        return response;
	}

	// Update
	@RequestMapping(method = RequestMethod.PUT, value = ControllerConstants.PHOTO_LABEL
			+ "/{photoId}")
	public @ResponseBody ResponseEntity<PhotoDomain> updatePhoto(
			@PathVariable Long photoId, @RequestBody PhotoDomain photo)
	{
		if (LOG.isInfoEnabled())
			LOG.info("Attempting to update photo. " + photoId);
		photo.setDate(new Date().getTime());
		UpdatedEvent photoUpdatedEvent = photoService
				.updatePhoto(new UpdatePhotoEvent(photoId, photo
						.toPhotoDetails()));
		if ((null != photoUpdatedEvent))
		{
			if (LOG.isDebugEnabled())
				LOG.debug("photoUpdatedEvent - " + photoUpdatedEvent);
			if (photoUpdatedEvent.isEntityFound())
			{
				PhotoDomain restPhoto = PhotoDomain.fromPhotoDetails((PhotoDetails) photoUpdatedEvent
						.getDetails());
				if (LOG.isDebugEnabled())
					LOG.debug("restPhoto = " + restPhoto);
				return new ResponseEntity<PhotoDomain>(restPhoto, HttpStatus.OK);
			}
			else
			{
				return new ResponseEntity<PhotoDomain>(HttpStatus.NOT_FOUND);
			}
		}
		else
		{
			return new ResponseEntity<PhotoDomain>(HttpStatus.BAD_REQUEST);
		}
	}

	// Update
	@RequestMapping(method = RequestMethod.PUT, value = ControllerConstants.PHOTO_ALBUM_LABEL
			+ "/{photoAlbumId}")
	public @ResponseBody ResponseEntity<PhotoAlbum> updatePhotoAlbum(
			@PathVariable Long photoAlbumId, @RequestBody PhotoAlbum photoAlbum)
	{
		if (LOG.isInfoEnabled())
			LOG.info("Attempting to update photo. " + photoAlbumId);
		ResponseEntity<PhotoAlbum> response;
		
		UpdatedEvent photoAlbumUpdatedEvent = photoService
				.updatePhotoAlbum(new UpdatePhotoAlbumEvent(photoAlbumId, photoAlbum
						.toPhotoAlbumDetails()));
		if ((null == photoAlbumUpdatedEvent))
		{
			response = new ResponseEntity<PhotoAlbum>(HttpStatus.BAD_REQUEST);
		}
		else if (!((PhotoAlbumUpdatedEvent)photoAlbumUpdatedEvent).isOwnerFound())
		{
			response = new ResponseEntity<PhotoAlbum>(HttpStatus.NOT_FOUND);
		}
		else if (!((PhotoAlbumUpdatedEvent)photoAlbumUpdatedEvent).isCreatorFound())
		{
			response = new ResponseEntity<PhotoAlbum>(HttpStatus.NOT_FOUND);
		}
		else if ((null == photoAlbumUpdatedEvent.getDetails())
				|| (null == photoAlbumUpdatedEvent.getDetails().getNodeId()))
		{
			response = new ResponseEntity<PhotoAlbum>(HttpStatus.BAD_REQUEST);
		}
		else
		{
			if (LOG.isDebugEnabled())
				LOG.debug("photoUpdatedEvent - " + photoAlbumUpdatedEvent);
			if (photoAlbumUpdatedEvent.isEntityFound())
			{
				PhotoAlbum restPhotoAlbum = PhotoAlbum.fromPhotoAlbumDetails((PhotoAlbumDetails) photoAlbumUpdatedEvent
						.getDetails());
				if (LOG.isDebugEnabled())
					LOG.debug("restPhotoAlbum = " + restPhotoAlbum);
				response = new ResponseEntity<PhotoAlbum>(restPhotoAlbum, HttpStatus.OK);
			}
			else
			{
				response = new ResponseEntity<PhotoAlbum>(HttpStatus.NOT_FOUND);
			}
		}
		return response;
	}

	/**
	 * Is passed all the necessary data to read photos from the database. The
	 * request must be a GET with the ownerId presented as the final portion of
	 * the URL.
	 * <p/>
	 * This method will return the photos read from the database.
	 * 
	 * @param ownerId
	 *            the ownerId of the photo objects to be read.
	 * @return the photos.
	 * 
	 */
	@RequestMapping(method = RequestMethod.GET, value = ControllerConstants.PHOTOS_LABEL
			+ "/{ownerId}")
	public @ResponseBody ResponseEntity<Photos> findPhotos(
			@PathVariable(value = "") Long ownerId,
			@RequestParam(value = "direction", required = false, defaultValue = ControllerConstants.DIRECTION) String direction,
			@RequestParam(value = "page", required = false, defaultValue = ControllerConstants.PAGE_NUMBER) String page,
			@RequestParam(value = "pageSize", required = false, defaultValue = ControllerConstants.PAGE_LENGTH) String pageSize)
	{
		int pageNumber = 0;
		int pageLength = 10;
		pageNumber = Integer.parseInt(page);
		pageLength = Integer.parseInt(pageSize);
		if (LOG.isInfoEnabled())
			LOG.info("Attempting to retrieve photos for owner " + ownerId + '.');

		Direction sortDirection = Direction.DESC;
		if (direction.equalsIgnoreCase("asc")) sortDirection = Direction.ASC;
		return getThePhotos(ownerId, sortDirection, pageNumber, pageLength);
	}

	/**
	 * Is passed all the necessary data to delete photos from the database. The
	 * request must be a DELETE with the ownerId presented as the final portion of
	 * the URL.
	 * <p/>
	 * This method will return the photos read from the database.
	 * 
	 * @param ownerId
	 *            the ownerId of the photo objects to be deleted.
	 * @return the photos.
	 * 
	 */
	@RequestMapping(method = RequestMethod.DELETE, value = ControllerConstants.PHOTOS_LABEL
			+ "/{ownerId}")
	public @ResponseBody ResponseEntity<Long> deleteItems(
			@PathVariable(value = "") Long ownerId)
	{
		if (LOG.isInfoEnabled())
			LOG.info("Attempting to delete photos for owner " + ownerId + '.');

		ResponseEntity<Long> response;

		PhotosReadEvent photoEvent = photoService.deletePhotos(
				new ReadPhotosEvent(ownerId));

		if (!photoEvent.isEntityFound())
		{
			response = new ResponseEntity<Long>(HttpStatus.NOT_FOUND);
		}
		else
		{
			Long numPhotos=photoEvent.getTotalPhotos();
			if (LOG.isDebugEnabled()) LOG.debug("Total photos = "+numPhotos);
			response = new ResponseEntity<Long>(numPhotos, HttpStatus.OK);
		}
		return response;
	}

	/**
	 * Is passed all the necessary data to read photos from the database. The
	 * request must be a GET with the ownerId presented as the final portion of
	 * the URL.
	 * <p/>
	 * This method will return the photos read from the database.
	 * 
	 * @param ownerId
	 *            the ownerId of the photo objects to be read.
	 * @return the photos.
	 * 
	 */
	@RequestMapping(method = RequestMethod.GET, value = ControllerConstants.USER_LABEL
			+ ControllerConstants.PHOTOS_LABEL + "/{userEmail}")
	public @ResponseBody ResponseEntity<Photos> findProfilePhotos(
			@PathVariable(value = "") String userEmail,
			@RequestParam(value = "direction", required = false, defaultValue = ControllerConstants.DIRECTION) String direction,
			@RequestParam(value = "page", required = false, defaultValue = ControllerConstants.PAGE_NUMBER) String page,
			@RequestParam(value = "pageSize", required = false, defaultValue = ControllerConstants.PAGE_LENGTH) String pageSize)
	{
		int pageNumber = 0;
		int pageLength = 10;
		pageNumber = Integer.parseInt(page);
		pageLength = Integer.parseInt(pageSize);
		if (LOG.isInfoEnabled())
			LOG.info("Attempting to retrieve profile photos for user "
					+ userEmail + '.');

		Direction sortDirection = Direction.DESC;
		if (direction.equalsIgnoreCase("asc")) sortDirection = Direction.ASC;

		Long ownerId = userService.findUserId(userEmail);

		return getThePhotos(ownerId, sortDirection, pageNumber, pageLength);

	}

	private @ResponseBody ResponseEntity<Photos> getThePhotos(Long ownerId,
			Direction sortDirection, int pageNumber, int pageLength)
	{
		ResponseEntity<Photos> response;

		PhotosReadEvent photoEvent = photoService.findPhotos(
				new ReadPhotosEvent(ownerId), sortDirection, pageNumber,
				pageLength);

		if (!photoEvent.isEntityFound())
		{
			response = new ResponseEntity<Photos>(HttpStatus.NOT_FOUND);
		}
		else
		{
			Iterator<PhotoDomain> photoIter = PhotoDomain.toPhotosIterator(photoEvent
					.getPhotos().iterator());
			Photos photos = Photos.fromPhotosIterator(photoIter,
					photoEvent.getTotalPhotos(), photoEvent.getTotalPages());
			response = new ResponseEntity<Photos>(photos, HttpStatus.OK);
		}
		return response;
	}

	/**
	 * Is passed all the necessary data to read photoAlbums from the database. The
	 * request must be a GET with the ownerId presented as the final portion of
	 * the URL.
	 * <p/>
	 * This method will return the photoAlbums read from the database.
	 * 
	 * @param ownerId
	 *            the ownerId of the photoAlbum objects to be read.
	 * @return the photoAlbums.
	 * 
	 */
	@RequestMapping(method = RequestMethod.GET, value = ControllerConstants.PHOTO_ALBUMS_LABEL
			+ "/{ownerId}")
	public @ResponseBody ResponseEntity<WrappedDomainList> findPhotoAlbums(
			@PathVariable(value = "") Long ownerId,
			@RequestParam(value = "direction", required = false, defaultValue = ControllerConstants.DIRECTION) String direction,
			@RequestParam(value = "page", required = false, defaultValue = ControllerConstants.PAGE_NUMBER) String page,
			@RequestParam(value = "pageSize", required = false, defaultValue = ControllerConstants.PAGE_LENGTH) String pageSize)
	{
		int pageNumber = 0;
		int pageLength = 10;
		pageNumber = Integer.parseInt(page);
		pageLength = Integer.parseInt(pageSize);
		if (LOG.isInfoEnabled())
			LOG.info("Attempting to retrieve photoAlbums for owner " + ownerId + '.');

		Direction sortDirection = Direction.DESC;
		if (direction.equalsIgnoreCase("asc")) sortDirection = Direction.ASC;
		ResponseEntity<WrappedDomainList> response;

		AllReadEvent photoAlbumEvent = photoService.findPhotoAlbums(
				new ReadAllEvent(ownerId), sortDirection, pageNumber,
				pageLength);

		if (!photoAlbumEvent.isEntityFound())
		{
			response = new ResponseEntity<WrappedDomainList>(HttpStatus.NOT_FOUND);
		}
		else
		{
			Iterator<PhotoAlbum> photoAlbumIter = PhotoAlbum.toPhotoAlbumsIterator(photoAlbumEvent.getDetails().iterator());
			WrappedDomainList photoAlbums = WrappedDomainList.fromIterator(photoAlbumIter,
					photoAlbumEvent.getTotalItems(), photoAlbumEvent.getTotalPages());
			response = new ResponseEntity<WrappedDomainList>(photoAlbums, HttpStatus.OK);
		}
		return response;
	}

	/**
	 * Is passed all the necessary data to unlike an event from the database.
	 * The request must be a PUT with the event id presented along with the
	 * userid as the final portion of the URL.
	 * <p/>
	 * This method will return the a boolean result.
	 * 
	 * @param email
	 *            the eventId eventId of the event object to be unliked.
	 * @param email
	 *            the email address of the user unliking the event.
	 * @return the success or failure.
	 * 
	 */
	@RequestMapping(method = RequestMethod.DELETE, value = ControllerConstants.PHOTO_LABEL
			+ "/{photoId}"+ControllerConstants.LIKED_BY_LABEL+"/{email}/")
	public @ResponseBody ResponseEntity<Response> unlikeEvent(
			@PathVariable Long photoId, @PathVariable String email)
	{
		if (LOG.isInfoEnabled())
			LOG.info("Attempting to have " + email + " unlike photo. "
					+ photoId);
		LikedEvent event = likesService.unlike(new LikeEvent(photoId,
				email));

		ResponseEntity<Response> response;

		if (!event.isEntityFound())
		{
			response = new ResponseEntity<Response>(HttpStatus.GONE);
		}
		else if (!event.isUserFound())
		{
			response = new ResponseEntity<Response>(HttpStatus.NOT_FOUND);
		}
		else
		{
			Response restEvent;
			if (event.isResultSuccess())
				restEvent = new Response();
			else
				restEvent = Response.failed("Could not like.");
			response = new ResponseEntity<Response>(restEvent, HttpStatus.OK);
		}
		return response;
	}

	/**
	 * Is passed all the necessary data to like an event from the database. The
	 * request must be a PUT with the event id presented along with the userid
	 * as the final portion of the URL.
	 * <p/>
	 * This method will return the a boolean result.
	 * 
	 * @param email
	 *            the eventId eventId of the event object to be liked.
	 * @param email
	 *            the email address of the user liking the event.
	 * @return the success or failure.
	 * 
	 */
	@RequestMapping(method = RequestMethod.GET, value = ControllerConstants.PHOTO_LABEL
			+ "/{photoId}"+ControllerConstants.LIKED_BY_LABEL+"/{email}/")
	public @ResponseBody ResponseEntity<Response> isLikedBy(
			@PathVariable Long photoId, @PathVariable String email)
	{
		ResponseEntity<Response> response=entityIsLikedBy(likesService, photoId, email);
		return response;
	}

	static ResponseEntity<Response> entityIsLikedBy(LikesService likesServiceParam, Long entityId, String email)
	{
		if (LOG.isInfoEnabled())
			LOG.info("Checking if " + email + " likes "
					+ entityId);
		LikedEvent event = likesServiceParam
				.isLikedBy(new LikeEvent(entityId, email));
	
		ResponseEntity<Response> response;
	
		if (!event.isEntityFound())
		{
			response = new ResponseEntity<Response>(HttpStatus.GONE);
		}
		else if (!event.isUserFound())
		{
			response = new ResponseEntity<Response>(HttpStatus.NOT_FOUND);
		}
		else
		{
			Response restEvent;
			if (event.isResultSuccess())
				restEvent = new Response();
			else
				restEvent = Response.failed("No like.");
			response = new ResponseEntity<Response>(restEvent, HttpStatus.OK);
		}
		return response;
	}
	
	/**
	 * Is passed all the necessary data to like an event from the database. The
	 * request must be a PUT with the event id presented along with the userid
	 * as the final portion of the URL.
	 * <p/>
	 * This method will return the a boolean result.
	 * 
	 * @param email
	 *            the eventId eventId of the event object to be liked.
	 * @param email
	 *            the email address of the user liking the event.
	 * @return the success or failure.
	 * 
	 */
	@RequestMapping(method = RequestMethod.PUT, value = ControllerConstants.PHOTO_LABEL
			+ "/{photoId}"+ControllerConstants.LIKED_BY_LABEL+"/{email}/")
	public @ResponseBody ResponseEntity<Response> likeEvent(
			@PathVariable Long photoId, @PathVariable String email)
	{
		if (LOG.isInfoEnabled())
			LOG.info("Attempting to have " + email + " like news article. "
					+ photoId);
		LikedEvent event = likesService
				.like(new LikeEvent(photoId, email, PhotoDomain.class));

		ResponseEntity<Response> response;

		if (!event.isEntityFound())
		{
			response = new ResponseEntity<Response>(HttpStatus.GONE);
		}
		else if (!event.isUserFound())
		{
			response = new ResponseEntity<Response>(HttpStatus.NOT_FOUND);
		}
		else
		{
			Response restEvent;
			if (event.isResultSuccess())
				restEvent = new Response();
			else
				restEvent = Response.failed("Could not like.");
			response = new ResponseEntity<Response>(restEvent, HttpStatus.OK);
		}
		return response;
	}

	// likes
	@RequestMapping(method = RequestMethod.GET, value = ControllerConstants.PHOTO_LABEL
			+ "/{photoId}" + ControllerConstants.LIKES_LABEL)
	public @ResponseBody ResponseEntity<Iterator<LikeInfo>> findLikes(
			@PathVariable Long photoId,
			@RequestParam(value = "direction", required = false, defaultValue = ControllerConstants.DIRECTION) String direction,
			@RequestParam(value = "page", required = false, defaultValue = ControllerConstants.PAGE_NUMBER) String page,
			@RequestParam(value = "pageSize", required = false, defaultValue = ControllerConstants.PAGE_LENGTH) String pageSize)
	{
		int pageNumber = 0;
		int pageLength = 10;
		pageNumber = Integer.parseInt(page);
		pageLength = Integer.parseInt(pageSize);
		if (LOG.isInfoEnabled())
			LOG.info("Attempting to retrieve liked users from photo " + photoId
					+ '.');
		Direction sortDirection = Direction.DESC;
		if (direction.equalsIgnoreCase("asc")) sortDirection = Direction.ASC;
		LikeableObjectLikesEvent likeableObjectLikesEvent = likesService.likes(
				new LikesLikeableObjectEvent(photoId), sortDirection,
				pageNumber, pageLength);
		Iterator<LikeInfo> likes = User
				.toLikesIterator(likeableObjectLikesEvent.getUserDetails()
						.iterator());
		if (likes.hasNext() == false)
		{
			ReadEvent readPollEvent = photoService
					.readPhoto(new ReadPhotoEvent(photoId));
			if (!readPollEvent.isEntityFound())
				return new ResponseEntity<Iterator<LikeInfo>>(
						HttpStatus.NOT_FOUND);
			else return new ResponseEntity<Iterator<LikeInfo>>(likes,
					HttpStatus.OK);
		}
		else return new ResponseEntity<Iterator<LikeInfo>>(likes, HttpStatus.OK);
	}

}
