package com.example.time_wise.users;

import androidx.annotation.NonNull;

import com.example.time_wise.followEfficiency.DayEfficiency;
import com.example.time_wise.followGoal.Goal;
import com.example.time_wise.schoolSchedule.SchoolDay;
import com.example.time_wise.task.Task;
import com.example.time_wise.todo.Todo;

import java.util.ArrayList;

public class User {

    protected String phoneNumber;
    protected String userName;
    protected String password;
    protected ArrayList<Task> tasks;
    protected ArrayList<DayEfficiency> efficiencyHistory;
    protected ArrayList<Todo> todos;
    protected ArrayList<SchoolDay> schoolSchedule;
    protected ArrayList<Goal> goals;

    public User(){}

    /**
     * @param phoneNumber - phone number
     * @param userName - user name
     * @param password - password
     * @param tasks - task List
     * @param efficiencyHistory - efficiency history List
     * @param todos - todos List
     * @param schoolSchedule - school schedule List
     *
     */
    public User(String phoneNumber,
                String userName,
                String password,
                ArrayList<Task> tasks,
                ArrayList<DayEfficiency> efficiencyHistory,
                ArrayList<Todo> todos,
                ArrayList<SchoolDay> schoolSchedule,
                ArrayList<Goal> goals){
        this.phoneNumber = phoneNumber;
        this.userName = userName;
        this.password = password;
        this.tasks = tasks;
        this.efficiencyHistory = efficiencyHistory;
        this.todos = todos;
        this.schoolSchedule = schoolSchedule;
        this.goals = goals;
    }

    public String getPhoneNumber() { return phoneNumber; }
    public String getUserName() { return userName; }
    public String getPassword() { return password; }
    public ArrayList<Task> getTasks() { return tasks; }
    public ArrayList<DayEfficiency> getEfficiencyHistory() { return efficiencyHistory; }
    public ArrayList<Todo> getTodos() { return todos; }
    public ArrayList<SchoolDay> getSchoolSchedule() { return schoolSchedule; }
    public ArrayList<Goal> getGoals() { return goals; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setPassword(String password) { this.password = password; }
    public void setTasks(ArrayList<Task> tasks) { this.tasks = tasks; }
    public void setEfficiencyHistory(ArrayList<DayEfficiency> efficiencyHistory) { this.efficiencyHistory = efficiencyHistory; }
    public void setTodos(ArrayList<Todo> todos) { this.todos = todos; }
    public void setSchoolSchedule(ArrayList<SchoolDay> schoolSchedule) { this.schoolSchedule = schoolSchedule; }
    public void setGoals(ArrayList<Goal> goals) { this.goals = goals; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("User Name: ").append(userName).append("\n");
        sb.append("Phone Number: ").append(phoneNumber).append("\n");
        sb.append("Password: ").append(password).append("\n");

        sb.append("Tasks: ").append(tasks != null ? tasks.toString() : "[]").append("\n");
        sb.append("Efficiency History: ").append(efficiencyHistory != null ? efficiencyHistory.toString() : "[]").append("\n");
        sb.append("Todos: ").append(todos != null ? todos.toString() : "[]").append("\n");
        sb.append("School Schedule: ").append(schoolSchedule != null ? schoolSchedule.toString() : "[]").append("\n");

        return sb.toString();
    }
}
