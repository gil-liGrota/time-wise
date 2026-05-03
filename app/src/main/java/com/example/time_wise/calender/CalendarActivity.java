package com.example.time_wise.calender;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.time_wise.Constant;
import com.example.time_wise.Date;
import com.example.time_wise.R;
import com.example.time_wise.enterApp.HomeScreen;
import com.example.time_wise.enterApp.log_in;
import com.example.time_wise.followEfficiency.EfficiencyActivity;
import com.example.time_wise.followGoal.GoalsActivity;
import com.example.time_wise.notes.NotesActivity;
import com.example.time_wise.schoolSchedule.SchoolSchedule;
import com.example.time_wise.task.Task;
import com.example.time_wise.task.TaskAdapter;
import com.example.time_wise.task.TasksScreen;
import com.example.time_wise.todo.Todos;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class CalendarActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private ListView lvTasks;
    private TextView tvDate, btnMenu;
    private LinearLayout sideMenu;
    private Intent intent;

    private ArrayList<Task> allTasks = new ArrayList<>();
    private ArrayList<Task> filteredTasks = new ArrayList<>();
    private TaskAdapter taskAdapter;
    private String userID;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        db = FirebaseFirestore.getInstance();
        userID = getIntent().getStringExtra("userId");

        if (userID == null) {
            finish();
            return;
        }

        initViews();
        loadTasksFromFirebase();

        btnMenu.setOnClickListener(v -> {
            sideMenu.setVisibility(sideMenu.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        });

        setMenuClickListener(R.id.nav_home, Constant.Menu.Home);
        setMenuClickListener(R.id.nav_activity, Constant.Menu.ACTIVITY);
        setMenuClickListener(R.id.nav_school_schedule, Constant.Menu.SCHOOL_SCHEDULE);
        setMenuClickListener(R.id.nav_follow_efficiency, Constant.Menu.FOLLOW_EFFICIENCY);
        setMenuClickListener(R.id.nav_todo, Constant.Menu.TODO);
        setMenuClickListener(R.id.nav_calendar, Constant.Menu.CALENDER);
        setMenuClickListener(R.id.nav_notes, Constant.Menu.NOTES);
        setMenuClickListener(R.id.nav_follow_goal, Constant.Menu.FOLLOW_GOAL);
        setMenuClickListener(R.id.nav_sign_out, Constant.Menu.SIGN_OUT);


    }

    private void initViews() {
        calendarView = findViewById(R.id.calendarView);
        lvTasks = findViewById(R.id.lvTasksForDate);
        tvDate = findViewById(R.id.tvSelectedDate);
        btnMenu = findViewById(R.id.btnMenu);
        sideMenu = findViewById(R.id.sideMenu);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            int correctedMonth = month + 1;
            tvDate.setText("Tasks for: " + dayOfMonth + "/" + correctedMonth + "/" + year);
            filterTasks(dayOfMonth, correctedMonth, year);
        });
    }

    private void loadTasksFromFirebase() {
        db.collection("users").document(userID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<Map<String, Object>> taskMaps = (List<Map<String, Object>>) documentSnapshot.get("tasks");
                if (taskMaps != null) {
                    allTasks.clear();
                    for (Map<String, Object> map : taskMaps) {
                        Task task = convertMapToTask(map);
                        if (task != null) allTasks.add(task);
                    }
                    Calendar cal = Calendar.getInstance();
                    filterTasks(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
                }
            }
        });
    }

    private Task convertMapToTask(Map<String, Object> map) {
        try {
            Task task = new Task();
            task.setName((String) map.get("name"));

            Map<String, Object> dateMap = (Map<String, Object>) map.get("date");
            if (dateMap != null) {
                task.setDate(new Date(
                        ((Long) dateMap.get("year")).intValue(),
                        ((Long) dateMap.get("month")).intValue(),
                        ((Long) dateMap.get("day")).intValue()));
            }

            if (map.get("start") != null) {
                Map<String, Object> s = (Map<String, Object>) map.get("start");
                task.setStart(LocalTime.of(((Long) s.get("hour")).intValue(), ((Long) s.get("minute")).intValue()));
            }
            if (map.get("end") != null) {
                Map<String, Object> e = (Map<String, Object>) map.get("end");
                task.setEnd(LocalTime.of(((Long) e.get("hour")).intValue(), ((Long) e.get("minute")).intValue()));
            }

            if (map.get("priority") != null) task.setPriority(((Long) map.get("priority")).intValue());
            if (map.get("important") != null) task.setImportant((Boolean) map.get("important"));
            if (map.get("strict") != null) task.setStrict((Boolean) map.get("strict"));

            return task;
        } catch (Exception e) { return null; }
    }

    private void filterTasks(int day, int month, int year) {
        filteredTasks.clear();
        for (Task task : allTasks) {
            Date d = task.getDate();
            if (d != null && d.getDay() == day && d.getMonth() == month && d.getYear() == year) {
                filteredTasks.add(task);
            }
        }
        if (taskAdapter == null) {
            taskAdapter = new TaskAdapter(this, filteredTasks, userID);
            lvTasks.setAdapter(taskAdapter);
        } else {
            taskAdapter.notifyDataSetChanged();
        }

        ArrayAdapter<Task> adapter = new ArrayAdapter<Task>(this, android.R.layout.simple_list_item_1, filteredTasks) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);

                Task task = getItem(position);

                if (task != null && textView != null) {
                    textView.setText(task.getName());

                    if(task.isStrict()){
                        textView.setTextColor(Color.parseColor("#EF4444")); //RED
                    } else {
                        switch (task.getPriority()) {
                            case 5:
                                textView.setTextColor(Color.parseColor("#F97316"));
                                break;
                            case 4:
                                textView.setTextColor(Color.parseColor("#F59E0B"));
                                break;
                            case 3:
                                textView.setTextColor(Color.parseColor("#EAB308"));
                                break;
                            case 2:
                                textView.setTextColor(Color.parseColor("#84CC16"));
                                break;
                            case 1:
                                textView.setTextColor(Color.parseColor("#22C55E")); //GREEN
                                break;
                            default:
                                textView.setTextColor(Color.BLACK);
                        }
                    }
                }
                return view;
            }
        };

        lvTasks.setAdapter(adapter);
    }

    public void openEditTaskDialog(Task task, int position) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_task, null);

        EditText etName = dialogView.findViewById(R.id.etTaskName);
        EditText etStart = dialogView.findViewById(R.id.etTaskStart);
        EditText etEnd = dialogView.findViewById(R.id.etTaskEnd);
        EditText etDate = dialogView.findViewById(R.id.etTaskDate);
        EditText etPriority = dialogView.findViewById(R.id.etPriority);
        Switch switchHasTime = dialogView.findViewById(R.id.switchHasTime);

        etName.setText(task.getName());
        etStart.setText(task.getStart() != null ? task.getStart().toString() : "");
        etEnd.setText(task.getEnd() != null ? task.getEnd().toString() : "");

        if (task.getDate() != null) {
            etDate.setText(String.format("%02d/%02d/%04d", task.getDate().getDay(), task.getDate().getMonth(), task.getDate().getYear()));
        }
        etPriority.setText(String.valueOf(task.getPriority()));

        switchHasTime.setChecked(task.getStart() != null);
        etStart.setVisibility(switchHasTime.isChecked() ? View.VISIBLE : View.GONE);
        etEnd.setVisibility(switchHasTime.isChecked() ? View.VISIBLE : View.GONE);

        switchHasTime.setOnCheckedChangeListener((v, isChecked) -> {
            etStart.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            etEnd.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        etDate.setOnClickListener(v -> showDatePicker(etDate));
        etStart.setOnClickListener(v -> showTimePicker(etStart));
        etEnd.setOnClickListener(v -> showTimePicker(etEnd));

        new AlertDialog.Builder(this)
                .setTitle("Edit Task")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    task.setName(etName.getText().toString().trim());
                    try {
                        if (switchHasTime.isChecked()) {
                            task.setStart(LocalTime.parse(etStart.getText().toString()));
                            task.setEnd(LocalTime.parse(etEnd.getText().toString()));
                        } else {
                            task.setStart(null);
                            task.setEnd(null);
                        }
                    } catch (Exception ignored) {}

                    syncTasksWithFirebase();
                    taskAdapter.notifyDataSetChanged();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void deleteTaskFromCalendar(Task task) {
        allTasks.remove(task);
        filteredTasks.remove(task);
        syncTasksWithFirebase();
        taskAdapter.notifyDataSetChanged();
    }

    public void syncTasksWithFirebase() {
        db.collection("users").document(userID)
                .update("tasks", allTasks);
    }

    private void showDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        new android.app.DatePickerDialog(this, (view, y, m, d) -> {
            editText.setText(String.format("%02d/%02d/%04d", d, m + 1, y));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker(EditText editText) {
        new android.app.TimePickerDialog(this, (view, h, m) -> {
            editText.setText(String.format("%02d:%02d", h, m));
        }, 9, 0, true).show();
    }

    private void setMenuClickListener(int id, Constant.Menu menu) {
        TextView item = findViewById(id);

        item.setOnClickListener(v -> {
            switch (menu){
                case Home: intent = new Intent(CalendarActivity.this, HomeScreen.class);break;
                case ACTIVITY: intent = new Intent(CalendarActivity.this, TasksScreen.class);break;
                case SCHOOL_SCHEDULE: intent = new Intent(CalendarActivity.this, SchoolSchedule.class);break;
                case FOLLOW_EFFICIENCY: intent = new Intent(CalendarActivity.this, EfficiencyActivity.class);break;
                case TODO: intent = new Intent(CalendarActivity.this, Todos.class);break;
                case CALENDER: intent = new Intent(CalendarActivity.this, CalendarActivity.class);break;
                case NOTES: intent = new Intent(CalendarActivity.this, NotesActivity.class);break;
                case FOLLOW_GOAL: intent = new Intent(CalendarActivity.this, GoalsActivity.class);break;
                case SIGN_OUT: intent = new Intent(CalendarActivity.this, log_in.class);break;
                default:
                    intent = new Intent(CalendarActivity.this, HomeScreen.class);
                    break;
            }
            intent.putExtra("userId", userID);
            startActivity(intent);
            sideMenu.setVisibility(View.GONE);
        });
    }

}