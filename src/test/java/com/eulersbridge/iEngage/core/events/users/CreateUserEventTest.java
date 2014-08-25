package com.eulersbridge.iEngage.core.events.users;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Yikai Gong
 */

public class CreateUserEventTest {
    final String email = new String("yikaig@gmail.com");
    final String firstName = new String("Yikai");
    final String lastName = new String("Gong");
    final String gender = new String("male");
    final String nationality = new String("China");
    final String yearOfBirth = new String("1989");
    final String personality = new String("Idealist");
    final String password = new String("password");
    final boolean accountVerified = true;
    final Long institutionId = new Long(1);
    UserDetails userDetails = null;
    CreateUserEvent createUserEvent = null;

    @Before
    public void setUp() throws Exception {
        userDetails = new UserDetails(email);
        userDetails.setFirstName(firstName);
        userDetails.setLastName(lastName);
        userDetails.setGender(gender);
        userDetails.setNationality(nationality);
        userDetails.setYearOfBirth(yearOfBirth);
        userDetails.setPersonality(personality);
        userDetails.setPassword(password);
        userDetails.setInstitutionId(institutionId);

        createUserEvent = new CreateUserEvent(email,userDetails);

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testCreateUserEvent() throws Exception {
        assertNotNull("createUserEvent is null", createUserEvent);
        CreateUserEvent createUserEvent1 = new CreateUserEvent(userDetails);
        assertNotNull("CreateUserEvent is null", createUserEvent1);
    }

    @Test
    public void testGetUserDetails() throws Exception {
        assertEquals("userDetails does not match", userDetails, createUserEvent.getUserDetails());
    }

    @Test
    public void testSetUserDetails() throws Exception {
        UserDetails userDetails1 = new UserDetails(email);
        userDetails1.setFirstName(firstName);
        userDetails1.setLastName(lastName);
        userDetails1.setGender(gender);
        userDetails1.setNationality(nationality);
        userDetails1.setYearOfBirth(yearOfBirth);
        userDetails1.setPersonality(personality);
        userDetails1.setPassword(password);
        userDetails1.setInstitutionId(institutionId);
        CreateUserEvent createUserEvent1 = new CreateUserEvent(email, userDetails1);
        assertEquals("userDetails does not match", userDetails1, createUserEvent1.getUserDetails());
    }
}