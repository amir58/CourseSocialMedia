package com.amirmohammed.coursesocialmedia;

public class Post {

    private String userId, username, userProfile, content;

    public Post() {
    }

    public Post(String userId, String username, String userProfile, String content) {
        this.userId = userId;
        this.username = username;
        this.userProfile = userProfile;
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
