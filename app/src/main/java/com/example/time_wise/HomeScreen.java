package com.example.time_wise;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class HomeScreen extends AppCompatActivity {
    private Intent intent;
    private LinearLayout sideMenu;
    private TextView btnMenu;
    private String userID;
    private String userName, password;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);
        startDailyAlarm();
        Intent lastIntent = getIntent();
        userID = lastIntent.getStringExtra("userId");
        Constant.USER_ID = userID;
        userName = lastIntent.getStringExtra("username");
        password = lastIntent.getStringExtra("password");
        sideMenu = findViewById(R.id.sideMenu);
        btnMenu = findViewById(R.id.btnMenu);

        if(userID == null){
            getUserIdByUsername(userName);
        }

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

    private void startDailyAlarm() {//TODO change to right hour
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // אם השעה כבר אחרי אחת בצהריים, נקבע למחר
        if (Calendar.getInstance().after(calendar)) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        Intent intent = new Intent(this, AlarmReceiver.class);
        // דגל FLAG_IMMUTABLE חובה באנדרואיד חדש
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        android.app.AlarmManager alarmManager = (android.app.AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // הגדרה שתחזור על עצמה כל יום
        if (alarmManager != null) {
            alarmManager.setRepeating(android.app.AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    android.app.AlarmManager.INTERVAL_DAY,
                    pendingIntent);
        }
    }

    private void setMenuClickListener(int id, Constant.Menu menu) {
        TextView item = findViewById(id);

        item.setOnClickListener(v -> {
            switch (menu){
                case Home:
                    intent = new Intent(HomeScreen.this, HomeScreen.class);
                    Toast.makeText(this, "home", Toast.LENGTH_LONG).show();
                    break;
                case ACTIVITY:
                    intent = new Intent(HomeScreen.this, TasksScreem.class);
                    break;
                case SCHOOL_SCHEDULE:
                    intent = new Intent(HomeScreen.this, SchoolSchedule.class);
                    Toast.makeText(this, "School Schedule", Toast.LENGTH_LONG).show();
                    break;

                case FOLLOW_EFFICIENCY:
                    intent = new Intent(HomeScreen.this, EfficiencyActivity.class);
                    Toast.makeText(this, "Follow Efficiency", Toast.LENGTH_LONG).show();
                    break;

                case TODO:
                    intent = new Intent(HomeScreen.this, Todos.class);
                    Toast.makeText(this, "To-Do", Toast.LENGTH_LONG).show();
                    break;

                case CALENDER:
                    intent = new Intent(HomeScreen.this, CalendarActivity.class);
                    Toast.makeText(this, "Calendar", Toast.LENGTH_LONG).show();
                    break;

                case NOTES:
                    intent = new Intent(HomeScreen.this, NotesActivity.class);
                    Toast.makeText(this, "Notes", Toast.LENGTH_LONG).show();
                    break;

                case LERNING_PLAN:
                    intent = new Intent(HomeScreen.this, HomeScreen.class);
                    Toast.makeText(this, "Learning Plan", Toast.LENGTH_LONG).show();
                    break;

                case FOLLOW_GOAL:
                    intent = new Intent(HomeScreen.this, GoalsActivity.class);
                    Toast.makeText(this, "Follow Goal", Toast.LENGTH_LONG).show();
                    break;
                default:
                    intent = new Intent(HomeScreen.this, HomeScreen.class);
                    Toast.makeText(this, "not working", Toast.LENGTH_LONG).show();
                    break;
            }
            intent.putExtra("userId", userID);
            startActivity(intent);
            sideMenu.setVisibility(View.GONE);
        });
    }

    private void getUserIdByUsername(String username) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("userName", username)
                .get()
                .addOnSuccessListener(query -> {
                    if (query.isEmpty()) {
                        Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // משתמש קיים → מקבלים את ה-ID
                    userID = query.getDocuments().get(0).getId();

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error connecting to database: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }



}
