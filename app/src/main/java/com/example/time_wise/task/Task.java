package com.example.time_wise.task;

import androidx.annotation.NonNull;

import com.example.time_wise.Constant;
import com.example.time_wise.Date;

import java.time.LocalTime;

public class Task {
    protected String name;
    protected Topic topic;
    protected Date date;
    protected Constant.RepeatType type;
    protected LocalTime start, end;
    protected boolean isImportant;
    protected int priority;
    protected boolean strict;

    public Task(){}

    /**
     * @param name - name of task
     * @param topic - topic
     * @param date - date
     * @param type - repeat type
     * @param start - start time
     * @param end - end time
     * @param isImportant - is important
     * @param priority - priority, if isImportant true priority = 5
     * @param strict - the task can't be move
     */
    public Task(String name,
                Topic topic,
                Date date,
                Constant.RepeatType type,
                LocalTime start,
                LocalTime end,
                boolean isImportant,
                int priority,
                boolean strict){
        this.name = name;
        this.topic = topic;
        this.date = date;
        this.type = type;
        this.start = start;
        this.end = end;
        this.isImportant = isImportant;
        this.priority = priority;
        this.strict = strict;
    }

    public String getName() { return name; }
    public Topic getTopic() { return topic; }
    public Date getDate() { return date; }
    public Constant.RepeatType getType() { return type; }
    public LocalTime getStart() { return start; }
    public LocalTime getEnd() { return end; }
    public boolean isImportant() { return isImportant; }
    public int getPriority() { return priority; }
    public boolean isStrict() { return strict; }

    public void setName(String name) { this.name = name; }
    public void setTopic(Topic topic) { this.topic = topic; }
    public void setDate(Date date) { this.date = date; }
    public void setType(Constant.RepeatType type) { this.type = type; }
    public void setStart(LocalTime start) { this.start = start; }
    public void setEnd(LocalTime end) { this.end = end; }
    public void setImportant(boolean important) { isImportant = important; }
    public void setPriority(int priority) { this.priority = priority; }
    public void setStrict(boolean strict) { this.strict = strict;}

    @NonNull
    @Override
    public String toString() {
        return this.name + ", " + this.topic + ", " + this.date.getDay() + "/" + this.date.getMonth()
                + "/" + this.date.getYear() + ", start: " + this.start + ", end: " + this.end +
                ", importent? " + this.isImportant + ", priority: " + this.priority +
                ", strict? " + this.strict;
    }
}
