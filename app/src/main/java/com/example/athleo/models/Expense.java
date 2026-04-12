package com.example.athleo.models;

public class Expense {
    private String id;
    private String description;
    private double amount;
    private String category;
    private long timestamp;
    private String status;

    public Expense() {
    }

    public Expense(String id, String description, double amount, String category, long timestamp) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
