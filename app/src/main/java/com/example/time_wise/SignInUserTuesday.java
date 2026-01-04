package com.example.time_wise;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;

public class SignInUserTuesday extends AppCompatActivity {

    EditText EffStart, EffEnd;
    EditText BadStart, BadEnd;
    EditText SleepStart, SleepEnd;
    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.sign_in_user_tuesday);

        Intent lastIntent = getIntent();

        // EditText לשעות
        EffStart = findViewById(R.id.EffStartH);
        EffEnd = findViewById(R.id.EffEndH);

        BadStart = findViewById(R.id.UneffStartH);
        BadEnd = findViewById(R.id.UneffEndH);

        SleepStart = findViewById(R.id.SleepStartH);
        SleepEnd = findViewById(R.id.SleepEndH);

        next = findViewById(R.id.btnNext);

        // Set TimePicker לכל EditText
        setTimePicker(EffStart);
        setTimePicker(EffEnd);
        setTimePicker(BadStart);
        setTimePicker(BadEnd);
        setTimePicker(SleepStart);
        setTimePicker(SleepEnd);

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
            ArrayList<SchoolDay> schoolDays = (ArrayList<SchoolDay>) getIntent().getSerializableExtra("school");

            Intent intent = new Intent(SignInUserTuesday.this, SignInUserWednesday.class);
            intent.putExtra("school", schoolDays);
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
                    } catch (Exception ignored) {}
                }
            }

            TimePickerDialog timePickerDialog = new TimePickerDialog(SignInUserTuesday.this,
                    (TimePicker view, int hourOfDay, int minute1) -> {
                        String formatted = String.format("%02d:%02d", hourOfDay, minute1);
                        editText.setText(formatted);
                    }, hour, minute, true);

            timePickerDialog.show();
        });
    }

    // --- בניית אובייקטי EfficientTime ---
    private EfficientTime getDayEfficientTime() {
        LocalTime start = parseLocalTime(EffStart);
        LocalTime end = parseLocalTime(EffEnd);
        return new EfficientTime(DayOfWeek.TUESDAY, start, end);
    }

    private EfficientTime getDayBadTime() {
        LocalTime start = parseLocalTime(BadStart);
        LocalTime end = parseLocalTime(BadEnd);
        return new EfficientTime(DayOfWeek.TUESDAY, start, end);
    }

    private EfficientTime getDaySleepTime() {
        LocalTime start = parseLocalTime(SleepStart);
        LocalTime end = parseLocalTime(SleepEnd);
        return new EfficientTime(DayOfWeek.TUESDAY, start, end);
    }

    // --- בדיקות אם שדות ריקים ---
    private boolean isDayEfficientTimeEmpty() {
        if (EffStart.getText().toString().isEmpty()) EffStart.setError("Enter start time");
        if (EffEnd.getText().toString().isEmpty()) EffEnd.setError("Enter end time");
        return EffStart.getText().toString().isEmpty() || EffEnd.getText().toString().isEmpty();
    }

    private boolean isDayBadTimeEmpty() {
        if (BadStart.getText().toString().isEmpty()) BadStart.setError("Enter start time");
        if (BadEnd.getText().toString().isEmpty()) BadEnd.setError("Enter end time");
        return BadStart.getText().toString().isEmpty() || BadEnd.getText().toString().isEmpty();
    }

    private boolean isDaySleepTimeEmpty() {
        if (SleepStart.getText().toString().isEmpty()) SleepStart.setError("Enter start time");
        if (SleepEnd.getText().toString().isEmpty()) SleepEnd.setError("Enter end time");
        return SleepStart.getText().toString().isEmpty() || SleepEnd.getText().toString().isEmpty();
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
