package com.example.time_wise;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;

public class SignInUserSaturday extends AppCompatActivity {

    private EditText EffStart, EffEnd;
    private EditText BadStart, BadEnd;
    private EditText SleepStart, SleepEnd;
    private Button next;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.sign_in_user_saturday);

        Intent lastIntent = getIntent();

        // אתחול EditText
        EffStart = findViewById(R.id.EffStartH);
        EffEnd = findViewById(R.id.EffEndH);

        BadStart = findViewById(R.id.UneffStartH);
        BadEnd = findViewById(R.id.UneffEndH);

        SleepStart = findViewById(R.id.SleepStartH);
        SleepEnd = findViewById(R.id.SleepEndH);

        next = findViewById(R.id.btnNext);

        // TimePicker לכל EditText
        setTimePicker(EffStart);
        setTimePicker(EffEnd);
        setTimePicker(BadStart);
        setTimePicker(BadEnd);
        setTimePicker(SleepStart);
        setTimePicker(SleepEnd);

        next.setOnClickListener(v -> saveData());
    }

    // --- הגדרת TimePicker ל־EditText ---
    private void setTimePicker(EditText editText) {
        editText.setOnClickListener(v -> {
            int hour = 12;
            int minute = 0;

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

            TimePickerDialog timePickerDialog = new TimePickerDialog(SignInUserSaturday.this,
                    (TimePicker view, int hourOfDay, int minute1) -> {
                        editText.setText(String.format("%02d:%02d", hourOfDay, minute1));
                    }, hour, minute, true);
            timePickerDialog.show();
        });
    }

    // --- שמירה ב־Firestore ---
    private void saveData() {
        Intent lastIntent = getIntent();
        String username = lastIntent.getStringExtra("username");
        String phone = lastIntent.getStringExtra("phoneNum");
        String pass = lastIntent.getStringExtra("password");

        // בדיקה אם לפחות אחד מהשדות מלא
        if (isDayEfficientTimeEmpty() && isDayBadTimeEmpty() && isDaySleepTimeEmpty()) {
            Toast.makeText(this, "Please fill at least one time field", Toast.LENGTH_LONG).show();
            return;
        }

        ArrayList<EfficientTime> effciency = (ArrayList<EfficientTime>) lastIntent.getSerializableExtra("effciency");
        if (!isDayEfficientTimeEmpty()) effciency.add(getDayEfficientTime());

        ArrayList<EfficientTime> uneffciency = (ArrayList<EfficientTime>) lastIntent.getSerializableExtra("uneffciency");
        if (!isDayBadTimeEmpty()) uneffciency.add(getDayBadTime());

        ArrayList<EfficientTime> sleep = (ArrayList<EfficientTime>) lastIntent.getSerializableExtra("sleep");
        if (!isDaySleepTimeEmpty()) sleep.add(getDaySleepTime());

        ArrayList<Task> tasks = new ArrayList<>();
        ArrayList<DayEfficiency> efficiencyHistory = new ArrayList<>();
        ArrayList<Todo> todos = new ArrayList<>();

        ArrayList<SchoolDay> schoolDays = (ArrayList<SchoolDay>) getIntent().getSerializableExtra("school");


        User user = new User(phone, username, pass, effciency, uneffciency, sleep, tasks, efficiencyHistory, todos, schoolDays);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .add(user)
                .addOnSuccessListener(res -> {
                    userId = res.getId();
                    Toast.makeText(this, "Welcome!", Toast.LENGTH_LONG).show();
                    // מעבר למסך הבית אחרי שמירה
                    Intent intent = new Intent(SignInUserSaturday.this, HomeScreen.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(exc -> {
                    Toast.makeText(this, "Something went wrong, please try again", Toast.LENGTH_LONG).show();
                });
    }

    // --- יצירת אובייקטי EfficientTime ---
    private EfficientTime getDayEfficientTime() {
        return new EfficientTime(DayOfWeek.SATURDAY, parseLocalTime(EffStart), parseLocalTime(EffEnd));
    }

    private EfficientTime getDayBadTime() {
        return new EfficientTime(DayOfWeek.SATURDAY, parseLocalTime(BadStart), parseLocalTime(BadEnd));
    }

    private EfficientTime getDaySleepTime() {
        return new EfficientTime(DayOfWeek.SATURDAY, parseLocalTime(SleepStart), parseLocalTime(SleepEnd));
    }

    // --- בדיקה אם שדות ריקים ---
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

    // --- המרת EditText ל־LocalTime ---
    private LocalTime parseLocalTime(EditText editText) {
        String text = editText.getText().toString();
        if (text.isEmpty()) return null;
        try {
            String[] parts = text.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            return LocalTime.of(hour, minute);
        } catch (Exception e) {
            editText.setError("Invalid time");
            return null;
        }
    }
}
