package com.example.athleo.models;

public class PerformanceReport {
    private String id;
    private String studentName;
    private String reportContent;
    private String authorName;
    private long timestamp;

    public PerformanceReport() {
    }

    public PerformanceReport(String id, String studentName, String reportContent, String authorName, long timestamp) {
        this.id = id;
        this.studentName = studentName;
        this.reportContent = reportContent;
        this.authorName = authorName;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getReportContent() { return reportContent; }
    public void setReportContent(String reportContent) { this.reportContent = reportContent; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
