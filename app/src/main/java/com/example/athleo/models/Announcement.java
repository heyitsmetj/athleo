package com.example.athleo.models;

public class Announcement {
    private String id;
    private String title;
    private String message;
    private String authorName;
    private long timestamp;

    public Announcement() {
    }

    public Announcement(String id, String title, String message, String authorName, long timestamp) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.authorName = authorName;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
