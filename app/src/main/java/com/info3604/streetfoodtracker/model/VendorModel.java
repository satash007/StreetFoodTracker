package com.info3604.streetfoodtracker.model;

public class VendorModel {

    public String name, address, lat, lng, rating, type;

    public VendorModel(String name, String address,String lat, String lng, String rating, String type) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.rating = rating;
        this.type = type;
    }

    @Override
    public String toString() {
        return "VendorModel{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", rating='" + rating + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}