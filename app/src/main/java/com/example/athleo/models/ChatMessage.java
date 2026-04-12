package com.example.athleo.models;

public class ChatMessage {
    private String id;
    private String senderId;
    private String senderName;
    private String role;
    private String text;
    private long timestamp;

    public ChatMessage() {}

    public ChatMessage(String id, String senderId, String senderName, String role, String text, long timestamp) {
        this.id = id;
        this.senderId = senderId;
        this.senderName = senderName;
        this.role = role;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
