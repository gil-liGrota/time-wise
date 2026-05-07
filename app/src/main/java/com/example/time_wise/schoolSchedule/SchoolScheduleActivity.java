package com.example.time_wise.schoolSchedule;

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

import com.example.time_wise.Constant;
import com.example.time_wise.R;
import com.example.time_wise.calender.CalendarActivity;
import com.example.time_wise.enterApp.HomeActivity;
import com.example.time_wise.enterApp.LogInActivity;
import com.example.time_wise.followEfficiency.EfficiencyActivity;
import com.example.time_wise.followGoal.GoalsActivity;
import com.example.time_wise.notes.NotesActivity;
import com.example.time_wise.task.TasksActivity;
import com.example.time_wise.todo.TodosActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchoolScheduleActivity extends AppCompatActivity {
    private Button btnSave;
    private Intent intent;
    private LinearLayout sideMenu;
    private TextView btnMenu;
    private String userID;
    private String currentDayName = "Sunday";
    private LessonAdapter adapter;
    private Map<String, SchoolDay> weekSchedule = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_schedule);

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
                                weekSchedule.put(dayName, new SchoolDay(dayName, lessonsListForThisDay));
                            }
                        }
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
                })
                .addOnFailureListener(e -> {
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
                case Home: intent = new Intent(SchoolScheduleActivity.this, HomeActivity.class);break;
                case ACTIVITY: intent = new Intent(SchoolScheduleActivity.this, TasksActivity.class);break;
                case SCHOOL_SCHEDULE: intent = new Intent(SchoolScheduleActivity.this, SchoolScheduleActivity.class);break;
                case FOLLOW_EFFICIENCY: intent = new Intent(SchoolScheduleActivity.this, EfficiencyActivity.class);break;
                case TODO: intent = new Intent(SchoolScheduleActivity.this, TodosActivity.class);break;
                case CALENDER: intent = new Intent(SchoolScheduleActivity.this, CalendarActivity.class);break;
                case NOTES: intent = new Intent(SchoolScheduleActivity.this, NotesActivity.class);break;
                case FOLLOW_GOAL: intent = new Intent(SchoolScheduleActivity.this, GoalsActivity.class);break;
                case SIGN_OUT: intent = new Intent(SchoolScheduleActivity.this, LogInActivity.class);break;
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
