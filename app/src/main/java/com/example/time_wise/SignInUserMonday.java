package com.example.time_wise;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;

public class SignInUserMonday extends AppCompatActivity {

    EditText EffStartH, EffEndH;
    EditText BadStartH, BadEndH;
    EditText SleepStartH, SleepEndH;
    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.sign_in_user_monday);

        Intent lastIntent = getIntent();

        // EditText לשעות
        EffStartH = findViewById(R.id.EffStartH);
        EffEndH = findViewById(R.id.EffEndH);

        BadStartH = findViewById(R.id.UneffStartH);
        BadEndH = findViewById(R.id.UneffEndH);

        SleepStartH = findViewById(R.id.SleepStartH);
        SleepEndH = findViewById(R.id.SleepEndH);

        next = findViewById(R.id.btnNext);

        // Set TimePicker לכל EditText
        setTimePicker(EffStartH);
        setTimePicker(EffEndH);
        setTimePicker(BadStartH);
        setTimePicker(BadEndH);
        setTimePicker(SleepStartH);
        setTimePicker(SleepEndH);

        next.setOnClickListener(v -> {

            ArrayList<EfficientTime> effciency = (ArrayList<EfficientTime>) getIntent().getSerializableExtra("effciency");
            if (!isDayEfficientTimeEmpty()) {
                effciency.add(getDayEfficientTime());
            }

            ArrayList<EfficientTime> uneffciency = (ArrayList<EfficientTime>) getIntent().getSerializableExtra("uneffciency");
            if (!isDayBadTimeEmpty()) {
                uneffciency.add(getDayBadTime());
            }

            ArrayList<EfficientTime> sleep = (ArrayList<EfficientTime>) getIntent().getSerializableExtra("sleep");
            if (!isDaySleepTimeEmpty()) {
                sleep.add(getDaySleepTime());
            }

            String user = lastIntent.getStringExtra("username");
            String phone = lastIntent.getStringExtra("phoneNum");
            String pass = lastIntent.getStringExtra("password");

            Intent intent = new Intent(SignInUserMonday.this, SignInUserTuesday.class);
            intent.putExtra("effciency", effciency);
            intent.putExtra("uneffciency", uneffciency);
            intent.putExtra("sleep", sleep);
            intent.putExtra("username", user);
            intent.putExtra("phoneNum", phone);
            intent.putExtra("password", pass);
            startActivity(intent);
        });
    }

    private void setTimePicker(EditText editText) {
        editText.setOnClickListener(v -> {
            int hour = 12;
            int minute = 0;

            // אם כבר יש שעה – נשמור אותה
            String text = editText.getText().toString();
            if (!text.isEmpty()) {
                String[] parts = text.split(":");
                if (parts.length == 2) {
                    try {
                        hour = Integer.parseInt(parts[0]);
                        minute = Integer.parseInt(parts[1]);
                    } catch (Exception ignored) { }
                }
            }

            TimePickerDialog timePickerDialog = new TimePickerDialog(SignInUserMonday.this,
                    (TimePicker view, int hourOfDay, int minute1) -> {
                        String formatted = String.format("%02d:%02d", hourOfDay, minute1);
                        editText.setText(formatted);
                    }, hour, minute, true);

            timePickerDialog.show();
        });
    }

    // --- בניית אובייקטי EfficientTime ---
    private EfficientTime getDayEfficientTime() {
        LocalTime start = parseLocalTime(EffStartH);
        LocalTime end = parseLocalTime(EffEndH);
        return new EfficientTime(DayOfWeek.MONDAY, start, end);
    }

    private EfficientTime getDayBadTime() {
        LocalTime start = parseLocalTime(BadStartH);
        LocalTime end = parseLocalTime(BadEndH);
        return new EfficientTime(DayOfWeek.MONDAY, start, end);
    }

    private EfficientTime getDaySleepTime() {
        LocalTime start = parseLocalTime(SleepStartH);
        LocalTime end = parseLocalTime(SleepEndH);
        return new EfficientTime(DayOfWeek.MONDAY, start, end);
    }

    // --- בדיקות אם שדות ריקים ---
    private boolean isDayEfficientTimeEmpty() {
        if (EffStartH.getText().toString().isEmpty()) EffStartH.setError("Enter start time");
        if (EffEndH.getText().toString().isEmpty()) EffEndH.setError("Enter end time");
        return EffStartH.getText().toString().isEmpty() || EffEndH.getText().toString().isEmpty();
    }

    private boolean isDayBadTimeEmpty() {
        if (BadStartH.getText().toString().isEmpty()) BadStartH.setError("Enter start time");
        if (BadEndH.getText().toString().isEmpty()) BadEndH.setError("Enter end time");
        return BadStartH.getText().toString().isEmpty() || BadEndH.getText().toString().isEmpty();
    }

    private boolean isDaySleepTimeEmpty() {
        if (SleepStartH.getText().toString().isEmpty()) SleepStartH.setError("Enter start time");
        if (SleepEndH.getText().toString().isEmpty()) SleepEndH.setError("Enter end time");
        return SleepStartH.getText().toString().isEmpty() || SleepEndH.getText().toString().isEmpty();
    }

    private LocalTime parseLocalTime(EditText editText) {
        try {
            String[] parts = editText.getText().toString().split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            return LocalTime.of(hour, minute);
        } catch (Exception e) {
            return null;
        }
    }
}
