package com.example.keepy;

public class HelperClass {
    String fullName, gardenName, phoneNumber, password;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGardenName() {
        return gardenName;
    }

    public void setGardenName(String gardenName) {
        this.gardenName = gardenName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public HelperClass(String fullName, String gardenName, String phoneNumber, String password) {
        this.fullName = fullName;
        this.gardenName = gardenName;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }
    public HelperClass() {
    }
}