package com.example.time_wise;

public class StudyTopic {
    private String topicName;
    private int difficulty;
    private int allocatedMinutes;

    public StudyTopic() {}

    public StudyTopic(String topicName, int difficulty) {
        this.topicName = topicName;
        this.difficulty = difficulty;
    }

    // Getters & Setters
    public String getTopicName() { return topicName; }
    public void setTopicName(String topicName) { this.topicName = topicName; }
    public int getDifficulty() { return difficulty; }
    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }
    public int getAllocatedMinutes() { return allocatedMinutes; }
    public void setAllocatedMinutes(int allocatedMinutes) { this.allocatedMinutes = allocatedMinutes; }
}