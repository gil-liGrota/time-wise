package com.example.time_wise;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

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

        Task task = tasks.get(position);
        tvTaskName.setText(task.getName());

        // --- Edit button ---
        btnEdit.setOnClickListener(v -> {
            if (context instanceof TasksScreem) {
                ((TasksScreem) context).openEditTaskDialog(task, position);
            }
        });

        // --- Delete button ---
        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete this task?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        tasks.remove(position);
                        notifyDataSetChanged();
                        saveTasksToDB();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        return convertView;
    }

    private void saveTasksToDB() {
        db.collection("users").document(userID)
                .update("tasks", tasks)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Tasks updated!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to update tasks", Toast.LENGTH_SHORT).show());
    }
}
