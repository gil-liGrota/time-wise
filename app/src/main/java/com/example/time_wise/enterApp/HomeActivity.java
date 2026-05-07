package com.example.time_wise.enterApp;

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

import com.example.time_wise.Constant;
import com.example.time_wise.R;
import com.example.time_wise.calender.CalendarActivity;
import com.example.time_wise.followEfficiency.AlarmReceiver;
import com.example.time_wise.followEfficiency.EfficiencyActivity;
import com.example.time_wise.followGoal.GoalsActivity;
import com.example.time_wise.notes.NotesActivity;
import com.example.time_wise.schoolSchedule.SchoolScheduleActivity;
import com.example.time_wise.task.TasksActivity;
import com.example.time_wise.todo.TodosActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {
    private Intent intent;
    private LinearLayout sideMenu;
    private TextView btnMenu;
    private String userID;
    private String userName, password;
    private FirebaseFirestore db;
    private int hour = 17;
    private int min = 0;
    private int sec = 0;
    private TextView tvUsernameDisplay, tvPhoneDisplay, tvNotificationTime;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        startDailyAlarm();
        Intent lastIntent = getIntent();
        userID = lastIntent.getStringExtra("userId");
        Constant.USER_ID = userID;
        userName = lastIntent.getStringExtra("username");
        password = lastIntent.getStringExtra("password");
        sideMenu = findViewById(R.id.sideMenu);
        btnMenu = findViewById(R.id.btnMenu);
        tvUsernameDisplay = findViewById(R.id.tvUsernameDisplay);
        tvPhoneDisplay = findViewById(R.id.tvPhoneDisplay);
        tvNotificationTime = findViewById(R.id.tvNotificationTime);

        fetchData();
        tvNotificationTime.setOnClickListener(v -> showTimePicker());

        if(userID == null){
            getUserIdByUsername(userName);
        }

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

    }

    private void fetchData(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(userID)
                .get()
                .addOnSuccessListener(doc -> {
                    if(doc.exists()){
                        String name = doc.getString("userName");
                        String phone = doc.getString("phoneNumber");
                        if(doc.getLong("notificationHour") != null && doc.getLong("notificationMinute") != null){
                            hour = doc.getLong("notificationHour").intValue();
                            min = doc.getLong("notificationMinute").intValue();

                        }

                        tvPhoneDisplay.setText("phone number: " + phone);
                        tvUsernameDisplay.setText("username: " + name);
                        tvNotificationTime.setText(hour + ":" + min);
                    }
                });


    }

    private void showTimePicker() {
        android.app.TimePickerDialog timePickerDialog = new android.app.TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    this.hour = hourOfDay;
                    this.min = minute;

                    String timeStr = String.format("%02d:%02d", hourOfDay, minute);
                    tvNotificationTime.setText(timeStr);

                    startDailyAlarm();
                    updateNotificationTimeInDB(hourOfDay, minute);
                }, hour, min, true);
        timePickerDialog.show();
    }

    private void updateNotificationTimeInDB(int h, int m) {
        if (userID != null) {
            FirebaseFirestore.getInstance().collection("users").document(userID)
                    .update("notificationHour", h, "notificationMinute", m);
        }
    }

    private void startDailyAlarm() {//TODO change to right hour
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, sec);

        if (Calendar.getInstance().after(calendar)) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        android.app.AlarmManager alarmManager = (android.app.AlarmManager) getSystemService(Context.ALARM_SERVICE);

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
                case Home: intent = new Intent(HomeActivity.this, HomeActivity.class);break;
                case ACTIVITY: intent = new Intent(HomeActivity.this, TasksActivity.class);break;
                case SCHOOL_SCHEDULE: intent = new Intent(HomeActivity.this, SchoolScheduleActivity.class);break;
                case FOLLOW_EFFICIENCY: intent = new Intent(HomeActivity.this, EfficiencyActivity.class);break;
                case TODO: intent = new Intent(HomeActivity.this, TodosActivity.class);break;
                case CALENDER: intent = new Intent(HomeActivity.this, CalendarActivity.class);break;
                case NOTES: intent = new Intent(HomeActivity.this, NotesActivity.class);break;
                case FOLLOW_GOAL: intent = new Intent(HomeActivity.this, GoalsActivity.class);break;
                case SIGN_OUT: intent = new Intent(HomeActivity.this, LogInActivity.class);break;
                default:
                    intent = new Intent(HomeActivity.this, HomeActivity.class);
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

                    userID = query.getDocuments().get(0).getId();

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error connecting to database: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
