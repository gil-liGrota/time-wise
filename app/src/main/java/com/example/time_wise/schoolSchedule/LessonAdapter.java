package com.example.time_wise.schoolSchedule;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.time_wise.R;

import java.util.ArrayList;
import java.util.List;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {

    private List<Lesson> lessons = new ArrayList<>();

    public void setData(List<Lesson> newLessons) {
        lessons = newLessons;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lesson, parent, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        Lesson lesson = lessons.get(position);

        if (holder.etLesson.getTag() instanceof TextWatcher) {
            holder.etLesson.removeTextChangedListener((TextWatcher) holder.etLesson.getTag());
        }

        holder.txtHour.setText(String.valueOf(lesson.getHour()));
        holder.etLesson.setText(lesson.getName());

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                lesson.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        holder.etLesson.addTextChangedListener(watcher);
        holder.etLesson.setTag(watcher);
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    static class LessonViewHolder extends RecyclerView.ViewHolder {

        TextView txtHour;
        EditText etLesson;

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            txtHour = itemView.findViewById(R.id.txtHour);
            etLesson = itemView.findViewById(R.id.etLesson);
        }
    }
}
