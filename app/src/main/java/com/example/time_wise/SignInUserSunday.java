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

public class SignInUserSunday extends AppCompatActivity {

    EditText EffStart, EffEnd;
    EditText BadStart, BadEnd;
    EditText SleepStart, SleepEnd;
    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent lastIntent = getIntent();
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.sign_in_user_sunday);

        // יעילות
        EffStart = findViewById(R.id.EffStartH);
        EffEnd = findViewById(R.id.EffEndH);

        // לא יעילות
        BadStart = findViewById(R.id.UneffStartH);
        BadEnd = findViewById(R.id.UneffEndH);

        // שינה
        SleepStart = findViewById(R.id.SleepStartH);
        SleepEnd = findViewById(R.id.SleepEndH);

        next = findViewById(R.id.btnNext);

        // Set TimePickers
        EffStart.setOnClickListener(v -> showTimePicker(EffStart));
        EffEnd.setOnClickListener(v -> showTimePicker(EffEnd));

        BadStart.setOnClickListener(v -> showTimePicker(BadStart));
        BadEnd.setOnClickListener(v -> showTimePicker(BadEnd));

        SleepStart.setOnClickListener(v -> showTimePicker(SleepStart));
        SleepEnd.setOnClickListener(v -> showTimePicker(SleepEnd));

        next.setOnClickListener(view -> {
            ArrayList<EfficientTime> effciency = new ArrayList<>();
            if (!isEmpty(EffStart, EffEnd)) {
                effciency.add(new EfficientTime(DayOfWeek.SUNDAY, parseTime(EffStart), parseTime(EffEnd)));
            }

            ArrayList<EfficientTime> uneffciency = new ArrayList<>();
            if (!isEmpty(BadStart, BadEnd)) {
                uneffciency.add(new EfficientTime(DayOfWeek.SUNDAY, parseTime(BadStart), parseTime(BadEnd)));
            }

            ArrayList<EfficientTime> sleep = new ArrayList<>();
            if (!isEmpty(SleepStart, SleepEnd)) {
                sleep.add(new EfficientTime(DayOfWeek.SUNDAY, parseTime(SleepStart), parseTime(SleepEnd)));
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
        });
    }

    private void showTimePicker(EditText editText) {
        int hour = 12;
        int minute = 0;

        // אם כבר יש שעה כתובה, נפרש אותה
        String timeStr = editText.getText().toString();
        if (!timeStr.isEmpty()) {
            String[] parts = timeStr.split(":");
            if (parts.length == 2) {
                try {
                    hour = Integer.parseInt(parts[0]);
                    minute = Integer.parseInt(parts[1]);
                } catch (NumberFormatException ignored) {}
            }
        }

        TimePickerDialog picker = new TimePickerDialog(this, (view, hourOfDay, minuteOfHour) -> {
            editText.setText(String.format("%02d:%02d", hourOfDay, minuteOfHour));
        }, hour, minute, true);
        picker.show();
    }

    private boolean isEmpty(EditText start, EditText end) {
        if (start.getText().toString().isEmpty()) start.setError("Enter start time");
        if (end.getText().toString().isEmpty()) end.setError("Enter end time");
        return start.getText().toString().isEmpty() || end.getText().toString().isEmpty();
    }

    private LocalTime parseTime(EditText editText) {
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
