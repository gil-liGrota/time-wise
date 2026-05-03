package com.example.time_wise;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class School extends AppCompatActivity {

    private EditText[] editHours = new EditText[11];
    private TextView txtDayTitle;
    private Button btnNext;

    private String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    private int currentDayIndex = 0;
    private ArrayList<SchoolDay> allWeekSchedule = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_school);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleNextDayLogic();
            }
        });
    }

    private void initViews() {
        txtDayTitle = findViewById(R.id.txtDayTitle);
        btnNext = findViewById(R.id.btnNext);

        txtDayTitle.setText(days[currentDayIndex]);

        for (int i = 0; i < 11; i++) {
            String idName = "edtHour" + (i + 1);
            int resID = getResources().getIdentifier(idName, "id", getPackageName());
            editHours[i] = findViewById(resID);
        }
    }

    private void handleNextDayLogic() {
        ArrayList<Lesson> dailyLessons = new ArrayList<>();

        for (int i = 0; i < editHours.length; i++) {
            String lessonName = editHours[i].getText().toString().trim();
            if (!lessonName.isEmpty()) {
                dailyLessons.add(new Lesson(i + 1, lessonName));
            }
        }

        if (dailyLessons.isEmpty()) {
            Toast.makeText(this, "Please enter at least one lesson", Toast.LENGTH_SHORT).show();
            return;
        }

        allWeekSchedule.add(new SchoolDay(days[currentDayIndex], dailyLessons));

        if (currentDayIndex < days.length - 1) {
            updateUIForNextDay();
        } else {
            finishSetup();
        }
    }

    private void updateUIForNextDay() {
        currentDayIndex++;

        txtDayTitle.setText(days[currentDayIndex]);

        for (EditText et : editHours) {
            et.setText("");
        }

        if (currentDayIndex == days.length - 1) {
            btnNext.setText("Finish");
        }

        editHours[0].requestFocus();
    }

    private void finishSetup() {
        Intent lastIntent = getIntent();
        String user = lastIntent.getStringExtra("username");
        String phone = lastIntent.getStringExtra("phoneNum");
        String pass = lastIntent.getStringExtra("password");

        Intent intent = new Intent(School.this, SignInDetails.class);//change from SignInDetails to HomeScreen

        intent.putExtra("school", allWeekSchedule);
        intent.putExtra("username", user);
        intent.putExtra("phoneNum", phone);
        intent.putExtra("password", pass);

        startActivity(intent);
        finish();
    }
}