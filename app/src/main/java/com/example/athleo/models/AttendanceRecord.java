package com.example.athleo.models;

import java.util.List;

public class AttendanceRecord {
    private String dateId; // YYYY-MM-DD format
    private String markedByUid;
    private long timestamp;
    private List<String> presentStudentUids;

    public AttendanceRecord() {}

    public AttendanceRecord(String dateId, String markedByUid, long timestamp, List<String> presentStudentUids) {
        this.dateId = dateId;
        this.markedByUid = markedByUid;
        this.timestamp = timestamp;
        this.presentStudentUids = presentStudentUids;
    }

    public String getDateId() { return dateId; }
    public void setDateId(String dateId) { this.dateId = dateId; }

    public String getMarkedByUid() { return markedByUid; }
    public void setMarkedByUid(String markedByUid) { this.markedByUid = markedByUid; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public List<String> getPresentStudentUids() { return presentStudentUids; }
    public void setPresentStudentUids(List<String> presentStudentUids) { this.presentStudentUids = presentStudentUids; }
}
