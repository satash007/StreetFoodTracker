package com.info3604.streetfoodtracker.model;

public class User {
    private String uID, name, email, password, accountType, phoneNumber, address, img;
    private Double latitude, longitude;

    //Default empty constructor
    public User(){}

    public User(String uID, String name, String email, String password, String accountType, String phoneNumber, String address, Double latitude, Double longitude, String img) {
        this.uID = uID;
        this.name = name;
        this.email = email;
        this.password = password;
        this.accountType = accountType;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.img = img;
    }


    public String getUID() {
        return uID;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getAddress() {
        return address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getImg() {
        return img;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setUID(String uID) {
        this.uID = uID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public void setImg(String img) {
        this.img = img;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

}
