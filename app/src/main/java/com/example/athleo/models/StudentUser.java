package com.example.athleo.models;

public class StudentUser {
    private String email;
    private String uid;
    
    // Non-firestore fields:
    private boolean isPresent;

    public StudentUser() {}

    public StudentUser(String uid, String email) {
        this.uid = uid;
        this.email = email;
        this.isPresent = false;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public boolean isPresent() { return isPresent; }
    public void setPresent(boolean present) { isPresent = present; }
}
