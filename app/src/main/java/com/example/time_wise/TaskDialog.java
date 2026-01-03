package com.example.time_wise;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TaskDialog extends Dialog {

    private Task task;
    private OnTaskUpdatedListener listener;
    private Context context;

    // UI Elements
    private EditText etTaskName, etTaskStart, etTaskEnd, etTaskDate, etPriority;
    private Spinner spinnerTopic, spinnerRepeat, spinnerImportance, spinnerStrict;
    private Switch switchHasTime;

    private FirebaseFirestore db;

    public interface OnTaskUpdatedListener {
        void onTaskUpdated(Task task);
    }

    public TaskDialog(@Nullable Task task, @NonNull Context context, @NonNull OnTaskUpdatedListener listener) {
        super(context);
        this.task = task;
        this.context = context;
        this.listener = listener;
        db = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_task);

        // findViewById
        etTaskName = findViewById(R.id.etTaskName);
        etTaskStart = findViewById(R.id.etTaskStart);
        etTaskEnd = findViewById(R.id.etTaskEnd);
        etTaskDate = findViewById(R.id.etTaskDate);
        etPriority = findViewById(R.id.etPriority);
        spinnerTopic = findViewById(R.id.spinnerTopic);
        spinnerRepeat = findViewById(R.id.spinnerRepeat);
        spinnerImportance = findViewById(R.id.spinnerImportance);
        spinnerStrict = findViewById(R.id.spinnerStrict);
        switchHasTime = findViewById(R.id.switchHasTime);

        // למלא את השדות אם זו משימה קיימת
        if (task != null) {
            etTaskName.setText(task.getName());
            etTaskDate.setText(task.getDate() != null ? task.getDate().toString() : "");
            etTaskStart.setText(task.getStart() != null ? task.getStart().toString() : "");
            etTaskEnd.setText(task.getEnd() != null ? task.getEnd().toString() : "");
            etPriority.setText(String.valueOf(task.getPriority()));
            switchHasTime.setChecked(task.getStart() != null && task.getEnd() != null);
        }

        // לדוגמה: נניח שיש לך רשימות מראש ל־Spinner
        List<String> topics = Constant.TOPICS;
        List<String> repeatTypes = Constant.REPEAT_TYPES;
        List<String> importance = Constant.IMPORTANCE_OPTIONS;
        List<String> strictOptions = Constant.STRICT_OPTIONS;

        spinnerTopic.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, topics));
        spinnerRepeat.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, repeatTypes));
        spinnerImportance.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, importance));
        spinnerStrict.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, strictOptions));

        // כפתור שמירה
        findViewById(R.id.btnSaveTask).setOnClickListener(v -> saveTask());
        findViewById(R.id.btnCancelTask).setOnClickListener(v -> dismiss());
    }

    private void saveTask() {
        String name = etTaskName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(context, "Enter task name", Toast.LENGTH_SHORT).show();
            return;
        }

        LocalTime start = null, end = null;
        if (switchHasTime.isChecked()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                start = LocalTime.parse(etTaskStart.getText().toString(), formatter);
                end = LocalTime.parse(etTaskEnd.getText().toString(), formatter);
            } catch (Exception e) {
                Toast.makeText(context, "Invalid time format. Use HH:mm", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // ניתן למפות את ה־Spinners לערכים שלך
        Topic topic = new Topic(spinnerTopic.getSelectedItem().toString());
        Constant.RepeatType repeatType = Constant.RepeatType.valueOf(spinnerRepeat.getSelectedItem().toString());
        boolean isImportant = spinnerImportance.getSelectedItemPosition() == 1;
        boolean strict = spinnerStrict.getSelectedItemPosition() == 1;
        int priority = 0;
        try {
            priority = Integer.parseInt(etPriority.getText().toString());
        } catch (NumberFormatException ignored) {}

        // ליצור/לעדכן משימה
        if (task == null) {
            task = new Task(name, topic, new Date(), repeatType, start, end, isImportant, priority, strict);
        } else {
            task.setName(name);
            task.setTopic(topic);
            task.setStart(start);
            task.setEnd(end);
            task.setType(repeatType);
            task.setPriority(priority);
            task.setImportant(isImportant);
            task.setStrict(strict);
        }

        // שמירה ב־Firestore
        String userID = Constant.USER_ID;
        if (userID == null) {
            Toast.makeText(context, "User ID is null", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users")
                .document(userID)
                .collection("tasks")
                .document(task.getName()) // אפשר לשנות למזהה ייחודי
                .set(task)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Task saved", Toast.LENGTH_SHORT).show();
                    listener.onTaskUpdated(task);
                    dismiss();
                })
                .addOnFailureListener(e -> Toast.makeText(context,
                        "Failed to save task: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show());
    }
}
