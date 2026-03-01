package com.example.time_wise;

import android.app.TimePickerDialog;
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

import com.google.firebase.firestore.FirebaseFirestore;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;

public class SignInDetails extends AppCompatActivity {

    private EditText EffStart, EffEnd, BadStart, BadEnd, SleepStart, SleepEnd;
    private TextView txtDayTitle;
    private Button btnNext, btnBack;

    // ניהול הימים
    private DayOfWeek[] days = {
            DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
    };
    private int currentDayIndex = 0;

    // רשימות נתונים מצטברות
    private ArrayList<EfficientTime> efficiencyList = new ArrayList<>();
    private ArrayList<EfficientTime> unefficiencyList = new ArrayList<>();
    private ArrayList<EfficientTime> sleepList = new ArrayList<>();

    // אובייקט אבטחה (דרישות 9 ו-10)
    private SecurityManager securityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in_details);

        securityManager = new SecurityManager(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
    }

    private void initViews() {
        txtDayTitle = findViewById(R.id.txtDayTitle);
        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);

        EffStart = findViewById(R.id.EffStartH);
        EffEnd = findViewById(R.id.EffEndH);
        BadStart = findViewById(R.id.UneffStartH);
        BadEnd = findViewById(R.id.UneffEndH);
        SleepStart = findViewById(R.id.SleepStartH);
        SleepEnd = findViewById(R.id.SleepEndH);

        EditText[] fields = {EffStart, EffEnd, BadStart, BadEnd, SleepStart, SleepEnd};
        for (EditText et : fields) {
            et.setOnClickListener(v -> showTimePicker(et));
        }

        updateUI();

        btnNext.setOnClickListener(v -> handleNextStep());
        btnBack.setOnClickListener(v -> handleBackStep());
    }

    private void updateUI() {
        txtDayTitle.setText(days[currentDayIndex].name());
        btnBack.setVisibility(currentDayIndex == 0 ? View.GONE : View.VISIBLE);

        if (currentDayIndex == days.length - 1) {
            btnNext.setText("Finish & Register");
        } else {
            btnNext.setText("Next Day");
        }
    }

    private void handleNextStep() {
        // ולידציה - חובה למלא הכל (חלק מדרישה 10 - Validation)
        if (isAnyFieldEmpty()) {
            Toast.makeText(this, "Please fill all fields for " + days[currentDayIndex].name(), Toast.LENGTH_SHORT).show();
            return;
        }

        collectCurrentDayData();

        if (currentDayIndex < days.length - 1) {
            currentDayIndex++;
            clearAllFields();
            updateUI();
        } else {
            saveDataToFirebase();
        }
    }

    private void handleBackStep() {
        if (currentDayIndex > 0) {
            currentDayIndex--;
            // הסרת הנתונים של היום שבוטל
            if (!efficiencyList.isEmpty()) efficiencyList.remove(efficiencyList.size() - 1);
            if (!unefficiencyList.isEmpty()) unefficiencyList.remove(unefficiencyList.size() - 1);
            if (!sleepList.isEmpty()) sleepList.remove(sleepList.size() - 1);

            clearAllFields();
            updateUI();
        }
    }

    private void collectCurrentDayData() {
        DayOfWeek today = days[currentDayIndex];
        efficiencyList.add(new EfficientTime(today, parseTime(EffStart), parseTime(EffEnd)));
        unefficiencyList.add(new EfficientTime(today, parseTime(BadStart), parseTime(BadEnd)));
        sleepList.add(new EfficientTime(today, parseTime(SleepStart), parseTime(SleepEnd)));
    }

    private void saveDataToFirebase() {
        Intent lastIntent = getIntent();
        String username = lastIntent.getStringExtra("username");
        String phone = lastIntent.getStringExtra("phoneNum");
        String rawPassword = lastIntent.getStringExtra("password");
        ArrayList<SchoolDay> schoolDays = (ArrayList<SchoolDay>) lastIntent.getSerializableExtra("school");

        // --- דרישה 9: הצפנת הסיסמה לפני השמירה ---
        String encryptedPassword = securityManager.hashPassword(rawPassword);

        User user = new User(phone, username, encryptedPassword, efficiencyList,
                unefficiencyList, sleepList, new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), schoolDays);

        FirebaseFirestore.getInstance().collection("users")
                .add(user)
                .addOnSuccessListener(doc -> {
                    // --- דרישה 10: שמירת ה-ID בקובץ XML מקומי ---
                    securityManager.saveUserId(doc.getId());

                    Toast.makeText(this, "Welcome to TimeWise!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(this, HomeScreen.class);
                    intent.putExtra("userId", doc.getId());
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // פונקציות עזר
    private boolean isAnyFieldEmpty() {
        EditText[] fields = {EffStart, EffEnd, BadStart, BadEnd, SleepStart, SleepEnd};
        boolean empty = false;
        for (EditText et : fields) {
            if (et.getText().toString().trim().isEmpty()) {
                et.setError("Required");
                empty = true;
            }
        }
        return empty;
    }

    private void showTimePicker(EditText editText) {
        TimePickerDialog picker = new TimePickerDialog(this, (view, hour, minute) -> {
            editText.setText(String.format("%02d:%02d", hour, minute));
            editText.setError(null);
        }, 12, 0, true);
        picker.show();
    }

    private void clearAllFields() {
        EditText[] fields = {EffStart, EffEnd, BadStart, BadEnd, SleepStart, SleepEnd};
        for (EditText et : fields) {
            et.setText("");
            et.setError(null);
        }
    }

    private LocalTime parseTime(EditText et) {
        String[] p = et.getText().toString().split(":");
        return LocalTime.of(Integer.parseInt(p[0]), Integer.parseInt(p[1]));
    }
}