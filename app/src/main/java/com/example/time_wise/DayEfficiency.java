package com.example.time_wise;

public class DayEfficiency {
    protected int midDay;
    protected int endOfTheDay;

    public DayEfficiency(){
        this.midDay = 0;
        this.endOfTheDay = 0;
    }

    /**
     * @param midDay - efficiency in the middel of the day
     * @param endOfTheDay - efficiency in the end of the day
     */
    public DayEfficiency(int  midDay, int endOfTheDay){
        this.midDay = midDay;
        this.endOfTheDay = endOfTheDay;
    }

    public void setMidDay(int midDay) { this.midDay = midDay; }
    public void setEndOfTheDay(int endOfTheDay) { this.endOfTheDay = endOfTheDay; }
    public int getMidDay() { return midDay; }
    public int getEndOfTheDay() { return endOfTheDay; }
}
