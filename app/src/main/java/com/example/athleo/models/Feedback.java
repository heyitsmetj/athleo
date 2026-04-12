package com.example.athleo.models;

public class Feedback {
    private String id;
    private String studentName;
    private String studentUid;
    private String message;
    private long timestamp;

    public Feedback() {
    }

    public Feedback(String id, String studentName, String studentUid, String message, long timestamp) {
        this.id = id;
        this.studentName = studentName;
        this.studentUid = studentUid;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentUid() { return studentUid; }
    public void setStudentUid(String studentUid) { this.studentUid = studentUid; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
