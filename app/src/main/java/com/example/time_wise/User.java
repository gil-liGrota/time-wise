package com.example.time_wise;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class User {

    protected String phoneNumber;
    protected String userName;
    protected String password;
    protected ArrayList<EfficientTime> efficiency, unefficiency, sleep;
    protected ArrayList<Task> tasks;
    protected ArrayList<DayEfficiency> efficiencyHistory;
    protected ArrayList<Todo> todos;

    public User(){
        this.phoneNumber = "";
        this.userName = "";
        this.password = "";
        this.efficiency = null;
        this.unefficiency = null;
        this.sleep = null;
        this.tasks = null;
        this.efficiencyHistory = null;
        this.todos = null;
    }

    /**
     * @param phoneNumber - phone number
     * @param userName - user name
     * @param password - password
     * @param efficiency - efficiency hours during the day List
     * @param unefficiency - unefficiency hours during the day List
     * @param sleep - sleep hours during the day List
     * @param tasks - taske List
     * @param efficiencyHistory - efficiency history List
     * @param todos - todos List
     */
    public User(String phoneNumber,
                String userName,
                String password,
                ArrayList<EfficientTime> efficiency,
                ArrayList<EfficientTime> unefficiency,
                ArrayList<EfficientTime> sleep,
                ArrayList<Task> tasks,
                ArrayList<DayEfficiency> efficiencyHistory,
                ArrayList<Todo> todos){
        this.phoneNumber = phoneNumber;
        this.userName = userName;
        this.password = password;
        this.efficiency = efficiency;
        this.unefficiency = unefficiency;
        this.sleep = sleep;
        this.tasks = tasks;
        this.efficiencyHistory = efficiencyHistory;
        this.todos = todos;
    }

    public String getPhoneNumber() { return phoneNumber; }
    public String getUserName() { return userName; }
    public String getPassword() { return password; }
    public ArrayList<EfficientTime> getEfficiency() { return efficiency; }
    public ArrayList<EfficientTime> getUnefficiency() { return unefficiency; }
    public ArrayList<EfficientTime> getSleep() { return sleep; }
    public ArrayList<Task> getTasks() { return tasks; }
    public ArrayList<DayEfficiency> getEfficiencyHistory() { return efficiencyHistory; }
    public ArrayList<Todo> getTodos() { return todos; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setPassword(String password) { this.password = password; }
    public void setEfficiency(ArrayList<EfficientTime> efficiency) { this.efficiency = efficiency; }
    public void setUnefficiency(ArrayList<EfficientTime> unefficiency) { this.unefficiency = unefficiency; }
    public void setSleep(ArrayList<EfficientTime> sleep) { this.sleep = sleep; }
    public void setTasks(ArrayList<Task> tasks) { this.tasks = tasks; }
    public void setEfficiencyHistory(ArrayList<DayEfficiency> efficiencyHistory) { this.efficiencyHistory = efficiencyHistory; }
    public void setTodos(ArrayList<Todo> todos) { this.todos = todos; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("User Name: ").append(userName).append("\n");
        sb.append("Phone Number: ").append(phoneNumber).append("\n");
        sb.append("Password: ").append(password).append("\n");

        sb.append("Efficiency: ").append(efficiency != null ? efficiency.toString() : "[]").append("\n");
        sb.append("Unefficiency: ").append(unefficiency != null ? unefficiency.toString() : "[]").append("\n");
        sb.append("Sleep: ").append(sleep != null ? sleep.toString() : "[]").append("\n");

        sb.append("Tasks: ").append(tasks != null ? tasks.toString() : "[]").append("\n");
        sb.append("Efficiency History: ").append(efficiencyHistory != null ? efficiencyHistory.toString() : "[]").append("\n");
        sb.append("Todos: ").append(todos != null ? todos.toString() : "[]").append("\n");

        return sb.toString();
    }
}
