package com.example.time_wise;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;

public class EfficientTime implements Serializable {
    protected DayOfWeek day;
    protected LocalTime start;
    protected LocalTime end;

    public EfficientTime(){
        this.day = null;
        this.start = null;
        this.end = null;
    }

    /**
     * @param day - day
     * @param start - start time
     * @param end - end time
     */
    public EfficientTime(DayOfWeek day, LocalTime start, LocalTime end){
        this.day = day;
        this.start = start;
        this.end = end;
    }

    public void setDay(DayOfWeek day) { this.day = day; }
    public void setStart(LocalTime start) { this.start = start; }
    public void setEnd(LocalTime end) { this.end = end; }
    public DayOfWeek getDay() { return day; }
    public LocalTime getStart() { return start; }
    public LocalTime getEnd() { return end; }

    @Override
    public String toString() {
        return "Day: " + day +
                ", Start: " + (start != null ? start.toString() : "null") +
                ", End: " + (end != null ? end.toString() : "null");
    }
}
