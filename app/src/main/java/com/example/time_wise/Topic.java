package com.example.time_wise;

public class Topic {
    protected String name;

    public Topic(){
        this.name = "";
    }

    /**
     *
     * @param name - topic name
     */
    public Topic(String name){
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
