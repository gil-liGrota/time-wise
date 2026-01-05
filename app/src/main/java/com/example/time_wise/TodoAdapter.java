package com.example.time_wise;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TodoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_TODO = 0;
    private static final int TYPE_ADD = 1;

    private ArrayList<Todo> todos;
    private OnTodosChangedListener listener;

    public TodoAdapter(ArrayList<Todo> todos, OnTodosChangedListener listener) {
        this.todos = todos;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return todos.size() + 1; // כולל TAP TO ENTER MORE
    }

    @Override
    public int getItemViewType(int position) {
        return position == todos.size() ? TYPE_ADD : TYPE_TODO;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_ADD) {
            View v = inflater.inflate(R.layout.todo_add, parent, false);
            return new AddViewHolder(v);
        } else {
            View v = inflater.inflate(R.layout.todo_item, parent, false);
            return new TodoViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof TodoViewHolder) {
            Todo todo = todos.get(position);
            TodoViewHolder vh = (TodoViewHolder) holder;

            vh.todoText.setText(todo.name);

            vh.checkBox.setOnCheckedChangeListener(null);
            vh.checkBox.setChecked(false);

            vh.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    todos.remove(position);
                    notifyDataSetChanged();
                    if (listener != null) listener.onTodosChanged(todos);
                }
            });

        } else { // TYPE_ADD
            holder.itemView.setOnClickListener(v -> {
                // פותח Dialog להוספה
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                View dialogView = LayoutInflater.from(v.getContext())
                        .inflate(R.layout.dialog_add_todo, null);
                EditText etTodo = dialogView.findViewById(R.id.etTodo);

                builder.setTitle("Add new task");
                builder.setView(dialogView);

                builder.setPositiveButton("Add", (dialog, which) -> {
                    String text = etTodo.getText().toString().trim();
                    if (!text.isEmpty()) {
                        todos.add(new Todo(text, false));
                        notifyDataSetChanged();
                        if (listener != null) listener.onTodosChanged(todos);
                    }
                });

                builder.setNegativeButton("Cancel", null);
                builder.show();
            });
        }
    }

    // -------- ViewHolders --------
    static class TodoViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView todoText;

        TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.todoCheck);
            todoText = itemView.findViewById(R.id.todoText);
        }
    }

    static class AddViewHolder extends RecyclerView.ViewHolder {
        AddViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    // -------- Listener לעדכון Activity --------
    public interface OnTodosChangedListener {
        void onTodosChanged(ArrayList<Todo> todos);
    }
}
