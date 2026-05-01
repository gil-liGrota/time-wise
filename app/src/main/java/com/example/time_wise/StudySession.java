package com.example.time_wise;

public class StudySession {
    private String topicName;
    private String date;
    private String startTime;
    private String endTime;
    private boolean isCompleted;

    public StudySession() {}

    public StudySession(String topicName, String date, String startTime, String endTime) {
        this.topicName = topicName;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isCompleted = false;
    }

    public String getTopicName() { return topicName; }
    public String getDate() { return date; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public boolean isCompleted() { return isCompleted; }

    public void setTopicName(String topicName) { this.topicName = topicName; }
    public void setDate(String date) { this.date = date; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
}