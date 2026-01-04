package com.example.time_wise;

import java.io.Serializable;
import java.util.ArrayList;

public class SchoolDay implements Serializable {
    private String dayName;
    private ArrayList<Lesson> lessons;

    public SchoolDay(String dayName, ArrayList<Lesson> lessons) {
        this.dayName = dayName;
        this.lessons = lessons;
    }

    public String getDayName() { return dayName; }
    public ArrayList<Lesson> getLessons() { return lessons; }

    @Override
    public String toString() {
        return dayName + ": " + lessons.toString();
    }
}
