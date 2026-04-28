package com.example.time_wise;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Map;

public class Todos extends AppCompatActivity {

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
        setContentView(R.layout.todos);

        // 1. קבלת ה-ID מה-Intent (כמו ב-SchoolSchedule)
        userID = getIntent().getStringExtra("userId");
        Constant.USER_ID = userID;

        // 2. אתחול רכיבי ממשק
        sideMenu = findViewById(R.id.sideMenu);
        btnMenu = findViewById(R.id.btnMenu);
        recyclerView = findViewById(R.id.todosRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 3. לחיצה על כפתור המבורגר לפתיחה/סגירה
        btnMenu.setOnClickListener(v -> {
            if (sideMenu.getVisibility() == View.GONE) {
                sideMenu.setVisibility(View.VISIBLE);
            } else {
                sideMenu.setVisibility(View.GONE);
            }
        });

        // 4. חיבור ישיר של כפתורי התפריט (בדיוק כמו ב-SchoolSchedule)
        setMenuClickListener(R.id.nav_home, Constant.Menu.Home);
        setMenuClickListener(R.id.nav_activity, Constant.Menu.ACTIVITY);
        setMenuClickListener(R.id.nav_school_schedule, Constant.Menu.SCHOOL_SCHEDULE);
        setMenuClickListener(R.id.nav_follow_efficiency, Constant.Menu.FOLLOW_EFFICIENCY);
        setMenuClickListener(R.id.nav_todo, Constant.Menu.TODO);
        setMenuClickListener(R.id.nav_calendar, Constant.Menu.CALENDER);
        setMenuClickListener(R.id.nav_notes, Constant.Menu.NOTES);
        setMenuClickListener(R.id.nav_learning_plan, Constant.Menu.LERNING_PLAN);
        setMenuClickListener(R.id.nav_follow_goal, Constant.Menu.FOLLOW_GOAL);

        // 5. אתחול רשימה ו-Adapter
        todos = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        adapter = new TodoAdapter(todos, updatedTodos -> {
            todos = updatedTodos;
            saveToFirestore();
        });
        recyclerView.setAdapter(adapter);

        // 6. טעינת נתונים מה-Firestore
        loadFromFirestore();
    }

    private void setMenuClickListener(int id, Constant.Menu menu) {
        TextView item = findViewById(id);

        item.setOnClickListener(v -> {
            switch (menu){
                case Home:
                    intent = new Intent(Todos.this, HomeScreen.class);
                    Toast.makeText(this, "home", Toast.LENGTH_SHORT).show();
                    break;

                case ACTIVITY:
                    intent = new Intent(Todos.this, TasksScreem.class);
                    break;

                case SCHOOL_SCHEDULE:
                    // אם אנחנו כבר במסך מערכת שעות, לא עושים כלום
                    intent = new Intent(Todos.this, SchoolSchedule.class);
                    break;

                case FOLLOW_EFFICIENCY:
                    intent = new Intent(Todos.this, EfficiencyActivity.class);
                    Toast.makeText(this, "Follow Efficiency", Toast.LENGTH_SHORT).show();
                    break;

                case TODO:
                    intent = new Intent(Todos.this, Todos.class);
                    Toast.makeText(this, "To-Do", Toast.LENGTH_SHORT).show();
                    break;

                case CALENDER:
                    intent = new Intent(Todos.this, CalendarActivity.class);
                    Toast.makeText(this, "Calendar", Toast.LENGTH_SHORT).show();
                    break;

                case NOTES:
                    intent = new Intent(Todos.this, HomeScreen.class);
                    Toast.makeText(this, "Notes", Toast.LENGTH_SHORT).show();
                    break;

                case LERNING_PLAN:
                    intent = new Intent(Todos.this, HomeScreen.class);
                    Toast.makeText(this, "Learning Plan", Toast.LENGTH_SHORT).show();
                    break;

                case FOLLOW_GOAL:
                    intent = new Intent(Todos.this, GoalsActivity.class);
                    Toast.makeText(this, "Follow Goal", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Toast.makeText(this, "not working", Toast.LENGTH_SHORT).show();
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