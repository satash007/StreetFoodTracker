package com.info3604.streetfoodtracker.model;

public class VendorModel {

    public String name, address, lat, lng, rating, type, distance;

    //Constructor with parameters
    public VendorModel(String name, String address,String lat, String lng, String rating, String type, String distance) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.rating = rating;
        this.type = type;
        this.distance = distance;
    }

    //Getter methods
    public String getName(){
        return name;
    }

    public String getAddress(){
        return address;
    }

    public String getLatitude(){
        return lat;
    }

    public String getLongitude(){
        return lng;
    }

    public String getType(){
        return type;
    }

    public String getDistance(){
        return distance;
    }

    public String getRating(){
        return rating;
    }

    //Setter methods
    public void setName(String name){
        this.name = name;
    }

    public void setAddress(){
       this.address = address;
    }

    public void setLatitude(){
        this.lat = lat;
    }

    public void setLongitude(){
        this.lng = lng;
    }

    public void setType(){
        this.type = type;
    }

    public void setDistance(){
        this.distance = distance;
    }

    public void setRating(){
       this.rating = rating;
    }

    @Override
    public String toString() {
        return "VendorModel{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", distance='" + distance + '\'' +
                ", rating='" + rating + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}