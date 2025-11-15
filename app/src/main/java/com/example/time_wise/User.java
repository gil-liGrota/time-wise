package com.example.time_wise;

import java.util.List;

public class User {

    protected String phoneNumber;
    protected String userName;
    protected String password;
    protected List<EfficientTime> efficiency, unefficiency, sleep;
    protected List<Task> tasks;
    protected List<DayEfficiency> efficiencyHistory;
    protected List<Todo> todos;

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
                List<EfficientTime> efficiency,
                List<EfficientTime> unefficiency,
                List<EfficientTime> sleep,
                List<Task> tasks,
                List<DayEfficiency> efficiencyHistory,
                List<Todo> todos){
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
    public List<EfficientTime> getEfficiency() { return efficiency; }
    public List<EfficientTime> getUnefficiency() { return unefficiency; }
    public List<EfficientTime> getSleep() { return sleep; }
    public List<Task> getTasks() { return tasks; }
    public List<DayEfficiency> getEfficiencyHistory() { return efficiencyHistory; }
    public List<Todo> getTodos() { return todos; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setPassword(String password) { this.password = password; }
    public void setEfficiency(List<EfficientTime> efficiency) { this.efficiency = efficiency; }
    public void setUnefficiency(List<EfficientTime> unefficiency) { this.unefficiency = unefficiency; }
    public void setSleep(List<EfficientTime> sleep) { this.sleep = sleep; }
    public void setTasks(List<Task> tasks) { this.tasks = tasks; }
    public void setEfficiencyHistory(List<DayEfficiency> efficiencyHistory) { this.efficiencyHistory = efficiencyHistory; }
    public void setTodos(List<Todo> todos) { this.todos = todos; }
}
