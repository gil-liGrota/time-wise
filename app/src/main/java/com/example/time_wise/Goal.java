package com.example.time_wise;

import java.io.Serializable;

public class Goal implements Serializable {
    private String id;
    private String title;
    private String note;
    private boolean isDaily;
    private Date targetDate;
    private Date lastCheckedDate;
    private boolean isCompleted;

    public Goal() {}

    public Goal(String id, String title, String note, boolean isDaily, Date targetDate) {
        this.id = id;
        this.title = title;
        this.note = note;
        this.isDaily = isDaily;
        this.targetDate = targetDate;
        this.isCompleted = false;
    }

    // Getters & Setters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getNote() { return note; }
    public boolean isDaily() { return isDaily; }
    public Date getTargetDate() { return targetDate; }
    public Date getLastCheckedDate() { return lastCheckedDate; }
    public void setLastCheckedDate(Date lastCheckedDate) { this.lastCheckedDate = lastCheckedDate; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    @Override
    public String toString() {
        return this.title;
    }
}