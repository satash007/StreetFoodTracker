package com.info3604.streetfoodtracker;

public class VendorModel {

    public String name, address;
    public String lat,lng;
    String rating;

    //Default empty constructor
    public VendorModel(){

    }

    //TODO
    public VendorModel(String name, String address, String lat, String lng, String rating) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.rating = rating;
    }
    //TODO

    @Override
    public String toString() {
        return "VendorModel{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", rating='" + rating + '\'' +
                '}';
    }
     // Acessors and mutator

    // Accessor: getName()-> Name of the Vendor Business Place Location
    public String getName() {
        return name;
    }

    // Accessor: getAddress()=> Address of the Vendor Business Place
    public String getAdress(){ return address;}

    public String getLatitude() {
        return lat;
    }

    public String getLongitude() {
        return lng;
    }

    public String getRating(){ return rating;}

    public void setName(String name) {
        this.name = name;
    }

    public void settLatitude(Double latitude) {
        this.lat = lat;
    }

    public void setLongitude(Double longitude) {
        this.lng = lng;
    }

    public void setRating(Double longitude) {
        this.lng = lng;
    }


}