package com.amirmohammed.coursesocialmedia;

import java.util.List;

public class Post {

    private String postId, userId, username, userProfile, content;

    private List<Likes> likes;

    public Post() {
    }

    public Post(String postId, String userId, String username, String userProfile, String content, List<Likes> likes) {
        this.postId = postId;
        this.userId = userId;
        this.username = username;
        this.userProfile = userProfile;
        this.content = content;
        this.likes = likes;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
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

    public List<Likes> getLikes() {
        return likes;
    }

    public void setLikes(List<Likes> likes) {
        this.likes = likes;
    }
}
