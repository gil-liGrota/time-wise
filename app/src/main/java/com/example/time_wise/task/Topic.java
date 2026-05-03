package com.example.time_wise.task;

import androidx.annotation.NonNull;

public class Topic {
    protected String name;

    public Topic(){}

    /**
     *
     * @param name - topic name
     */
    public Topic(String name){
        this.name = name;
    }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }
}
