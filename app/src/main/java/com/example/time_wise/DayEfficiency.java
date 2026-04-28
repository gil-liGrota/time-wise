package com.example.time_wise;

public class DayEfficiency {
    protected int midDay;
    protected Date date; // שימוש במחלקה שלך

    public DayEfficiency() {}

    public DayEfficiency(int midDay, Date date) {
        this.midDay = midDay;
        this.date = date;
    }

    public void setMidDay(int midDay) { this.midDay = midDay; }
    public int getMidDay() { return midDay; }

    public void setDate(Date date) { this.date = date; }
    public Date getDate() { return date; }

    @Override
    public String toString() {
        return date.getDay() + "/" + date.getMonth() + "/" + date.getYear() + " - Score: " + midDay;
    }
}