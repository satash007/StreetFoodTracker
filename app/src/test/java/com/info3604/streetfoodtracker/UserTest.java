package com.info3604.streetfoodtracker;


import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserTest {
    private User user;

    @Before
    public void initialize(){
        user = new User("12345", "John Doe", "johndoe@gmail.com", "password", "customer", "8499902", "Rio Claro", 668869.00, -394993.00, "link");
    }

    @Test
    public void testGetUser(){
        System.out.println("Testing getUID() method in the User class...");
        String expResult = "12345";
        String actualResult = user.getUID();
        assertEquals(expResult, actualResult);
    }

}
