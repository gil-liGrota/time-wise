package com.example.time_wise;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
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

public class TasksScreem extends AppCompatActivity implements TaskAdapter.TaskActionListener {

    private ListView lvActivity;
    private ArrayList<Task> tasks;
    private TaskAdapter taskAdapter;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.tasks_screem);

        lvActivity = findViewById(R.id.activityList);
        tasks = new ArrayList<>();
        userID = getIntent().getStringExtra("userId");

        taskAdapter = new TaskAdapter(this, tasks, this);
        lvActivity.setAdapter(taskAdapter);

        loadTasks();

        findViewById(R.id.btnAddActivity).setOnClickListener(v -> openAddTaskDialog());
        findViewById(R.id.btnAddTopic).setOnClickListener(v -> showAddTopicDirectDialog());
    }

    // --- Load tasks from Firestore ---
    private void loadTasks() {
        // רוקן קודם את הרשימה המקומית
        tasks.clear();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // לוקח את כל המשימות של המשתמש (כל משימה במסמך נפרד)
        db.collection("users")
                .document(userID)
                .collection("tasks")
                .get()
                .addOnSuccessListener(snapshot -> {
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        // ממיר את המסמך לאובייקט Task
                        Task task = doc.toObject(Task.class);

                        if (task != null) {
                            // שמירת מזהה המסמך בתוך ה-Task
                            task.setFirestoreId(doc.getId());

                            // מוסיף לרשימה המקומית
                            tasks.add(task);
                        }
                    }

                    // מעדכן את ה-Adapter כדי שה-ListView יציג את הרשימה החדשה
                    if (taskAdapter != null) {
                        taskAdapter.updateTasks(tasks);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load tasks: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    // --- TaskAdapter callbacks ---
    @Override
    public void onEdit(Task task, int position) {
        openEditTaskDialog(task, position);
    }

    @Override
    public void onDelete(Task task, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    String docId = task.getFirestoreId(); // צריך להוסיף מזהה לכל Task
                    FirebaseFirestore.getInstance()
                            .collection("users").document(userID)
                            .collection("tasks").document(docId)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                tasks.remove(position);
                                taskAdapter.updateTasks(tasks);
                            });
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


    // --- Add / Edit Task dialogs ---
    private void openAddTaskDialog() { /* כמו הקודם אבל מוסיף ל-Firestore עם ID */ }

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

        // מלא את השדות הקיימים
        etName.setText(task.getName());
        etStart.setText(task.getStart() != null ? task.getStart().toString() : "09:00");
        etEnd.setText(task.getEnd() != null ? task.getEnd().toString() : "10:00");
        if (task.getDate() != null)
            etDate.setText(String.format("%02d/%02d/%04d", task.getDate().getDay(), task.getDate().getMonth(), task.getDate().getYear()));
        etPriority.setText(String.valueOf(task.getPriority()));

        switchHasTime.setChecked(task.getStart() != null && task.getEnd() != null);
        switchHasTime.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) { etStart.setVisibility(View.VISIBLE); etEnd.setVisibility(View.VISIBLE); }
            else { etStart.setVisibility(View.GONE); etEnd.setVisibility(View.GONE); }
        });

        etStart.setOnClickListener(v -> showTimePicker(etStart));
        etEnd.setOnClickListener(v -> showTimePicker(etEnd));
        etDate.setOnClickListener(v -> showDatePicker(etDate));

        loadUserTopics(spinnerTopic);

        new AlertDialog.Builder(this)
                .setTitle("Edit Task")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    // --- עדכון השדות ---
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
                        try {
                            String[] parts = dateStr.split("/");
                            int day = Integer.parseInt(parts[0]);
                            int month = Integer.parseInt(parts[1]);
                            int year = Integer.parseInt(parts[2]);
                            task.setDate(new Date(year, month, day));
                        } catch (Exception ignored) {}
                    }

                    try { task.setPriority(Integer.parseInt(etPriority.getText().toString())); } catch (Exception ignored) {}

                    String selectedTopic = spinnerTopic.getSelectedItem() != null ? spinnerTopic.getSelectedItem().toString() : null;
                    if (selectedTopic != null) task.setTopic(new Topic(selectedTopic));

                    task.setType(Constant.RepeatType.values()[spinnerRepeat.getSelectedItemPosition()]);
                    task.setImportant(spinnerImportance.getSelectedItemPosition() == 1);
                    task.setStrict(spinnerStrict.getSelectedItemPosition() == 1);

                    // --- שמירה ל-Firestore ---
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("users")
                            .document(userID)
                            .collection("tasks")
                            .document(task.getFirestoreId())
                            .set(task)
                            .addOnSuccessListener(aVoid -> {
                                taskAdapter.updateTasks(tasks); // עדכון UI
                                Toast.makeText(this, "Task updated!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Failed to update task", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ... שאר פונקציות כמו showDatePicker, showTimePicker, loadUserTopics נשארות
}
