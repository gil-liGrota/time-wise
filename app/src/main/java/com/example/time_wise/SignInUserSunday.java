package com.example.time_wise;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SignInUserSunday extends AppCompatActivity {

    EditText EffStartH, EffStartM, EffEndH, EffEndM;
    EditText BadStartH, BadStartM, BadEndH, BadEndM;
    EditText SleepStartH, SleepStartM, SleepEndH, SleepEndM;
    private Button next;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent lastIntent = getIntent();
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.sign_in_user_sunday);

        EffStartH = findViewById(R.id.EffStartH);
        EffStartM = findViewById(R.id.EffStartM);
        EffEndH = findViewById(R.id.EffEndH);
        EffEndM = findViewById(R.id.EffEndtM);

        BadStartH = findViewById(R.id.UneffStartH);
        BadStartM = findViewById(R.id.UneffStartM);
        BadEndH = findViewById(R.id.UneffEndH);
        BadEndM = findViewById(R.id.UneffEndM);

        SleepStartH = findViewById(R.id.SleepStartH);
        SleepStartM = findViewById(R.id.SleepStartM);
        SleepEndH = findViewById(R.id.SleepEndH);
        SleepEndM = findViewById(R.id.SleepEndM);

        next = findViewById(R.id.btnNext);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isdaySleepTimeEmpty() || !isdayBadTimeEmpty() || !isdayEfficientTimeEmpty()) {
                    System.out.println("in");
                    ArrayList<EfficientTime> effciency = new ArrayList<>();
                    if (!isdayEfficientTimeEmpty()) {
                        effciency.add(getdayEfficientTime());
                    }

                    ArrayList<EfficientTime> uneffciency = new ArrayList<>();
                    if (!isdayBadTimeEmpty()) {
                        uneffciency.add(getdayBadTime());
                    }

                    ArrayList<EfficientTime> sleep = new ArrayList<>();
                    if (!isdaySleepTimeEmpty()) {
                        sleep.add(getdaySleepTime());
                    }

                    String user = lastIntent.getStringExtra("username");
                    String phone = lastIntent.getStringExtra("phoneNum");
                    String pass = lastIntent.getStringExtra("password");
                    Intent intent = new Intent(SignInUserSunday.this, SignInUserMonday.class);
                    intent.putExtra("effciency", effciency);
                    intent.putExtra("uneffciency", uneffciency);
                    intent.putExtra("sleep", sleep);
                    intent.putExtra("username", user);
                    intent.putExtra("phoneNum", phone);
                    intent.putExtra("password", pass);
                    startActivity(intent);
                }
            }
        });

    }

    private EfficientTime getdayEfficientTime() {
        LocalTime start = buildLocalTime(EffStartH, EffStartM);
        LocalTime end = buildLocalTime(EffEndH, EffEndM);

        return new EfficientTime(DayOfWeek.SUNDAY, start, end);
    }

    private boolean isdayEfficientTimeEmpty(){
        if (EffStartH.getText().toString().isEmpty()){
            EffStartH.setError("enter start hour");
        }
        if (EffStartM.getText().toString().isEmpty()){
            EffStartM.setError("enter start minute");
        }
        if(EffEndH.getText().toString().isEmpty()){
            EffEndH.setError("enter end hour");
        }
        if(EffEndM.getText().toString().isEmpty()){
            EffEndM.setError("enter end minute");
        }

        return EffStartH.getText().toString().isEmpty() || EffEndH.getText().toString().isEmpty() || EffStartM.getText().toString().isEmpty() || EffEndM.getText().toString().isEmpty();
    }


    private EfficientTime getdayBadTime() {
        LocalTime start = buildLocalTime(BadStartH, BadStartM);
        LocalTime end = buildLocalTime(BadEndH, BadEndM);

        return new EfficientTime(DayOfWeek.SUNDAY, start, end);
    }

    private boolean isdayBadTimeEmpty(){
        if (BadStartH.getText().toString().isEmpty()){
            BadStartH.setError("enter start hour");
        }
        if (BadStartM.getText().toString().isEmpty()){
            BadStartM.setError("enter start minute");
        }
        if(BadEndH.getText().toString().isEmpty()){
            BadEndH.setError("enter end hour");
        }
        if(BadEndM.getText().toString().isEmpty()){
            BadEndM.setError("enter end minute");
        }

        return BadStartH.getText().toString().isEmpty() || BadEndH.getText().toString().isEmpty() || BadStartM.getText().toString().isEmpty() || BadEndM.getText().toString().isEmpty();
    }


    private EfficientTime getdaySleepTime() {
        LocalTime start = buildLocalTime(SleepStartH, SleepStartM);
        LocalTime end = buildLocalTime(SleepEndH, SleepEndM);

        return new EfficientTime(DayOfWeek.SUNDAY, start, end);
    }

    private boolean isdaySleepTimeEmpty(){
        if (SleepStartH.getText().toString().isEmpty()){
            SleepStartH.setError("enter start hour");
        }
        if (SleepStartM.getText().toString().isEmpty()){
            SleepStartM.setError("enter start minute");
        }
        if(SleepEndH.getText().toString().isEmpty()){
            SleepEndH.setError("enter end hour");
        }
        if(SleepEndM.getText().toString().isEmpty()){
            SleepEndM.setError("enter end minute");
        }

        return SleepStartH.getText().toString().isEmpty() || SleepEndH.getText().toString().isEmpty() || SleepStartM.getText().toString().isEmpty() || SleepEndM.getText().toString().isEmpty();
    }

    private LocalTime buildLocalTime(EditText hourET, EditText minuteET) {
        String hourStr = hourET.getText().toString().trim();
        String minStr  = minuteET.getText().toString().trim();

        // אם אחד מהם ריק – החזרת null והצגת שגיאה
        if (hourStr.isEmpty()) {
            hourET.setError("Enter hour");
            return null;
        }
        if (minStr.isEmpty()) {
            minuteET.setError("Enter minutes");
            return null;
        }

        try {
            int hour = Integer.parseInt(hourStr);
            int minute = Integer.parseInt(minStr);

            // בדיקת טווחים
            if (hour < 0 || hour > 23) {
                hourET.setError("Hour must be 0-23");
                hourET.setText("");
                return null;
            }
            if (minute < 0 || minute > 59) {
                minuteET.setError("Minutes must be 0-59");
                minuteET.setText("");
                return null;
            }

            return LocalTime.of(hour, minute);

        } catch (Exception e) {
            hourET.setError("Invalid time");
            minuteET.setError("Invalid time");
            return null;
        }
    }
}