package com.example.time_wise;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class TaskAdapter extends BaseAdapter {

    public interface TaskActionListener {
        void onEdit(Task task, int position);
        void onDelete(Task task, int position);
    }

    private Context context;
    private ArrayList<Task> tasks;
    private TaskActionListener listener;

    public TaskAdapter(Context context, ArrayList<Task> tasks, TaskActionListener listener) {
        this.context = context;
        this.tasks = tasks;
        this.listener = listener;
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
        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.task_list_item, parent, false);

        TextView tvTaskName = convertView.findViewById(R.id.tvTaskName);
        ImageButton btnEdit = convertView.findViewById(R.id.btnEdit);
        ImageButton btnDelete = convertView.findViewById(R.id.btnDelete);

        Task task = tasks.get(position);
        tvTaskName.setText(task.getName());

        btnEdit.setOnClickListener(v -> {
            if (listener != null)
                listener.onEdit(task, position);
        });

        btnDelete.setOnClickListener(v -> {
            if (listener != null)
                listener.onDelete(task, position);
        });

        return convertView;
    }

    public void updateTasks(ArrayList<Task> newTasks) {
        this.tasks = newTasks;
        notifyDataSetChanged();
    }
}
