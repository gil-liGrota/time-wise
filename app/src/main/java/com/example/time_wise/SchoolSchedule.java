package com.example.time_wise;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchoolSchedule extends AppCompatActivity {
    private Button btnSave;
    private Intent intent;
    private LinearLayout sideMenu;
    private TextView btnMenu;
    private String userID;
    private String userName, password;
    private String currentDayName = "Sunday";
    private LessonAdapter adapter;
    private Map<String, SchoolDay> weekSchedule = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.school_schedule);

        btnSave = findViewById(R.id.btnSaveSchedule);
        btnSave.setOnClickListener(v -> saveScheduleToFirebase());

        Button btnAddLesson = findViewById(R.id.btnAddLesson);
        btnAddLesson.setOnClickListener(v -> {
            SchoolDay day = weekSchedule.get(currentDayName);
            if (day != null) {
                ArrayList<Lesson> lessons = day.getLessons();
                int newHour = lessons.size() + 1;
                lessons.add(new Lesson(newHour, ""));
                adapter.setData(lessons);
            }
        });

        Intent lastIntent = getIntent();
        userID = lastIntent.getStringExtra("userId");
        Constant.USER_ID = userID;
        userName = lastIntent.getStringExtra("username");
        password = lastIntent.getStringExtra("password");

        sideMenu = findViewById(R.id.sideMenu);
        btnMenu = findViewById(R.id.btnMenu);



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
        setMenuClickListener(R.id.nav_sign_out, Constant.Menu.SIGN_OUT);



        RecyclerView recyclerView = findViewById(R.id.scheduleRecycler);
        adapter = new LessonAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        db.collection("users").document(userID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()) {
                        List<Map<String, Object>> schoolScheduleMap =
                                (List<Map<String, Object>>) documentSnapshot.get("schoolSchedule");

                        if (schoolScheduleMap != null) {
                            // ניקוי המפה לפני טעינה חדשה
                            weekSchedule.clear();

                            for (Map<String, Object> dayMap : schoolScheduleMap) {
                                String dayName = (String) dayMap.get("dayName");
                                List<Map<String, Object>> lessonsMapList = (List<Map<String, Object>>) dayMap.get("lessons");

                                ArrayList<Lesson> lessonsListForThisDay = new ArrayList<>();
                                if (lessonsMapList != null) {
                                    for (Map<String, Object> lessonMap : lessonsMapList) {
                                        int number = ((Long) lessonMap.get("hour")).intValue();
                                        String name = (String) lessonMap.get("name");
                                        lessonsListForThisDay.add(new Lesson(number, name));
                                    }
                                }
                                // שמירה למפה עם השם המדויק מה-DB
                                weekSchedule.put(dayName, new SchoolDay(dayName, lessonsListForThisDay));
                            }
                        }
                        // הצגת יום ראשון אחרי שהנתונים נטענו
                        showDay("Sunday");
                    }
                });

        ((Button)findViewById(R.id.btnSun)).setOnClickListener(v -> showDay("Sunday"));
        ((Button)findViewById(R.id.btnMon)).setOnClickListener(v -> showDay("Monday"));
        ((Button)findViewById(R.id.btnTue)).setOnClickListener(v -> showDay("Tuesday"));
        ((Button)findViewById(R.id.btnWed)).setOnClickListener(v -> showDay("Wednesday"));
        ((Button)findViewById(R.id.btnThu)).setOnClickListener(v -> showDay("Thursday"));
        ((Button)findViewById(R.id.btnFri)).setOnClickListener(v -> showDay("Friday"));

        showDay("Sun");
    }
    private void saveScheduleToFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ArrayList<SchoolDay> updatedList = new ArrayList<>(weekSchedule.values());

        db.collection("users").document(userID)
                .update("schoolSchedule", updatedList)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Schedule Updated Successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show();
                });
    }

    private void showDay(String dayName) {
        currentDayName = dayName;
        SchoolDay day = weekSchedule.get(dayName);
        if(day != null) {
            adapter.setData(day.getLessons());
        }
    }


    private void setMenuClickListener(int id, Constant.Menu menu) {
        TextView item = findViewById(id);

        item.setOnClickListener(v -> {
            switch (menu){
                case Home:
                    intent = new Intent(SchoolSchedule.this, HomeScreen.class);
                    Toast.makeText(this, "home", Toast.LENGTH_SHORT).show();
                    break;

                case ACTIVITY:
                    intent = new Intent(SchoolSchedule.this, TasksScreem.class);
                    break;

                case SCHOOL_SCHEDULE:
                    intent = new Intent(SchoolSchedule.this, SchoolSchedule.class);
                    break;

                case FOLLOW_EFFICIENCY:
                    intent = new Intent(SchoolSchedule.this, EfficiencyActivity.class);
                    Toast.makeText(this, "Follow Efficiency", Toast.LENGTH_SHORT).show();
                    break;

                case TODO:
                    intent = new Intent(SchoolSchedule.this, Todos.class);
                    Toast.makeText(this, "To-Do", Toast.LENGTH_SHORT).show();
                    break;

                case CALENDER:
                    intent = new Intent(SchoolSchedule.this, CalendarActivity.class);
                    Toast.makeText(this, "Calendar", Toast.LENGTH_SHORT).show();
                    break;

                case NOTES:
                    intent = new Intent(SchoolSchedule.this, NotesActivity.class);
                    Toast.makeText(this, "Notes", Toast.LENGTH_SHORT).show();
                    break;

                case LERNING_PLAN:
                    intent = new Intent(SchoolSchedule.this, HomeScreen.class);
                    Toast.makeText(this, "Learning Plan", Toast.LENGTH_SHORT).show();
                    break;

                case FOLLOW_GOAL:
                    intent = new Intent(SchoolSchedule.this, GoalsActivity.class);
                    Toast.makeText(this, "Follow Goal", Toast.LENGTH_SHORT).show();
                    break;

                case SIGN_OUT:
                    intent = new Intent(SchoolSchedule.this, log_in.class);
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
}
