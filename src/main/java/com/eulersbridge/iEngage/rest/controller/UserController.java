package com.eulersbridge.iEngage.rest.controller;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;

import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.VelocityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import com.eulersbridge.iEngage.core.events.users.CreateUserEvent;
import com.eulersbridge.iEngage.core.events.users.DeleteUserEvent;
import com.eulersbridge.iEngage.core.events.users.ReadUserEvent;
import com.eulersbridge.iEngage.core.events.users.RequestReadUserEvent;
import com.eulersbridge.iEngage.core.events.users.UpdateUserEvent;
import com.eulersbridge.iEngage.core.events.users.UserCreatedEvent;
import com.eulersbridge.iEngage.core.events.users.UserDeletedEvent;
import com.eulersbridge.iEngage.core.events.users.UserUpdatedEvent;
import com.eulersbridge.iEngage.core.events.users.UserAccountVerifiedEvent;
import com.eulersbridge.iEngage.core.events.users.VerifyUserAccountEvent;
import com.eulersbridge.iEngage.core.events.users.AddPersonalityEvent;
import com.eulersbridge.iEngage.core.events.users.PersonalityAddedEvent;
import com.eulersbridge.iEngage.core.services.EmailService;
import com.eulersbridge.iEngage.core.services.UserService;
import com.eulersbridge.iEngage.email.EmailConstants;
import com.eulersbridge.iEngage.rest.domain.Personality;
import com.eulersbridge.iEngage.rest.domain.User;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired UserService userService;
    @Autowired EmailService emailService;
    
    @Autowired ServletContext servletContext;
    @Autowired VelocityEngine velocityEngine;
	@Autowired JavaMailSender emailSender;
    
	public UserController() 
	{
		if (LOG.isDebugEnabled()) LOG.debug("UserController()");
	}

    private static Logger LOG = LoggerFactory.getLogger(UserController.class);

    /**
     * Is passed all the necessary data to update a user.
     * Or potentially create a new one.
     * The request must be a PUT with the necessary parameters in the
     * attached data.
     * <p/>
     * This method will return the resulting user object. 
     * There will also be a relationship set up with the 
     * institution the user belongs to.
     * 
     * @param email the email address of the user to be updated.
     * @param user the user object passed across as JSON.
     * @return the user object returned by the Graph Database.
     * 

	*/
    
    @RequestMapping(method=RequestMethod.PUT,value="/user/{email}")
    public @ResponseBody ResponseEntity<User> alterUser(@PathVariable String email,
    		@RequestBody User user) 
    {
    	if (LOG.isInfoEnabled()) LOG.info("Attempting to edit user. "+user.getEmail());
    	
    	UserUpdatedEvent userEvent=userService.updateUser(new UpdateUserEvent(email,user.toUserDetails()));
    	if ((null!=userEvent)&&(LOG.isDebugEnabled()))
    		LOG.debug("userEvent - "+userEvent);
    	if (!userEvent.isInstituteFound())
    	{
    		return new ResponseEntity<User>(HttpStatus.FAILED_DEPENDENCY);
    	}
    	else
    	{
    	User restUser=User.fromUserDetails(userEvent.getUserDetails());
    	if (LOG.isDebugEnabled()) LOG.debug("restUser = "+restUser);
      	return new ResponseEntity<User>(restUser,HttpStatus.OK);
    	}
    }
    
    /**
     * Is passed all the necessary data to update a user.
     * Or potentially create a new one.
     * The request must be a PUT with the necessary parameters in the
     * attached data.
     * <p/>
     * This method will return the resulting user object. 
     * There will also be a relationship set up with the 
     * institution the user belongs to.
     * 
     * @param email the email address of the user to be updated.
     * @param user the user object passed across as JSON.
     * @return the user object returned by the Graph Database.
     * 

	*/
    
    @RequestMapping(method=RequestMethod.PUT,value="/user/{email}/personality")
    public @ResponseBody ResponseEntity<Personality> addUserPersonality(@PathVariable String email,
    		@RequestBody Personality personality) 
    {
    	ResponseEntity<Personality> result;
    	if (LOG.isInfoEnabled()) LOG.info("Attempting to add personality to user. ");
    	
    	PersonalityAddedEvent persEvent=userService.addPersonality(new AddPersonalityEvent(email,personality.toPersonalityDetails()));
    	if (persEvent!=null)
    	{
    		if (LOG.isDebugEnabled()) LOG.debug("personalityEvent - "+persEvent);
	    	if (persEvent.isUserFound())
	       	{
		    	Personality restPersonality=Personality.fromPersonalityDetails(persEvent.getPersonalityDetails());
		    	if (LOG.isDebugEnabled()) LOG.debug("restUser = "+restPersonality);
		      	result= new ResponseEntity<Personality>(restPersonality,HttpStatus.CREATED);
	    	}
	    	else
	    	{
	    		result=new ResponseEntity<Personality>(HttpStatus.FAILED_DEPENDENCY);
	    	}
    	}
    	else
    	{
    		if (LOG.isWarnEnabled()) LOG.warn("personalityEvent null");
    		result=new ResponseEntity<Personality>(HttpStatus.BAD_REQUEST);
    	}
    	return result;
    }
    
    /**
     * Is passed all the necessary data to read a user from the database.
     * The request must be a GET with the user email presented
     * as the final portion of the URL.
     * <p/>
     * This method will return the user object read from the database.
     * 
     * @param email the email address of the user object to be read.
     * @return the user object.
     * 

	*/
	@RequestMapping(method=RequestMethod.GET,value="/user/{email}")
	public @ResponseBody ResponseEntity<User> findUser(@PathVariable String email) 
	{
		if (LOG.isInfoEnabled()) LOG.info("Attempting to retrieve user. "+email);
		ReadUserEvent userEvent=userService.requestReadUser(new RequestReadUserEvent(email));
  	
		if (!userEvent.isEntityFound())
		{
			return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
		}
		User restUser=User.fromUserDetails(userEvent.getReadUserDetails());
		return new ResponseEntity<User>(restUser,HttpStatus.OK);
	}
    
    /**
     * Is passed all the necessary data to delete a user.
     * The request must be a DELETE with the user email presented
     * as the final portion of the URL.
     * <p/>
     * This method will return the deleted user object.
     * 
     * @param email the email address of the user object to be deleted.
     * @return the user object deleted.
     * 

	*/
	@RequestMapping(method=RequestMethod.DELETE,value="/user/{email}")
	public @ResponseBody ResponseEntity<User> deleteUser(@PathVariable String email) 
	{
		if (LOG.isInfoEnabled()) LOG.info("Attempting to delete user. "+email);
		UserDeletedEvent userEvent=userService.deleteUser(new DeleteUserEvent(email));
  	
		if (!userEvent.isEntityFound())
		{
			return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
		}
		User restUser=User.fromUserDetails(userEvent.getDetails());
		return new ResponseEntity<User>(restUser,HttpStatus.OK);
	}
    
    
    /**
     * Is passed all the necessary data to create a new user.
     * The request must be a POST with the necessary parameters in the
     * attached data.
     * <p/>
     * This method will return the resulting user object.
     * There will also be a relationship set up with the 
     * institution the user belongs to.
     * 
     * @param user the user object passed across as JSON.
     * @return the user object returned by the Graph Database.
     * 

	*/
    
    @RequestMapping(method=RequestMethod.POST,value="/signUp")
    public @ResponseBody ResponseEntity<User> saveNewUser(@RequestBody User user) 
    {
    	if (LOG.isInfoEnabled()) LOG.info("attempting to save user "+user);
    	UserCreatedEvent userEvent=userService.signUpNewUser(new CreateUserEvent(user.toUserDetails()));

    	if (userEvent.getEmail()==null)
    	{
    		return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
    	}
    	else if (!userEvent.isInstituteFound())
    	{
    		return new ResponseEntity<User>(HttpStatus.FAILED_DEPENDENCY);
    	}
    	else if (!userEvent.isUserUnique())
    	{
    		return new ResponseEntity<User>(HttpStatus.CONFLICT);
    	}
    	else
    	{
	    	User restUser=User.fromUserDetails(userEvent.getUserDetails());
	    	if (LOG.isDebugEnabled()) LOG.debug(userEvent.getVerificationEmail().toString());
	    	emailService.sendEmail(userEvent.getVerificationEmail());
	    	return new ResponseEntity<User>(restUser,HttpStatus.CREATED);
    	}
    }
    
    /**
     * Is passed all the necessary data to verify a user account.
     * The request must be a POST with the necessary parameters in the
     * URL.
     * <p/>
     * This method will return the user object owner of the verified account.
     * 
     * @param email the email address of the user account to be verified.
     * @param token the token string to be used to verify the user account.
     * @return the user object returned by the Graph Database.
     * 
	*/
    @RequestMapping(method=RequestMethod.POST,value="/emailVerification/{email}/{token}")
    public @ResponseBody ResponseEntity<User> verifyUserAccount(@PathVariable String email, @PathVariable String token) 
    {
    	if (LOG.isInfoEnabled()) LOG.info("attempting to verify email by token "+email+" " +token);
    	byte[] decodedTokenBytes=Base64.decodeBase64(token);
    	UUID decodedToken=UUID.nameUUIDFromBytes(decodedTokenBytes);
    	if (LOG.isDebugEnabled()) LOG.debug("Decoded token - "+decodedToken);
    	UserAccountVerifiedEvent userAccountVerifiedEvent=userService.validateUserAccount(new VerifyUserAccountEvent(email,token));

    	if (!userAccountVerifiedEvent.isAccountVerified())
    	{
    		String verfError = userAccountVerifiedEvent.getVerificationError();
    		
    		if(verfError == UserAccountVerifiedEvent.VerificationErrorType.userNotFound.toString())
    			return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
    		else
    		{ 
    			User resultUser = User.fromUserDetails(userAccountVerifiedEvent.getUserDetails());
    			if(verfError == UserAccountVerifiedEvent.VerificationErrorType.tokenDoesntExists.toString())
    			return new ResponseEntity<User>(resultUser,HttpStatus.BAD_REQUEST);
    		else if(verfError == UserAccountVerifiedEvent.VerificationErrorType.tokenAlreadyUsed.toString()
    				|| verfError == UserAccountVerifiedEvent.VerificationErrorType.tokenExpired.toString()
    				|| verfError == UserAccountVerifiedEvent.VerificationErrorType.tokenTypeMismatch.toString())
    			return new ResponseEntity<User>(resultUser,HttpStatus.FAILED_DEPENDENCY);
    		else
    			return new ResponseEntity<User>(resultUser,HttpStatus.BAD_REQUEST);
    		}
    	}
    	else
    	{
	    	User restUser=User.fromUserDetails(userAccountVerifiedEvent.getUserDetails());
	    	return new ResponseEntity<User>(restUser,HttpStatus.OK);
    	}
    }
    
    @RequestMapping(value="/displayParams")
    public @ResponseBody ResponseEntity<Boolean> displayDetails(@RequestBody User user) 
    {
    	if (LOG.isInfoEnabled()) 
    		LOG.info("user = "+user);
    	return new ResponseEntity<Boolean>(true,HttpStatus.OK);
    }
    
    @RequestMapping(value="/testResources")
    public @ResponseBody ResponseEntity<StringWriter> testResources() 
    {
    	if (LOG.isInfoEnabled()) 
    		LOG.info("testResources()");
        if (LOG.isDebugEnabled()) LOG.debug("this.velocityEngine = "+this.velocityEngine);
        LOG.debug("Real path = "+servletContext.getRealPath("/"));
		LOG.debug("Real path = "+servletContext.getRealPath("/WEB-INF/"));
		String resourceName=EmailConstants.EmailVerificationTemplate;
		Template t;
	    StringWriter sw = new StringWriter();
		try {
			String info=servletContext.getServerInfo();
			if (LOG.isDebugEnabled())
			{
				LOG.debug("info - "+info);
			}
			velocityEngine.setApplicationAttribute("javax.servlet.ServletContext",servletContext );
			velocityEngine.init();

			VelocityContext context=new VelocityContext();
			context.put("recipientName", "Greg Newitt");
			context.put("emailAddress","gnewitt@hotmail.com");
			context.put("verificationToken","blahblahblah");
			
			final Map<String, Object> hTemplateVariables = new HashMap<String,Object>();

			hTemplateVariables.put("recipientName", "Greg Newitt");
			hTemplateVariables.put("emailAddress","gnewitt@hotmail.com");
			hTemplateVariables.put("verificationToken","blahblahblah");
		
			
			boolean exists=velocityEngine.resourceExists(resourceName);
	
			if (LOG.isDebugEnabled())
			{
				if (exists)
					LOG.debug("Resource name "+resourceName+" exists.");
				else
					LOG.warn("validate template2 does not exist. ");
			}
			t=velocityEngine.getTemplate(resourceName);
    	    t.merge(context, sw);
    	    
			String velocityModel=resourceName;
			String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, velocityModel,"UTF-8", hTemplateVariables);

    	    LOG.debug(sw.toString());
    	    LOG.debug(body);
		}
        catch (ResourceNotFoundException re)
        {
        	LOG.warn("Resource not found");
        }
        
        catch (VelocityException e) 
        {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return new ResponseEntity<StringWriter>(sw,HttpStatus.OK);
    }
    
}

