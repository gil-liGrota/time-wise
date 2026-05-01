package com.example.time_wise;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {

    private List<String> lessons = new ArrayList<>();

    // פונקציה שמחליפה את המערכת לפי יום
    public void setData(List<String> newLessons) {
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
        // מספר השעה (1, 2, 3...)
        holder.txtHour.setText(String.valueOf(position + 1));

        // שם השיעור
        holder.txtLesson.setText(lessons.get(position));
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    // ViewHolder
    static class LessonViewHolder extends RecyclerView.ViewHolder {

        TextView txtHour;
        TextView txtLesson;

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            txtHour = itemView.findViewById(R.id.txtHour);
            txtLesson = itemView.findViewById(R.id.txtLesson);
        }
    }
}
