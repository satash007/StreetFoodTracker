package com.info3604.streetfoodtracker;


import com.info3604.streetfoodtracker.model.User;

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
        System.out.println("Testing getID() method in the User class...");
        String expResult = "12345";
        String actualResult = user.getUID();
        assertEquals(expResult, actualResult);
    }



    @Test
    public void testGetFullName(){
        System.out.println("Testing getFullName() method in the User class...");
        String expResult = "John Doe";
        String actualResult = user.getName();
        assertEquals(expResult, actualResult);
    }


    @Test
    public void testEmail(){
        System.out.println("Testing getFullName() method in the User class...");
        String expResult = "johndoe@gmail.com";
        String actualResult = user.getEmail();
        assertEquals(expResult, actualResult);
    }


    @Test
    public void testPassword(){
        System.out.println("Testing getPassword() method in the User class...");
        String expResult = "password";
        String actualResult = user.getEmail();
        assertEquals(expResult, actualResult);
    }



    @Test
    public void testAccountType(){
        System.out.println("Testing getAccountType() method in the User class...");
        String expResult = "customer";
        String actualResult = user.getAccountType();
        assertEquals(expResult, actualResult);
    }


    @Test
    public void testPhoneNumber(){
        System.out.println("Testing getPhoneNumber() method in the User class...");
        String expResult = "8499902";
        String actualResult = user.getPhoneNumber();
        assertEquals(expResult, actualResult);
    }


    @Test
    public void testAddress(){
        System.out.println("Testing getAddress() method in the User class...");
        String expResult = "Rio Claro";
        String actualResult = user.getAddress();
        assertEquals(expResult, actualResult);
    }




    @Test
    public void testLatitude(){
        System.out.println("Testing getLatitude() method in the User class...");
        double expResult = 669860.00;
        double actualResult = user.getLatitude();
        assertEquals(expResult, actualResult);
    }


    @Test
    public void testLongitude(){
        System.out.println("Testing getLongitude() method in the User class...");
        double expResult = -394993.00;
        double actualResult = user.getLongitude();
        assertEquals(expResult, actualResult);
    }



    @Test
    public void testImage(){
        System.out.println("Testing getImg() method in the User class...");
        String expResult = "link";
        String actualResult = user.getImg();
        assertEquals(expResult, actualResult);
    }








}

