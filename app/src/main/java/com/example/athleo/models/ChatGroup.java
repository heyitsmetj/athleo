package com.example.athleo.models;

import java.util.ArrayList;
import java.util.List;

public class ChatGroup {
    private String id;
    private String name;
    private String type; // "default" or "custom"
    private String description;
    private List<String> adminIds;
    private List<String> memberIds;
    private long createdAt;

    public ChatGroup() {
        this.adminIds = new ArrayList<>();
        this.memberIds = new ArrayList<>();
    }

    public ChatGroup(String id, String name, String type) {
        this(id, name, "", type);
    }

    public ChatGroup(String id, String name, String description, String type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.adminIds = new ArrayList<>();
        this.memberIds = new ArrayList<>();
        this.createdAt = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public List<String> getAdminIds() { return adminIds; }
    public void setAdminIds(List<String> adminIds) { this.adminIds = adminIds; }

    public List<String> getMemberIds() { return memberIds; }
    public void setMemberIds(List<String> memberIds) { this.memberIds = memberIds; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
