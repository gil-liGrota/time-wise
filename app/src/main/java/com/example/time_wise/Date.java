package com.example.time_wise;

import androidx.annotation.NonNull;

public class Date implements java.io.Serializable{
    protected int year;
    protected int month;
    protected int day;

    public Date(){}

    /**
     * @param year - year
     * @param month - month
     * @param day - day
     */
    public  Date(int year, int month, int day){
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getYear(){ return  year; }
    public int getMonth(){ return  month; }
    public int getDay(){ return  day; }
    public void setDay(int day) { this.day = day; }
    public void setMonth(int month) { this.month = month; }
    public void setYear(int year) { this.year = year; }

}
