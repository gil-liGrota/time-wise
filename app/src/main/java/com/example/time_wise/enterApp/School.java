package com.example.time_wise.enterApp;

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

import com.example.time_wise.R;
import com.example.time_wise.schoolSchedule.Lesson;
import com.example.time_wise.schoolSchedule.SchoolDay;
import com.example.time_wise.users.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class School extends AppCompatActivity {

    private EditText[] editHours = new EditText[11];
    private TextView txtDayTitle;
    private Button btnNext;

    private String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    private int currentDayIndex = 0;
    private ArrayList<SchoolDay> allWeekSchedule = new ArrayList<>();
    private SecurityManager securityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_school);

        securityManager = new SecurityManager(this);

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
            return;
        }

        allWeekSchedule.add(new SchoolDay(days[currentDayIndex], dailyLessons));

        if (currentDayIndex < days.length - 1) {
            updateUIForNextDay();
        } else {
            saveDataToFirebase();
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
    private void saveDataToFirebase() {
        Intent lastIntent = getIntent();
        String username = lastIntent.getStringExtra("username");
        String phone = lastIntent.getStringExtra("phoneNum");
        String rawPassword = lastIntent.getStringExtra("password");
        ArrayList<SchoolDay> schoolDays = (ArrayList<SchoolDay>) lastIntent.getSerializableExtra("school");

        String encryptedPassword = securityManager.hashPassword(rawPassword);

        User user = new User(phone, username, encryptedPassword, new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), schoolDays, new ArrayList<>(), 00, 00);

        FirebaseFirestore.getInstance().collection("users")
                .add(user)
                .addOnSuccessListener(doc -> {
                    securityManager.saveUserId(doc.getId());

                    Intent intent = new Intent(School.this, HomeScreen.class);
                    intent.putExtra("userId", doc.getId());
                    startActivity(intent);
                    finish();
                });
    }
}