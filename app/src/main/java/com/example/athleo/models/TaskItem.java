package com.example.athleo.models;

public class TaskItem {
    private String id;
    private String title;
    private String description;
    private String status;
    private int completion;
    private String category;
    private String submissionUrl;
    private String assignedTo;
    private int score;
    private String coachFeedback;
    private long timestamp;
    private String deadline;

    public TaskItem() {
    }

    public TaskItem(String id, String title, String description, String status, int completion) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.completion = completion;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getCompletion() { return completion; }
    public void setCompletion(int completion) { this.completion = completion; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSubmissionUrl() { return submissionUrl; }
    public void setSubmissionUrl(String submissionUrl) { this.submissionUrl = submissionUrl; }

    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getCoachFeedback() { return coachFeedback; }
    public void setCoachFeedback(String coachFeedback) { this.coachFeedback = coachFeedback; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
}
