package com.example.time_wise.schoolSchedule;

import java.io.Serializable;

public class Lesson implements Serializable {
    private int hour;
    private String name;

    public Lesson(int hour, String name) {
        this.hour = hour;
        this.name = name;
    }

    public int getHour() { return hour; }
    public String getName() { return name; }

    public void setHour(int hour) { this.hour = hour; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return hour + ": " + name;
    }
}
