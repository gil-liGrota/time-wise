package com.example.time_wise;

import androidx.annotation.NonNull;

public class Topic {
    protected String name;
//    protected String userId;

    public Topic(){
        this.name = "";
    }

    /**
     *
     * @param name - topic name
     */
    public Topic(String name){
        this.name = name;
//        this.userId = userId;
    }

    public String getName() { return this.name; }
//    public String getUserId() { return  this.userId; }
    public void setName(String name) { this.name = name; }
//    public void setUserId(String userId) { this.userId = userId; }

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }
}
