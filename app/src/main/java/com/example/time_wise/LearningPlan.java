package com.example.time_wise;

import java.util.List;

public class LearningPlan {
    private String targetDate;
    private int totalStudyHours;
    private int maxDailyStudyHours;
    private boolean studyInSchoolGaps;
    private List<StudyTopic> topics;
    private List<String> excludedDates;

    public LearningPlan() {}

    public LearningPlan(String targetDate, int totalStudyHours, int maxDailyStudyHours,
                        boolean studyInSchoolGaps, List<StudyTopic> topics, List<String> excludedDates) {
        this.targetDate = targetDate;
        this.totalStudyHours = totalStudyHours;
        this.maxDailyStudyHours = maxDailyStudyHours;
        this.studyInSchoolGaps = studyInSchoolGaps;
        this.topics = topics;
        this.excludedDates = excludedDates;
    }
    public String getTargetDate() { return targetDate; }
    public int getTotalStudyHours() { return totalStudyHours; }
    public int getMaxDailyStudyHours() { return maxDailyStudyHours; }
    public boolean isStudyInSchoolGaps() { return studyInSchoolGaps; }
    public List<StudyTopic> getTopics() { return topics; }
    public List<String> getExcludedDates() { return excludedDates; }

    public void setTargetDate(String targetDate) { this.targetDate = targetDate; }
    public void setTotalStudyHours(int totalStudyHours) { this.totalStudyHours = totalStudyHours; }
    public void setMaxDailyStudyHours(int maxDailyStudyHours) { this.maxDailyStudyHours = maxDailyStudyHours; }
    public void setStudyInSchoolGaps(boolean studyInSchoolGaps) { this.studyInSchoolGaps = studyInSchoolGaps; }
    public void setTopics(List<StudyTopic> topics) { this.topics = topics; }
    public void setExcludedDates(List<String> excludedDates) { this.excludedDates = excludedDates; }

}