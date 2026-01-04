package com.example.time_wise;

import java.io.Serializable;

public class Lesson implements Serializable {
    private int hour;       // מספר השעה
    private String name;    // שם השיעור

    public Lesson(int hour, String name) {
        this.hour = hour;
        this.name = name;
    }

    // Getters
    public int getHour() { return hour; }
    public String getName() { return name; }

    // Setters
    public void setHour(int hour) { this.hour = hour; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return hour + ": " + name;
    }
}
