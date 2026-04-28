package com.example.time_wise;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Map;

public class GoalsActivity extends AppCompatActivity {
    private Intent intent;
    private LinearLayout sideMenu;
    private TextView btnMenu;
    private ListView lvGoals;
    private ArrayList<Goal> goals;
    private ArrayAdapter<Goal> adapter;
    private FirebaseFirestore db;
    private String userID = Constant.USER_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);

        db = FirebaseFirestore.getInstance();
        lvGoals = findViewById(R.id.lvGoals);
        FloatingActionButton fabAdd = findViewById(R.id.fabAddGoal);

        goals = new ArrayList<>();
        // נשתמש ב-ArrayAdapter פשוט שמציג את שם המטרה (מתוך ה-toString של Goal)
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, goals);
        lvGoals.setAdapter(adapter);

        // מעבר למסך פרטי המטרה בלחיצה על פריט ברשימה
        lvGoals.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(GoalsActivity.this, GoalDetailsActivity.class);
            intent.putExtra("goal", goals.get(position));
            startActivity(intent);
        });

        // מעבר למסך הוספת מטרה חדשה
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(GoalsActivity.this, AddGoalActivity.class);
            startActivity(intent);
        });

        loadGoalsFromFirebase();
        sideMenu = findViewById(R.id.sideMenu);
        btnMenu = findViewById(R.id.btnMenu);

        // לחיצה על כפתור המבורגר לפתיחה/סגירה
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
        setMenuClickListener(R.id.nav_learning_plan, Constant.Menu.LERNING_PLAN);
        setMenuClickListener(R.id.nav_follow_goal, Constant.Menu.FOLLOW_GOAL);
    }


    private void setMenuClickListener(int id, Constant.Menu menu) {
        TextView item = findViewById(id);

        item.setOnClickListener(v -> {
            switch (menu){
                case Home:
                    intent = new Intent(GoalsActivity.this, HomeScreen.class);
                    Toast.makeText(this, "home", Toast.LENGTH_LONG).show();
                    break;
                case ACTIVITY:
                    intent = new Intent(GoalsActivity.this, TasksScreem.class);
                    break;
                case SCHOOL_SCHEDULE:
                    intent = new Intent(GoalsActivity.this, SchoolSchedule.class);
                    Toast.makeText(this, "School Schedule", Toast.LENGTH_LONG).show();
                    break;

                case FOLLOW_EFFICIENCY:
                    intent = new Intent(GoalsActivity.this, EfficiencyActivity.class);
                    Toast.makeText(this, "Follow Efficiency", Toast.LENGTH_LONG).show();
                    break;

                case TODO:
                    intent = new Intent(GoalsActivity.this, Todos.class);
                    Toast.makeText(this, "To-Do", Toast.LENGTH_LONG).show();
                    break;

                case CALENDER:
                    intent = new Intent(GoalsActivity.this, CalendarActivity.class);
                    Toast.makeText(this, "Calendar", Toast.LENGTH_LONG).show();
                    break;

                case NOTES:
                    intent = new Intent(GoalsActivity.this, HomeScreen.class);
                    Toast.makeText(this, "Notes", Toast.LENGTH_LONG).show();
                    break;

                case LERNING_PLAN:
                    intent = new Intent(GoalsActivity.this, HomeScreen.class);
                    Toast.makeText(this, "Learning Plan", Toast.LENGTH_LONG).show();
                    break;

                case FOLLOW_GOAL:
                    intent = new Intent(GoalsActivity.this, GoalsActivity.class);
                    Toast.makeText(this, "Follow Goal", Toast.LENGTH_LONG).show();
                    break;
                default:
                    intent = new Intent(GoalsActivity.this, HomeScreen.class);
                    Toast.makeText(this, "not working", Toast.LENGTH_LONG).show();
                    break;
            }
            intent.putExtra("userId", userID);
            startActivity(intent);
            sideMenu.setVisibility(View.GONE);
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadGoalsFromFirebase(); // רענון הרשימה כשחוזרים ממסכים אחרים
    }

    private void loadGoalsFromFirebase() {
        db.collection("users").document(userID).get().addOnSuccessListener(doc -> {
            if (doc.exists() && doc.get("goals") != null) {
                ArrayList<Map<String, Object>> data = (ArrayList<Map<String, Object>>) doc.get("goals");
                goals.clear();
                for (Map<String, Object> map : data) {
                    goals.add(convertMapToGoal(map));
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    // פונקציית עזר להמרת הנתונים מ-Firebase לאובייקט Goal
    private Goal convertMapToGoal(Map<String, Object> map) {
        String id = (String) map.get("id");
        String title = (String) map.get("title");
        String note = (String) map.get("note");
        boolean isDaily = (Boolean) map.get("daily");

        Date targetDate = null;
        if (map.get("targetDate") != null) {
            Map<String, Object> d = (Map<String, Object>) map.get("targetDate");
            targetDate = new Date(
                    ((Long) d.get("year")).intValue(),
                    ((Long) d.get("month")).intValue(),
                    ((Long) d.get("day")).intValue()
            );
        }

        Goal g = new Goal(id, title, note, isDaily, targetDate);

        if (map.get("lastCheckedDate") != null) {
            Map<String, Object> d = (Map<String, Object>) map.get("lastCheckedDate");
            g.setLastCheckedDate(new Date(
                    ((Long) d.get("year")).intValue(),
                    ((Long) d.get("month")).intValue(),
                    ((Long) d.get("day")).intValue()
            ));
        }
        return g;
    }
}