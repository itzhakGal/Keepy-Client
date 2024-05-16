package com.example.keepy.helperClass;

public class KindergartenDetailsHelperClass {
    String kindergartenName;
    public KindergartenDetailsHelperClass(String kindergartenName, String password) {
        this.kindergartenName = kindergartenName;
        this.password = password;
    }

    public KindergartenDetailsHelperClass() {
        // Default constructor required for Firebase deserialization
    }

    public String getKindergartenName() {
        return kindergartenName;
    }

    public void setKindergartenName(String kindergartenName) {
        this.kindergartenName = kindergartenName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    String password;



}
