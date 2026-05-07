package com.example.time_wise.todo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.time_wise.Constant;
import com.example.time_wise.R;
import com.example.time_wise.calender.CalendarActivity;
import com.example.time_wise.enterApp.HomeActivity;
import com.example.time_wise.enterApp.LogInActivity;
import com.example.time_wise.followEfficiency.EfficiencyActivity;
import com.example.time_wise.followGoal.GoalsActivity;
import com.example.time_wise.notes.NotesActivity;
import com.example.time_wise.schoolSchedule.SchoolScheduleActivity;
import com.example.time_wise.task.TasksActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class TodosActivity extends AppCompatActivity {

    public String userID;
    private Intent intent;
    private RecyclerView recyclerView;
    private TodoAdapter adapter;
    private ArrayList<Todo> todos;
    private FirebaseFirestore db;
    private LinearLayout sideMenu;
    private TextView btnMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todos);

        userID = getIntent().getStringExtra("userId");
        Constant.USER_ID = userID;

        sideMenu = findViewById(R.id.sideMenu);
        btnMenu = findViewById(R.id.btnMenu);
        recyclerView = findViewById(R.id.todosRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnMenu.setOnClickListener(v -> {
            if (sideMenu.getVisibility() == View.GONE) {
                sideMenu.setVisibility(View.VISIBLE);
            } else {
                sideMenu.setVisibility(View.GONE);
            }
        });

        setMenuClickListener(R.id.nav_home, Constant.Menu.Home);
        setMenuClickListener(R.id.nav_activity, Constant.Menu.ACTIVITY);
        setMenuClickListener(R.id.nav_school_schedule, Constant.Menu.SCHOOL_SCHEDULE);
        setMenuClickListener(R.id.nav_follow_efficiency, Constant.Menu.FOLLOW_EFFICIENCY);
        setMenuClickListener(R.id.nav_todo, Constant.Menu.TODO);
        setMenuClickListener(R.id.nav_calendar, Constant.Menu.CALENDER);
        setMenuClickListener(R.id.nav_notes, Constant.Menu.NOTES);
        setMenuClickListener(R.id.nav_follow_goal, Constant.Menu.FOLLOW_GOAL);
        setMenuClickListener(R.id.nav_sign_out, Constant.Menu.SIGN_OUT);


        todos = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        adapter = new TodoAdapter(todos, updatedTodos -> {
            todos = updatedTodos;
            saveToFirestore();
        });
        recyclerView.setAdapter(adapter);

        loadFromFirestore();
    }

    private void setMenuClickListener(int id, Constant.Menu menu) {
        TextView item = findViewById(id);

        item.setOnClickListener(v -> {
            switch (menu){
                case Home: intent = new Intent(TodosActivity.this, HomeActivity.class);break;
                case ACTIVITY: intent = new Intent(TodosActivity.this, TasksActivity.class);break;
                case SCHOOL_SCHEDULE: intent = new Intent(TodosActivity.this, SchoolScheduleActivity.class);break;
                case FOLLOW_EFFICIENCY: intent = new Intent(TodosActivity.this, EfficiencyActivity.class);break;
                case TODO: intent = new Intent(TodosActivity.this, TodosActivity.class);break;
                case CALENDER: intent = new Intent(TodosActivity.this, CalendarActivity.class);break;
                case NOTES: intent = new Intent(TodosActivity.this, NotesActivity.class);break;
                case FOLLOW_GOAL: intent = new Intent(TodosActivity.this, GoalsActivity.class);break;
                case SIGN_OUT: intent = new Intent(TodosActivity.this, LogInActivity.class);break;
                default:
                    return;
            }

            intent.putExtra("userId", userID);
            startActivity(intent);
            sideMenu.setVisibility(View.GONE);
        });
    }


    private void loadFromFirestore() {
        if (userID == null || userID.isEmpty()) return;

        db.collection("users").document(userID)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.e("FIRESTORE", "Listen failed", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        todos.clear();
                        ArrayList<Map<String, Object>> list = (ArrayList<Map<String, Object>>) snapshot.get("todos");

                        if (list != null) {
                            for (Map<String, Object> map : list) {
                                String name = (String) map.get("name");
                                boolean done = map.get("done") != null && (boolean) map.get("done");
                                todos.add(new Todo(name, done));
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void saveToFirestore() {
        if (userID == null || userID.isEmpty()) return;

        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for (Todo t : todos) {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("name", t.name);
            map.put("done", t.done);
            list.add(map);
        }

        db.collection("users").document(userID)
                .update("todos", list)
                .addOnSuccessListener(aVoid -> Log.d("FIRESTORE", "Todos saved"))
                .addOnFailureListener(e -> Log.e("FIRESTORE", "Failed to save", e));
    }
}