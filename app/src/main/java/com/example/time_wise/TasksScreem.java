package com.example.time_wise;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Map;

public class TasksScreem extends AppCompatActivity {

    private TextView btnMenu;
    private ListView lvActivity;
    private ArrayList<Task> tasks;
    private ArrayAdapter<String> adapter;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.tasks_screem);

        btnMenu = findViewById(R.id.btnMenu);
        lvActivity = findViewById(R.id.activityList);

        userID = getIntent().getStringExtra("userId");
        tasks = new ArrayList<>();

        FloatingActionButton btnAddActivity = findViewById(R.id.btnAddActivity);
        btnAddActivity.setOnClickListener(v -> openAddTaskDialog());

        FloatingActionButton btnAddTopic = findViewById(R.id.btnAddTopic);
        btnAddTopic.setOnClickListener(v -> showAddTopicDirectDialog());

        loadTasks();
    }

    private void loadTasks() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userID)
                .get()
                .addOnSuccessListener(doc -> {
                    ArrayList<Map<String, Object>> taskListFromDB =
                            (ArrayList<Map<String, Object>>) doc.get("tasks");

                    if (taskListFromDB != null) {
                        for (Map<String, Object> t : taskListFromDB) {
                            String name = t.get("name") != null ? t.get("name").toString() : "No Name";
                            Task task = new Task();
                            task.setName(name);
                            tasks.add(task);
                        }
                    }
                    updateListView();
                });
    }

    private void updateListView() {
        ArrayList<String> taskNames = new ArrayList<>();
        for (Task t : tasks) taskNames.add(t.getName());

        if (adapter == null) {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, taskNames);
            lvActivity.setAdapter(adapter);
        } else {
            adapter.clear();
            adapter.addAll(taskNames);
            adapter.notifyDataSetChanged();
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

        // TimePickers
        etStart.setOnClickListener(v -> showTimePicker(etStart));
        etEnd.setOnClickListener(v -> showTimePicker(etEnd));

        // DatePicker
        etDate.setOnClickListener(v -> showDatePicker(etDate));

        // Load topics
        loadUserTopics(spinnerTopic);

        // RepeatType spinner
        Constant.RepeatType[] repeatTypes = Constant.RepeatType.values();
        String[] repeatTypeNames = new String[repeatTypes.length];
        for (int i = 0; i < repeatTypes.length; i++) {
            repeatTypeNames[i] = repeatTypes[i].name(); // או friendly name
        }
        ArrayAdapter<String> repeatAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, repeatTypeNames);
        repeatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRepeat.setAdapter(repeatAdapter);

        // Importance spinner
        ArrayAdapter<String> importanceAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"Not Important", "Important"});
        importanceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerImportance.setAdapter(importanceAdapter);

        // Strict spinner
        ArrayAdapter<String> strictAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"Not Constant", "Constant"});
        strictAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStrict.setAdapter(strictAdapter);

        // Switch behavior
        switchHasTime.setChecked(true); // ברירת מחדל
        switchHasTime.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                etStart.setVisibility(View.VISIBLE);
                etEnd.setVisibility(View.VISIBLE);
            } else {
                etStart.setText("00:00");
                etEnd.setText("24:00");
                etStart.setVisibility(View.GONE);
                etEnd.setVisibility(View.GONE);
            }
        });

        // Dialog itself
        new AlertDialog.Builder(this)
                .setTitle("Add New Task")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String startStr = etStart.getText().toString().trim();
                    String endStr = etEnd.getText().toString().trim();
                    String dateStr = etDate.getText().toString().trim();
                    String selectedTopic = spinnerTopic.getSelectedItem() != null
                            ? spinnerTopic.getSelectedItem().toString() : null;

                    Constant.RepeatType repeatType = Constant.RepeatType.values()[spinnerRepeat.getSelectedItemPosition()];
                    boolean isImportant = spinnerImportance.getSelectedItem().equals("Important");
                    boolean strict = spinnerStrict.getSelectedItem().equals("Constant");

                    int priority = 0;
                    try { priority = Integer.parseInt(etPriority.getText().toString()); } catch (Exception ignored) {}

                    Task newTask = new Task();
                    newTask.setName(name);
                    if (selectedTopic != null) newTask.setTopic(new Topic(selectedTopic));
                    newTask.setType(repeatType);
                    newTask.setImportant(isImportant);
                    newTask.setStrict(strict);
                    newTask.setPriority(priority);

                    // Times
                    try {
                        if (!startStr.isEmpty()) newTask.setStart(LocalTime.parse(startStr));
                        if (!endStr.isEmpty()) newTask.setEnd(LocalTime.parse(endStr));
                    } catch (Exception e) { e.printStackTrace(); }

                    // Date
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

                    tasks.add(newTask);
                    updateListView();
                    saveTaskToUser(newTask);
                })
                .setNegativeButton("Cancel", null)
                .show();
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
                .addOnSuccessListener(snapshot -> {
                    ArrayList<String> topicNames = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        String topicName = doc.getString("name");
                        if (topicName != null) topicNames.add(topicName);
                    }
                    topicNames.add("Add Topic...");

                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
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

    private void showAddTopicDialog(Spinner spinnerTopic, ArrayAdapter<String> spinnerAdapter) {
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
                                        Toast.makeText(this, "Failed to add topic", Toast.LENGTH_SHORT).show());
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveTaskToUser(Task task) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userID)
                .update("tasks", tasks)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Task saved!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save task", Toast.LENGTH_SHORT).show());
    }
}
