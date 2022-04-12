package com.info3604.streetfoodtracker;

public class LocationTypeFirebaseModel {

    // Variable to store data corresponding
    // to foodName keyword in database
    private String locationName;

    // Variable to store data corresponding
    // to imageLink keyword in database
    private String imageLink;

    // Mandatory empty constructor
    // for use of FirebaseUI
    public LocationTypeFirebaseModel(){
    }

    // Getter and setter methods
    public String getLocationName(){
        return locationName;
    }

    public void setLocationName(String locationName){
        this.locationName = locationName;
    }

    public String getImageLink(){
        return imageLink;
    }

    public void setImageLink(String imageLink){
        this.imageLink = imageLink;
    }
}
