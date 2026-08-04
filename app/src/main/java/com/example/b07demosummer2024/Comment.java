package com.example.b07demosummer2024;

public class Comment implements java.io.Serializable {
    private String commentId;
    private String userId;
    private String username;
    private String text;
    private long timestamp;

    public Comment() {}

    public Comment(String commentId, String userId, String username, String text, long timestamp) {
        this.commentId = commentId;
        this.userId = userId;
        this.username = username;
        this.text = text;
        this.timestamp = timestamp;
    }

    //getters
    public String getCommentId() {
        return commentId;
    }
    public String getUserId() {
        return userId;
    }
    public String getUsername() {
        return username;
    }
    public String getText() {
        return text;
    }
    public long getTimestamp() {
        return timestamp;
    }

    //setters
    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setText(String text) {
        this.text = text;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
