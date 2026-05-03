package com.example.time_wise;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalTime;
import java.util.ArrayList;

public class TaskAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Task> tasks;
    private String userID;
    private FirebaseFirestore db;

    public TaskAdapter(Context context, ArrayList<Task> tasks, String userID) {
        this.context = context;
        this.tasks = tasks;
        this.userID = userID;
        this.db = FirebaseFirestore.getInstance();
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.task_list_item, parent, false);
        }

        TextView tvTaskName = convertView.findViewById(R.id.tvTaskName);
        ImageButton btnEdit = convertView.findViewById(R.id.btnEdit);
        ImageButton btnDelete = convertView.findViewById(R.id.btnDelete);


//        Task task = tasks.get(position);
//        tvTaskName.setText(task.getName());
        Object item = tasks.get(position);
        Task task;

        if (item instanceof java.util.Map) {
            // אם זה HashMap (כפי שמגיע מ-Firestore), נמיר אותו לאובייקט Task
            java.util.Map<String, Object> map = (java.util.Map<String, Object>) item;
            task = convertMapToTask(map);
        } else {
            // אם זה כבר אובייקט Task (כפי שקורה ב-LearningPlan)
            task = (Task) item;
        }
        // ------------------

        tvTaskName.setText(task.getName());

        btnEdit.setOnClickListener(v -> {
            if (context instanceof TasksScreem) {
                ((TasksScreem) context).openEditTaskDialog(task, position);
            } else if (context instanceof CalendarActivity) {
                ((CalendarActivity) context).openEditTaskDialog(task, position);
            }
        });

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete this task?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (context instanceof CalendarActivity) {
                            ((CalendarActivity) context).deleteTaskFromCalendar(task);
                        } else {
                            tasks.remove(position);
                            notifyDataSetChanged();
                            saveTasksToDB();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        return convertView;
    }

    private Task convertMapToTask(java.util.Map<String, Object> map) {
        if (map == null) return null;

        Task t = new Task();

        // שם המשימה - וודאי שב-Firebase זה אכן String ולא אובייקט
        Object nameObj = map.get("name");
        if (nameObj instanceof String) {
            t.setName((String) nameObj);
        }

        // טיפול ב-Date - כאן השגיאה הכי נפוצה
        Object dateObj = map.get("date");
        if (dateObj instanceof java.util.Map) {
            java.util.Map<String, Object> dateMap = (java.util.Map<String, Object>) dateObj;
            // שימוש ב-Number כדי להיות בטוחים (מטפל גם ב-Long וגם ב-Integer)
            int d = ((Number) dateMap.get("day")).intValue();
            int m = ((Number) dateMap.get("month")).intValue();
            int y = ((Number) dateMap.get("year")).intValue();
            t.setDate(new com.example.time_wise.Date(d, m, y));
        }

        // טיפול ב-Topic
        Object topicObj = map.get("topic");
        if (topicObj instanceof java.util.Map) {
            // אם Topic הוא אובייקט מורכב ב-Firebase
            java.util.Map<String, Object> topicMap = (java.util.Map<String, Object>) topicObj;
            String topicName = (String) topicMap.get("name");
            // צרי אובייקט Topic חדש לפי הצורך
            // t.setTopic(new Topic(topicName));
        } else if (topicObj instanceof String) {
            // אם Topic הוא רק מחרוזת
            // t.setTopic(new Topic((String) topicObj));
        }

        // המרת זמנים מ-String ל-LocalTime
        if (map.get("start") instanceof String) {
            t.setStart(LocalTime.parse((String) map.get("start")));
        }
        if (map.get("end") instanceof String) {
            t.setEnd(LocalTime.parse((String) map.get("end")));
        }

        // בוליאנים
        if (map.get("isImportant") instanceof Boolean) {
            t.setImportant((Boolean) map.get("isImportant"));
        }
        if (map.get("strict") instanceof Boolean) {
            t.setStrict((Boolean) map.get("strict"));
        }

        // עדיפות - זהירות עם Long מ-Firestore
        if (map.get("priority") instanceof Number) {
            t.setPriority(((Number) map.get("priority")).intValue());
        }

        // Enum
        if (map.get("type") instanceof String) {
            t.setType(Constant.RepeatType.valueOf((String) map.get("type")));
        }

        return t;
    }

    private void saveTasksToDB() {
        db.collection("users").document(userID)
                .update("tasks", tasks)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Tasks updated!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to update tasks", Toast.LENGTH_SHORT).show());
    }
}