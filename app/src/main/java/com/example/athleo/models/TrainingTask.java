package com.example.athleo.models;

public class TrainingTask {
    private String id;
    private String title;
    private String description;
    private String assignedByUid;
    private String assignedByRole;
    private String assignedToUid;
    private String deadline;
    private String status; // "Pending", "Submitted", "Approved", "Rejected"
    private String submissionUrl;

    public TrainingTask() {}

    public TrainingTask(String id, String title, String description, String assignedByUid, String assignedByRole, String assignedToUid, String deadline, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.assignedByUid = assignedByUid;
        this.assignedByRole = assignedByRole;
        this.assignedToUid = assignedToUid;
        this.deadline = deadline;
        this.status = status;
        this.submissionUrl = "";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getAssignedByUid() { return assignedByUid; }
    public void setAssignedByUid(String assignedByUid) { this.assignedByUid = assignedByUid; }

    public String getAssignedByRole() { return assignedByRole; }
    public void setAssignedByRole(String assignedByRole) { this.assignedByRole = assignedByRole; }

    public String getAssignedToUid() { return assignedToUid; }
    public void setAssignedToUid(String assignedToUid) { this.assignedToUid = assignedToUid; }
    
    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSubmissionUrl() { return submissionUrl; }
    public void setSubmissionUrl(String submissionUrl) { this.submissionUrl = submissionUrl; }
}
