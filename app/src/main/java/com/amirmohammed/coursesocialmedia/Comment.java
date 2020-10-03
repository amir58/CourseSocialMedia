package com.amirmohammed.coursesocialmedia;

public class Comment {
    private String id, uid, content, username, profileUrl;

    public Comment(String id, String uid, String content, String username, String profileUrl) {
        this.id = id;
        this.uid = uid;
        this.content = content;
        this.username = username;
        this.profileUrl = profileUrl;
    }

    public Comment() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id='" + id + '\'' +
                ", uid='" + uid + '\'' +
                ", content='" + content + '\'' +
                ", username='" + username + '\'' +
                ", profileUrl='" + profileUrl + '\'' +
                '}';
    }
}
