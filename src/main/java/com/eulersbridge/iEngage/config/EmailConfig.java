package com.eulersbridge.iEngage.config;

import com.eulersbridge.iEngage.core.services.EmailEventHandler;
import com.eulersbridge.iEngage.core.services.EmailService;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.tools.view.WebappResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;

import java.util.Properties;

//import org.springframework.core.io.Resource;
@Configuration
public class EmailConfig 
{
	@Value("${email.host}")
	private String host;
	
	@Value("${email.port}")
	private Integer port;
	
	@Value("${email.username}")
	private String username;
	
	@Value("${email.password}")
	private String password;
	
    private static Logger LOG = LoggerFactory.getLogger(EmailConfig.class);
    
	@Bean
	public EmailService createEmailService()
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("createEmailService()");
			LOG.debug("host - "+host+" username "+username);
		}
		
		return new EmailEventHandler();
	}

	@Bean
	public JavaMailSender mailSender()
	{
		if (LOG.isDebugEnabled()) LOG.debug("mailSender()");
		JavaMailSenderImpl sender=new JavaMailSenderImpl();
		sender.setHost(host);
		sender.setPort(port);
		sender.setUsername(username);
		sender.setPassword(password);
		Properties smtpProperties=getSmtpMailProperties();
		sender.setJavaMailProperties(smtpProperties);
		if (LOG.isDebugEnabled()) LOG.debug("host = "+host+" port = "+port);
		if (LOG.isDebugEnabled()) LOG.debug("properties = "+smtpProperties);
		if (LOG.isDebugEnabled()) LOG.debug("Sender = "+sender);
		return sender;
		
	}

	// TODO: Witch to FreeMarker as Spring in favor of
	@Bean
	public VelocityEngineFactoryBean velocityEngine()
	{
		if (LOG.isDebugEnabled()) LOG.debug("velocityEngine()");
		VelocityEngineFactoryBean ve=new VelocityEngineFactoryBean();
		Properties velocityProperties=new Properties();
		velocityProperties.setProperty(RuntimeConstants.RESOURCE_LOADER, "webapp");
		velocityProperties.setProperty("webapp.resource.loader.path", "/");
		velocityProperties.setProperty("webapp.resource.loader.class", WebappResourceLoader.class.getName());
		ve.setVelocityProperties(velocityProperties);
		return ve;
	}
	
	private Properties getSmtpMailProperties()
	{
		Properties javaMailProperties=new Properties();
		javaMailProperties.setProperty("mail.transport.protocol", "smtp");
		javaMailProperties.setProperty("mail.smtp.auth", "true");
		javaMailProperties.setProperty("mail.smtp.starttls.enable", "true");
		javaMailProperties.setProperty("mail.debug", "true");
		return javaMailProperties;
	}
	
/*	private Properties getAwsMailProperties()
	{
		Properties awsMailProperties=new Properties();
		awsMailProperties.setProperty("mail.transport.protocol", "aws");
		awsMailProperties.setProperty("mail.aws.user", "true");
		awsMailProperties.setProperty("mail.aws.password", "true");
		awsMailProperties.setProperty("mail.debug", "true");
		return awsMailProperties;
	}
*/	

}