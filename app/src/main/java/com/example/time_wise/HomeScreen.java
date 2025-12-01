package com.example.time_wise;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HomeScreen extends AppCompatActivity {

    private LinearLayout sideMenu;
    private TextView btnMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

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

        // טיפול בלחיצה על כל פריט
        setMenuClickListener(R.id.nav_home, true); // Home → נשאר באותו מסך
        setMenuClickListener(R.id.nav_activity, false);
        setMenuClickListener(R.id.nav_school_schedule, false);
        setMenuClickListener(R.id.nav_follow_efficiency, false);
        setMenuClickListener(R.id.nav_todo, false);
        setMenuClickListener(R.id.nav_calendar, false);
        setMenuClickListener(R.id.nav_notes, false);
        setMenuClickListener(R.id.nav_learning_plan, false);
        setMenuClickListener(R.id.nav_follow_goal, false);
    }

    private void setMenuClickListener(int id, boolean isHome) {
        TextView item = findViewById(id);
        item.setOnClickListener(v -> {
            if (isHome) {
                Toast.makeText(HomeScreen.this, "Already on Home", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(HomeScreen.this, "Clicked: " + item.getText(), Toast.LENGTH_SHORT).show();
                // כאן תוכל לפתוח פעילויות אחרות, לדוגמה:
                // startActivity(new Intent(HomeActivity.this, SomeActivity.class));
            }
            sideMenu.setVisibility(View.GONE);
        });
    }
}
