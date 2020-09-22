package com.amirmohammed.coursesocialmedia;

public class User {
    private String username, phoneNumber, profileUrl;

    public User() {
    }

    public User(String username, String phoneNumber, String profileUrl) {
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.profileUrl = profileUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
