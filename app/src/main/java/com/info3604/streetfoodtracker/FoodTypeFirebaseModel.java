package com.info3604.streetfoodtracker;

public class FoodTypeFirebaseModel {

    // Variable to store data corresponding
    // to foodName keyword in database
    private String foodName;

    // Variable to store data corresponding
    // to imageLink keyword in database
    private String imageLink;

    // Mandatory empty constructor
    // for use of FirebaseUI
    public FoodTypeFirebaseModel(){
    }

    // Getter and setter methods
    public String getFoodName(){
        return foodName;
    }

    public void setFoodName(String foodName){
        this.foodName = foodName;
    }

    public String getImageLink(){
        return imageLink;
    }

    public void setImageLink(String imageLink){
        this.imageLink = imageLink;
    }
}

