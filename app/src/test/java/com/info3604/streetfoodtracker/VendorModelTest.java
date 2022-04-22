
package com.info3604.streetfoodtracker;


import com.info3604.streetfoodtracker.model.VendorModel;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertTrue;


public class VendorModelTest {
    private VendorModel vendorT;


    @Before
    public void intialize(){
        vendorT = new VendorModel( "Nigel", "Penal",  "346782","-456723.00","3.0", "Firebase", "56");
    }


    @Test
    public void testGetVendorName(){
        System.out.println("Testing getName() method in the VendorModel class...");
        String expResult = "Nigel" ;
        String actualResult = vendorT.getName();
        assertEquals(expResult, actualResult);
    }


    @Test
    public void testGetVendorAddress(){
        System.out.println("Testing getAddress() method in the VendorModel class...");
        String expResult = "Penal" ;
        String actualResult = vendorT.getAddress();
        assertEquals(expResult, actualResult);
    }


    @Test
    public void testGetVendorLatitude(){
        System.out.println("Testing  getLatitude() method in the VendorModel class...");
        String expResult = "346782";
        String actualResult = vendorT.getLatitude();
        assertEquals(expResult, actualResult);
    }

    @Test
    public void testGetVendorLongitiude(){
        System.out.println("Testing getLongitiude method in the VendorModel class...");
        String expResult = "-456723.00";
        String actualResult = vendorT.getLongitude();
        assertEquals(expResult, actualResult);
    }




    @Test
    public void toStringTest(){
        String toString = vendorT.toString();
        // testing the toString() method function
        // specifically the variable type that is returned respectively.
        assertTrue(toString.contains(vendorT.getName())); // name
        assertTrue(toString.contains(vendorT.getAddress())); // address
        assertTrue(toString.contains(vendorT.getLatitude())); // latitude
        assertTrue(toString.contains(vendorT.getLongitude()));
        assertTrue(toString.contains(vendorT.getRating()));
        assertTrue(toString.contains(vendorT.getDistance()));
        assertTrue(toString.contains(vendorT.getType()));

        System.out.println(toString);

    }




}
