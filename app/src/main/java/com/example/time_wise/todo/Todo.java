package com.example.time_wise.todo;

public class Todo {
    protected String name;
    protected boolean done;

    public Todo(){
        this.name = "";
        this.done = false;
    }

    /**
     * @param name - task name
     * @param done - is the task done
     */
    public  Todo(String name, boolean done){
        this.name = name;
        this.done = done;
    }

    public boolean isDone() { return done; }
    public String getName() { return name; }
    public void setDone(boolean done) { this.done = done; }
    public void setName(String name) { this.name = name; }
}
