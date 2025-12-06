package com.example.time_wise;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
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

    private LinearLayout sideMenu;
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

        sideMenu = findViewById(R.id.sideMenu);
        btnMenu = findViewById(R.id.btnMenu);
        lvActivity = findViewById(R.id.activityList);

        Intent lastIntent = getIntent();
        userID = lastIntent.getStringExtra("userId");

        tasks = new ArrayList<>();

        FloatingActionButton btnAddActivity = findViewById(R.id.btnAddActivity);
        btnAddActivity.setOnClickListener(v -> openAddTaskDialog());

        FloatingActionButton btnAddTopic = findViewById(R.id.btnAddTopic);
        btnAddTopic.setOnClickListener(v -> showAddTopicDirectDialog());

        loadTasks();
        setupMenu();
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

    private void setupMenu() {
        btnMenu.setOnClickListener(v -> sideMenu.setVisibility(sideMenu.getVisibility() == View.GONE ? View.VISIBLE : View.GONE));

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

    private void setMenuClickListener(int id, Constant.Menu menu) {
        TextView item = findViewById(id);
        item.setOnClickListener(v -> {
            switch (menu) {
                case Home:
                    startActivity(new Intent(TasksScreem.this, TasksScreem.class));
                    break;
                default:
                    Toast.makeText(this, menu.toString(), Toast.LENGTH_SHORT).show();
            }
            sideMenu.setVisibility(View.GONE);
        });
    }

    private void openAddTaskDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_task, null);
        EditText etName = dialogView.findViewById(R.id.etTaskName);
        EditText etStart = dialogView.findViewById(R.id.etTaskStart);
        EditText etEnd = dialogView.findViewById(R.id.etTaskEnd);
        Spinner spinnerTopic = dialogView.findViewById(R.id.spinnerTopic);

        etStart.setOnClickListener(v -> showTimePicker(etStart));
        etEnd.setOnClickListener(v -> showTimePicker(etEnd));

        loadUserTopics(spinnerTopic);

        new AlertDialog.Builder(this)
                .setTitle("Add New Task")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String startStr = etStart.getText().toString().trim();
                    String endStr = etEnd.getText().toString().trim();
                    String selectedTopic = spinnerTopic.getSelectedItem() != null
                            ? spinnerTopic.getSelectedItem().toString()
                            : null;

                    if (!name.isEmpty()) {
                        Task newTask = new Task();
                        newTask.setName(name);
                        if (selectedTopic != null) newTask.setTopic(new Topic(selectedTopic));
                        try {
                            if (!startStr.isEmpty()) newTask.setStart(LocalTime.parse(startStr));
                            if (!endStr.isEmpty()) newTask.setEnd(LocalTime.parse(endStr));
                        } catch (Exception e) { e.printStackTrace(); }

                        tasks.add(newTask);
                        updateListView();
                        saveTaskToUser(newTask);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
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
}
