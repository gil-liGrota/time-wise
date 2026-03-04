package com.example.time_wise;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
        SecurityManager securityManager = new SecurityManager(this);
        userID = securityManager.getUserId();

        sideMenu = findViewById(R.id.sideMenu);
        btnMenu = findViewById(R.id.btnMenu);
        if (userID == null) {
            finish();
            return;
        }

        initViews();
        loadTasksFromFirebase();

        btnMenu.setOnClickListener(v -> {
            if (sideMenu.getVisibility() == View.GONE) {
                sideMenu.setVisibility(View.VISIBLE);
            } else {
                sideMenu.setVisibility(View.GONE);
            }
        });
        setMenuClickListener(R.id.nav_home, Constant.Menu.Home);
        setMenuClickListener(R.id.nav_activity, Constant.Menu.ACTIVITY);
        setMenuClickListener(R.id.nav_school_schedule, Constant.Menu.SCHOOL_SCHEDULE);
        setMenuClickListener(R.id.nav_follow_efficiency, Constant.Menu.FOLLOW_EFFICIENCY);
        setMenuClickListener(R.id.nav_todo, Constant.Menu.TODO);
        setMenuClickListener(R.id.nav_calendar, Constant.Menu.CALENDER);
        setMenuClickListener(R.id.nav_notes, Constant.Menu.NOTES);
        setMenuClickListener(R.id.nav_learning_plan, Constant.Menu.LERNING_PLAN);
        setMenuClickListener(R.id.nav_follow_goal, Constant.Menu.FOLLOW_GOAL);



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
                    // הצגת היום הנוכחי בטעינה
                    Calendar cal = Calendar.getInstance();
                    filterTasks(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
                }
            }
        });
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
    }

    // --- פונקציית עריכה (הופעלה עבור הלוח שנה) ---
    public void openEditTaskDialog(Task task, int position) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_task, null);

        EditText etName = dialogView.findViewById(R.id.etTaskName);
        EditText etDate = dialogView.findViewById(R.id.etTaskDate);
        // ... (ניתן להוסיף כאן את שאר ה-findViewById מה-Screem הקודם אם רוצים עריכה מלאה)

        etName.setText(task.getName());
        if (task.getDate() != null) {
            etDate.setText(String.format("%02d/%02d/%04d", task.getDate().getDay(), task.getDate().getMonth(), task.getDate().getYear()));
        }

        etDate.setOnClickListener(v -> showDatePicker(etDate));

        new AlertDialog.Builder(this)
                .setTitle("Edit Task")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    task.setName(etName.getText().toString().trim());

                    String dateStr = etDate.getText().toString();
                    if (!dateStr.isEmpty()) {
                        String[] parts = dateStr.split("/");
                        task.setDate(new Date(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]), Integer.parseInt(parts[0])));
                    }

                    syncTasksWithFirebase();
                    taskAdapter.notifyDataSetChanged();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // עדכון ה-DB עם הרשימה המלאה (allTasks)
    public void syncTasksWithFirebase() {
        db.collection("users").document(userID)
                .update("tasks", allTasks)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show());
    }

    private void showDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        new android.app.DatePickerDialog(this, (view, y, m, d) -> {
            editText.setText(String.format("%02d/%02d/%04d", d, m + 1, y));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private Task convertMapToTask(Map<String, Object> map) {
        try {
            Task task = new Task();
            task.setName((String) map.get("name"));
            Map<String, Object> dateMap = (Map<String, Object>) map.get("date");
            if (dateMap != null) {
                task.setDate(new Date(((Long) dateMap.get("year")).intValue(),
                        ((Long) dateMap.get("month")).intValue(),
                        ((Long) dateMap.get("day")).intValue()));
            }
            return task;
        } catch (Exception e) { return null; }
    }

    private void setMenuClickListener(int id, Constant.Menu menu) {
        TextView item = findViewById(id);

        item.setOnClickListener(v -> {
            switch (menu){
                case Home:
                    intent = new Intent(CalendarActivity.this, HomeScreen.class);
                    Toast.makeText(this, "home", Toast.LENGTH_SHORT).show();
                    break;

                case ACTIVITY:
                    intent = new Intent(CalendarActivity.this, TasksScreem.class);
                    break;

                case SCHOOL_SCHEDULE:
                    // אם אנחנו כבר במסך מערכת שעות, לא עושים כלום
                    intent = new Intent(CalendarActivity.this, SchoolSchedule.class);
                    break;

                case FOLLOW_EFFICIENCY:
                    intent = new Intent(CalendarActivity.this, HomeScreen.class);
                    Toast.makeText(this, "Follow Efficiency", Toast.LENGTH_SHORT).show();
                    break;

                case TODO:
                    intent = new Intent(CalendarActivity.this, Todos.class);
                    Toast.makeText(this, "To-Do", Toast.LENGTH_SHORT).show();
                    break;

                case CALENDER:
                    intent = new Intent(CalendarActivity.this, CalendarActivity.class);
                    Toast.makeText(this, "Calendar", Toast.LENGTH_SHORT).show();
                    break;

                case NOTES:
                    intent = new Intent(CalendarActivity.this, HomeScreen.class);
                    Toast.makeText(this, "Notes", Toast.LENGTH_SHORT).show();
                    break;

                case LERNING_PLAN:
                    intent = new Intent(CalendarActivity.this, HomeScreen.class);
                    Toast.makeText(this, "Learning Plan", Toast.LENGTH_SHORT).show();
                    break;

                case FOLLOW_GOAL:
                    intent = new Intent(CalendarActivity.this, HomeScreen.class);
                    Toast.makeText(this, "Follow Goal", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Toast.makeText(this, "not working", Toast.LENGTH_SHORT).show();
                    return;
            }

            intent.putExtra("userId", userID);
            startActivity(intent);
            sideMenu.setVisibility(View.GONE);
        });
    }
}