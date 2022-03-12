package com.info3604.streetfoodtracker;

public class VendorModel {

    public String name, address;
    public String lat,lng;
    String rating;

    public VendorModel(String name, String address, String lat, String lng, String rating) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.rating = rating;
    }

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
}