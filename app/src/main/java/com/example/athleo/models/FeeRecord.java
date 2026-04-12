package com.example.athleo.models;

public class FeeRecord {
    private String id;
    private String studentId;
    private String studentName;
    private double amount;
    private String dueDate;
    private String status; // "Paid", "Pending"

    public FeeRecord() {
        // Required empty constructor for Firestore
    }

    public FeeRecord(String id, String studentId, String studentName, double amount, String dueDate, String status) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.amount = amount;
        this.dueDate = dueDate;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
