package com.example.athleo.models;

public class MatchSchedule {
    private String id;
    private String title;
    private String date;
    private String time;
    private String location;
    private String opponent;

    public MatchSchedule() {
        // Required for Firestore
    }

    public MatchSchedule(String id, String title, String date, String time, String location, String opponent) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.location = location;
        this.opponent = opponent;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getOpponent() { return opponent; }
    public void setOpponent(String opponent) { this.opponent = opponent; }
}
