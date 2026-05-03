package com.example.time_wise.task;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.time_wise.Constant;
import com.example.time_wise.Date;
import com.example.time_wise.R;
import com.example.time_wise.calender.CalendarActivity;
import com.example.time_wise.followGoal.GoalsActivity;
import com.example.time_wise.enterApp.log_in;

import com.example.time_wise.enterApp.HomeScreen;
import com.example.time_wise.followEfficiency.EfficiencyActivity;
import com.example.time_wise.notes.NotesActivity;
import com.example.time_wise.schoolSchedule.SchoolSchedule;
import com.example.time_wise.todo.Todos;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Map;

public class TasksScreen extends AppCompatActivity {

    private ListView lvActivity;
    private ArrayList<Task> tasks;
    private String userID;
    private TaskAdapter taskAdapter;
    private Intent intent;
    private LinearLayout sideMenu;
    private TextView btnMenu;
    private String userName, password;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.tasks_screen);

        lvActivity = findViewById(R.id.activityList);
        tasks = new ArrayList<>();
        userID = getIntent().getStringExtra("userId");

        FloatingActionButton btnAddActivity = findViewById(R.id.btnAddActivity);
        btnAddActivity.setOnClickListener(v -> openAddTaskDialog());

        FloatingActionButton btnAddTopic = findViewById(R.id.btnAddTopic);
        btnAddTopic.setOnClickListener(v -> showAddTopicDirectDialog());

        loadTasks();

        Intent lastIntent = getIntent();
        userID = lastIntent.getStringExtra("userId");
        Constant.USER_ID = userID;
        userName = lastIntent.getStringExtra("username");
        password = lastIntent.getStringExtra("password");
        sideMenu = findViewById(R.id.sideMenu);
        btnMenu = findViewById(R.id.btnMenu);



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
        setMenuClickListener(R.id.nav_follow_goal, Constant.Menu.FOLLOW_GOAL);
        setMenuClickListener(R.id.nav_sign_out, Constant.Menu.SIGN_OUT);


    }

    private void setMenuClickListener(int id, Constant.Menu menu) {
        TextView item = findViewById(id);

        item.setOnClickListener(v -> {
            switch (menu){
                case Home: intent = new Intent(TasksScreen.this, HomeScreen.class);break;
                case ACTIVITY: intent = new Intent(TasksScreen.this, TasksScreen.class);break;
                case SCHOOL_SCHEDULE: intent = new Intent(TasksScreen.this, SchoolSchedule.class);break;
                case FOLLOW_EFFICIENCY: intent = new Intent(TasksScreen.this, EfficiencyActivity.class);break;
                case TODO: intent = new Intent(TasksScreen.this, Todos.class);break;
                case CALENDER: intent = new Intent(TasksScreen.this, CalendarActivity.class);break;
                case NOTES: intent = new Intent(TasksScreen.this, NotesActivity.class);break;
                case FOLLOW_GOAL: intent = new Intent(TasksScreen.this, GoalsActivity.class);break;
                case SIGN_OUT: intent = new Intent(TasksScreen.this, log_in.class);break;
                default:
                    intent = new Intent(TasksScreen.this, HomeScreen.class);
                    break;
            }
            intent.putExtra("userId", userID);
            startActivity(intent);
            sideMenu.setVisibility(View.GONE);
        });
    }

    private void loadTasks() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userID)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        ArrayList<Map<String, Object>> taskListFromDB =
                                (ArrayList<Map<String, Object>>) doc.get("tasks");

                        if (taskListFromDB != null) {
                            tasks.clear();
                            for (Map<String, Object> t : taskListFromDB) {
                                Task task = convertMapToTask(t); // שימוש בפונקציית המרה
                                if (task != null) tasks.add(task);
                            }
                        }
                        updateListView();
                    }
                });
    }

    private Task convertMapToTask(Map<String, Object> map) {
        try {
            Task task = new Task();
            task.setName(map.get("name") != null ? map.get("name").toString() : "No Name");

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

            return task;
        } catch (Exception e) {
            return null;
        }
    }

    private void updateListView() {
        if (taskAdapter == null) {
            taskAdapter = new TaskAdapter(this, tasks, userID);
            lvActivity.setAdapter(taskAdapter);
        } else {
            taskAdapter.notifyDataSetChanged();
        }
    }

    private void openAddTaskDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_task, null);

        EditText etName = dialogView.findViewById(R.id.etTaskName);
        EditText etStart = dialogView.findViewById(R.id.etTaskStart);
        EditText etEnd = dialogView.findViewById(R.id.etTaskEnd);
        EditText etDate = dialogView.findViewById(R.id.etTaskDate);
        EditText etPriority = dialogView.findViewById(R.id.etPriority);

        Spinner spinnerTopic = dialogView.findViewById(R.id.spinnerTopic);
        Spinner spinnerRepeat = dialogView.findViewById(R.id.spinnerRepeat);
        Spinner spinnerImportance = dialogView.findViewById(R.id.spinnerImportance);
        Spinner spinnerStrict = dialogView.findViewById(R.id.spinnerStrict);

        Switch switchHasTime = dialogView.findViewById(R.id.switchHasTime);

        etStart.setOnClickListener(v -> showTimePicker(etStart));
        etEnd.setOnClickListener(v -> showTimePicker(etEnd));
        etDate.setOnClickListener(v -> showDatePicker(etDate));

        loadUserTopics(spinnerTopic);

        Constant.RepeatType[] repeatTypes = Constant.RepeatType.values();
        String[] repeatTypeNames = new String[repeatTypes.length];
        for (int i = 0; i < repeatTypes.length; i++) repeatTypeNames[i] = repeatTypes[i].name();
        android.widget.ArrayAdapter<String> repeatAdapter = new android.widget.ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, repeatTypeNames);
        repeatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRepeat.setAdapter(repeatAdapter);

        String[] importanceOptions = {"Not Important", "Important"};
        android.widget.ArrayAdapter<String> importanceAdapter = new android.widget.ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, importanceOptions);
        spinnerImportance.setAdapter(importanceAdapter);

        String[] strictOptions = {"Not Constant", "Constant"};
        android.widget.ArrayAdapter<String> strictAdapter = new android.widget.ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, strictOptions);
        spinnerStrict.setAdapter(strictAdapter);

        switchHasTime.setOnCheckedChangeListener((buttonView, isChecked) -> {
            etStart.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            etEnd.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        new AlertDialog.Builder(this)
                .setTitle("Add New Task")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    Task newTask = new Task();

                    newTask.setName(etName.getText().toString().trim());

                    try {
                        if (switchHasTime.isChecked()) {
                            if (!etStart.getText().toString().isEmpty()) newTask.setStart(LocalTime.parse(etStart.getText().toString()));
                            if (!etEnd.getText().toString().isEmpty()) newTask.setEnd(LocalTime.parse(etEnd.getText().toString()));
                        }
                    } catch (Exception ignored) {}

                    String dateStr = etDate.getText().toString();
                    if (!dateStr.isEmpty()) {
                        String[] parts = dateStr.split("/");
                        if (parts.length == 3) {
                            try {
                                int day = Integer.parseInt(parts[0]);
                                int month = Integer.parseInt(parts[1]);
                                int year = Integer.parseInt(parts[2]);
                                newTask.setDate(new Date(year, month, day));
                            } catch (Exception ignored) {}
                        }
                    }

                    try {
                        String pStr = etPriority.getText().toString();
                        if (!pStr.isEmpty()) newTask.setPriority(Integer.parseInt(pStr));
                    } catch (Exception ignored) {}

                    String selectedTopic = spinnerTopic.getSelectedItem() != null ? spinnerTopic.getSelectedItem().toString() : null;
                    if (selectedTopic != null && !selectedTopic.equals("Add Topic...")) {
                        newTask.setTopic(new Topic(selectedTopic));
                    }

                    newTask.setType(Constant.RepeatType.values()[spinnerRepeat.getSelectedItemPosition()]);
                    newTask.setImportant(spinnerImportance.getSelectedItemPosition() == 1);
                    newTask.setStrict(spinnerStrict.getSelectedItemPosition() == 1);

                    tasks.add(newTask);
                    updateListView();
                    saveTaskToUser(newTask);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    public void openEditTaskDialog(Task task, int position) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_task, null);

        EditText etName = dialogView.findViewById(R.id.etTaskName);
        EditText etStart = dialogView.findViewById(R.id.etTaskStart);
        EditText etEnd = dialogView.findViewById(R.id.etTaskEnd);
        EditText etDate = dialogView.findViewById(R.id.etTaskDate);
        EditText etPriority = dialogView.findViewById(R.id.etPriority);

        Spinner spinnerTopic = dialogView.findViewById(R.id.spinnerTopic);
        Spinner spinnerRepeat = dialogView.findViewById(R.id.spinnerRepeat);
        Spinner spinnerImportance = dialogView.findViewById(R.id.spinnerImportance);
        Spinner spinnerStrict = dialogView.findViewById(R.id.spinnerStrict);

        Switch switchHasTime = dialogView.findViewById(R.id.switchHasTime);

        etName.setText(task.getName());

        switchHasTime.setChecked(task.getStart() != null && task.getEnd() != null);
        switchHasTime.setOnCheckedChangeListener((buttonView, isChecked) -> {

        if(switchHasTime.isChecked()){
            etStart.setVisibility(View.VISIBLE);
            etEnd.setVisibility(View.VISIBLE);
            etStart.setText(task.getStart() != null ? task.getStart().toString() : "");
            etEnd.setText(task.getEnd() != null ? task.getEnd().toString() : "");
        } else {
            etStart.setVisibility(View.GONE);
            etEnd.setVisibility(View.GONE);
        }


        });
        if (task.getDate() != null) {
            etDate.setText(String.format("%02d/%02d/%04d",
                    task.getDate().getDay(), task.getDate().getMonth(), task.getDate().getYear()));
        }

        etPriority.setText(String.valueOf(task.getPriority()));

        etDate.setOnClickListener(v -> showDatePicker(etDate));
        etStart.setOnClickListener(v -> showTimePicker(etStart));
        etEnd.setOnClickListener(v -> showTimePicker(etEnd));

        loadUserTopics(spinnerTopic);
        if (task.getTopic() != null) {
            spinnerTopic.post(() -> {
                for (int i = 0; i < spinnerTopic.getCount(); i++) {
                    if (spinnerTopic.getItemAtPosition(i).toString().equals(task.getTopic().getName())) {
                        spinnerTopic.setSelection(i);
                        break;
                    }
                }
            });
        }

        Constant.RepeatType[] repeatTypes = Constant.RepeatType.values();
        String[] repeatTypeNames = new String[repeatTypes.length];
        for (int i = 0; i < repeatTypes.length; i++) repeatTypeNames[i] = repeatTypes[i].name();
        android.widget.ArrayAdapter<String> repeatAdapter = new android.widget.ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, repeatTypeNames);
        repeatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRepeat.setAdapter(repeatAdapter);
        if (task.getType() != null) spinnerRepeat.setSelection(task.getType().ordinal());

        android.widget.ArrayAdapter<String> importanceAdapter = new android.widget.ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"Not Important", "Important"});
        importanceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerImportance.setAdapter(importanceAdapter);
        spinnerImportance.setSelection(task.isImportant() ? 1 : 0);

        android.widget.ArrayAdapter<String> strictAdapter = new android.widget.ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"Not Constant", "Constant"});
        strictAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStrict.setAdapter(strictAdapter);
        spinnerStrict.setSelection(task.isStrict() ? 1 : 0);

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

                    String dateStr = etDate.getText().toString();
                    if (!dateStr.isEmpty()) {
                        String[] parts = dateStr.split("/");
                        if (parts.length == 3) {
                            try {
                                int day = Integer.parseInt(parts[0]);
                                int month = Integer.parseInt(parts[1]);
                                int year = Integer.parseInt(parts[2]);
                                task.setDate(new Date(year, month, day));
                            } catch (Exception ignored) {}
                        }
                    }

                    try { task.setPriority(Integer.parseInt(etPriority.getText().toString())); } catch (Exception ignored) {}

                    String selectedTopic = spinnerTopic.getSelectedItem() != null ? spinnerTopic.getSelectedItem().toString() : null;
                    if (selectedTopic != null) task.setTopic(new Topic(selectedTopic));

                    task.setType(Constant.RepeatType.values()[spinnerRepeat.getSelectedItemPosition()]);
                    task.setImportant(spinnerImportance.getSelectedItemPosition() == 1);
                    task.setStrict(spinnerStrict.getSelectedItemPosition() == 1);

                    updateListView();
                    saveTaskToUser(task);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void saveTaskToUser(Task task) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userID)
                .update("tasks", tasks);
    }

    private void showDatePicker(EditText editText) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int year = calendar.get(java.util.Calendar.YEAR);
        int month = calendar.get(java.util.Calendar.MONTH);
        int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);

        new android.app.DatePickerDialog(this, (view, y, m, d) -> {
            editText.setText(String.format("%02d/%02d/%04d", d, m + 1, y));
        }, year, month, day).show();
    }

    private void showTimePicker(EditText editText) {
        int hour = 9, minute = 0;
        String current = editText.getText().toString();
        if (!current.isEmpty()) {
            String[] parts = current.split(":");
            try { hour = Integer.parseInt(parts[0]); minute = Integer.parseInt(parts[1]); } catch (Exception ignored) {}
        }
        new android.app.TimePickerDialog(this,
                (view, hourOfDay, minute1) -> editText.setText(String.format("%02d:%02d", hourOfDay, minute1)),
                hour, minute, true).show();
    }

    private void loadUserTopics(Spinner spinnerTopic) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userID).collection("topics")
                .get()
                .addOnSuccessListener(doc -> {
                    ArrayList<String> topicNames = new ArrayList<>();
                    for (DocumentSnapshot snap : doc.getDocuments()) {
                        String topicName = snap.getString("name");
                        if (topicName != null) topicNames.add(topicName);
                    }
                    topicNames.add("Add Topic...");
                    android.widget.ArrayAdapter<String> spinnerAdapter = new android.widget.ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_item, topicNames);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerTopic.setAdapter(spinnerAdapter);

                    spinnerTopic.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                        boolean ignoreFirst = true;

                        @Override
                        public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                            if (ignoreFirst) { ignoreFirst = false; return; }
                            String selected = topicNames.get(position);
                            if (selected.equals("Add Topic...")) showAddTopicDialog(spinnerTopic, spinnerAdapter);
                        }

                        @Override
                        public void onNothingSelected(android.widget.AdapterView<?> parent) {}
                    });
                });
    }

    private void showAddTopicDialog(Spinner spinnerTopic, android.widget.ArrayAdapter<String> spinnerAdapter) {
        EditText etNewTopic = new EditText(this);
        etNewTopic.setHint("Enter topic name");

        new AlertDialog.Builder(this)
                .setTitle("Add Topic")
                .setView(etNewTopic)
                .setPositiveButton("Add", (dialog, which) -> {
                    String newTopicName = etNewTopic.getText().toString().trim();
                    if (!newTopicName.isEmpty()) {
                        FirebaseFirestore.getInstance()
                                .collection("users").document(userID)
                                .collection("topics")
                                .add(Map.of("name", newTopicName));
                        spinnerAdapter.insert(newTopicName, spinnerAdapter.getCount() - 1);
                        spinnerTopic.setSelection(spinnerAdapter.getPosition(newTopicName));
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAddTopicDirectDialog() {
        EditText etNewTopic = new EditText(this);
        etNewTopic.setHint("Enter new topic name");

        new AlertDialog.Builder(this)
                .setTitle("Add New Topic")
                .setView(etNewTopic)
                .setPositiveButton("Add", (dialog, which) -> {
                    String newTopicName = etNewTopic.getText().toString().trim();
                    if (!newTopicName.isEmpty()) {
                        FirebaseFirestore.getInstance()
                                .collection("users").document(userID)
                                .collection("topics")
                                .add(Map.of("name", newTopicName))
                                .addOnSuccessListener(docRef ->
                                        Toast.makeText(this, "Topic added: " + newTopicName, Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Failed to add topic, pls try again", Toast.LENGTH_SHORT).show());
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
