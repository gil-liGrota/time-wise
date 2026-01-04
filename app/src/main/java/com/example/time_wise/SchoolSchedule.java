package com.example.time_wise;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchoolSchedule extends AppCompatActivity {

    private Intent intent;
    private LinearLayout sideMenu;
    private TextView btnMenu;
    private String userID;
    private String userName, password;

    private LessonAdapter adapter;

    // השבוע: שם יום -> SchoolDay
    private Map<String, SchoolDay> weekSchedule = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.school_schedule);

        // קבלת פרטי המשתמש מהIntent
        Intent lastIntent = getIntent();
        userID = lastIntent.getStringExtra("userId");
        Constant.USER_ID = userID;
        userName = lastIntent.getStringExtra("username");
        password = lastIntent.getStringExtra("password");

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

        // חיבור RecyclerView וה-Adapter
        RecyclerView recyclerView = findViewById(R.id.scheduleRecycler);
        adapter = new LessonAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        // יצירת ה-SchoolDay לכל יום עם שיעורים לדוגמה
//        weekSchedule.put("Sun", new SchoolDay("Sun"));
//        weekSchedule.get("Sun").setLessons(Arrays.asList(
//                new Lesson(1, "מתמטיקה"),
//                new Lesson(2, "עברית"),
//                new Lesson(3, "אנגלית"),
//                new Lesson(4, "היסטוריה"),
//                new Lesson(5, "גיאוגרפיה")
//        ));
//
//        weekSchedule.put("Mon", new SchoolDay("Mon"));
//        weekSchedule.get("Mon").setLessons(Arrays.asList(
//                new Lesson(1, "פיזיקה"),
//                new Lesson(2, "כימיה"),
//                new Lesson(3, "ספורט"),
//                new Lesson(4, "תנ\"ך"),
//                new Lesson(5, "מוזיקה")
//        ));
//
//        weekSchedule.put("Tue", new SchoolDay("Tue"));
//        weekSchedule.get("Tue").setLessons(Arrays.asList(
//                new Lesson(1, "מתמטיקה"),
//                new Lesson(2, "אנגלית"),
//                new Lesson(3, "תכנות"),
//                new Lesson(4, "עברית"),
//                new Lesson(5, "גיאוגרפיה")
//        ));
//
//        weekSchedule.put("Wed", new SchoolDay("Wed"));
//        weekSchedule.get("Wed").setLessons(Arrays.asList(
//                new Lesson(1, "ספורט"),
//                new Lesson(2, "כימיה"),
//                new Lesson(3, "היסטוריה"),
//                new Lesson(4, "מוזיקה"),
//                new Lesson(5, "אנגלית")
//        ));
//
//        weekSchedule.put("Thu", new SchoolDay("Thu"));
//        weekSchedule.get("Thu").setLessons(Arrays.asList(
//                new Lesson(1, "תנ\"ך"),
//                new Lesson(2, "מתמטיקה"),
//                new Lesson(3, "פיזיקה"),
//                new Lesson(4, "אנגלית"),
//                new Lesson(5, "תכנות")
//        ));
//
//        weekSchedule.put("Fri", new SchoolDay("Fri"));
//        weekSchedule.get("Fri").setLessons(Arrays.asList(
//                new Lesson(1, "מוזיקה"),
//                new Lesson(2, "תנ\"ך"),
//                new Lesson(3, "ספורט"),
//                new Lesson(4, "כימיה"),
//                new Lesson(5, "גיאוגרפיה")
//        ));

        // חיבור כפתורי ימים ל-Adapter
        ((Button)findViewById(R.id.btnSun)).setOnClickListener(v -> showDay("Sun"));
        ((Button)findViewById(R.id.btnMon)).setOnClickListener(v -> showDay("Mon"));
        ((Button)findViewById(R.id.btnTue)).setOnClickListener(v -> showDay("Tue"));
        ((Button)findViewById(R.id.btnWed)).setOnClickListener(v -> showDay("Wed"));
        ((Button)findViewById(R.id.btnThu)).setOnClickListener(v -> showDay("Thu"));
        ((Button)findViewById(R.id.btnFri)).setOnClickListener(v -> showDay("Fri"));

        // הצגת יום ראשון כברירת מחדל
        showDay("Sun");
    }

    // עדכון ה-Adapter לפי יום
    private void showDay(String dayName) {
        SchoolDay day = weekSchedule.get(dayName);
        if(day != null) {
            // יצירת רשימת שמות שיעורים ידנית
            List<String> lessonNames = new ArrayList<>();
            for (Lesson lesson : day.getLessons()) {
                lessonNames.add(lesson.getName());
            }
            adapter.setData(lessonNames);
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
                    // אם אנחנו כבר במסך מערכת שעות, לא עושים כלום
                    sideMenu.setVisibility(View.GONE);
                    return;

                case FOLLOW_EFFICIENCY:
                    intent = new Intent(SchoolSchedule.this, HomeScreen.class);
                    Toast.makeText(this, "Follow Efficiency", Toast.LENGTH_SHORT).show();
                    break;

                case TODO:
                    intent = new Intent(SchoolSchedule.this, HomeScreen.class);
                    Toast.makeText(this, "To-Do", Toast.LENGTH_SHORT).show();
                    break;

                case CALENDER:
                    intent = new Intent(SchoolSchedule.this, HomeScreen.class);
                    Toast.makeText(this, "Calendar", Toast.LENGTH_SHORT).show();
                    break;

                case NOTES:
                    intent = new Intent(SchoolSchedule.this, HomeScreen.class);
                    Toast.makeText(this, "Notes", Toast.LENGTH_SHORT).show();
                    break;

                case LERNING_PLAN:
                    intent = new Intent(SchoolSchedule.this, HomeScreen.class);
                    Toast.makeText(this, "Learning Plan", Toast.LENGTH_SHORT).show();
                    break;

                case FOLLOW_GOAL:
                    intent = new Intent(SchoolSchedule.this, HomeScreen.class);
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
}
