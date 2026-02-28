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

    // ניהול הימים והמצב
    private String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    private int currentDayIndex = 0;
    private ArrayList<SchoolDay> allWeekSchedule = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_school);

        // הגדרת Insets (ריפוד למערכת)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // אתחול רכיבי הממשק
        initViews();

        // הגדרת מאזין לכפתור
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

        // הגדרת כותרת התחלתית (Sunday)
        txtDayTitle.setText(days[currentDayIndex]);

        // קישור כל ה-EditTextים למערך בלולאה
        for (int i = 0; i < 11; i++) {
            String idName = "edtHour" + (i + 1);
            int resID = getResources().getIdentifier(idName, "id", getPackageName());
            editHours[i] = findViewById(resID);
        }
    }

    private void handleNextDayLogic() {
        ArrayList<Lesson> dailyLessons = new ArrayList<>();

        // איסוף השיעורים שהוזנו
        for (int i = 0; i < editHours.length; i++) {
            String lessonName = editHours[i].getText().toString().trim();
            if (!lessonName.isEmpty()) {
                dailyLessons.add(new Lesson(i + 1, lessonName));
            }
        }

        // בדיקה שהמשתמש הזין לפחות שיעור אחד
        if (dailyLessons.isEmpty()) {
            Toast.makeText(this, "Please enter at least one lesson", Toast.LENGTH_SHORT).show();
            return;
        }

        // שמירת היום הנוכחי לרשימה
        allWeekSchedule.add(new SchoolDay(days[currentDayIndex], dailyLessons));

        // מעבר ליום הבא או סיום
        if (currentDayIndex < days.length - 1) {
            updateUIForNextDay();
        } else {
            finishSetup();
        }
    }

    private void updateUIForNextDay() {
        currentDayIndex++;

        // עדכון הכותרת ליום החדש (Monday, Tuesday...)
        txtDayTitle.setText(days[currentDayIndex]);

        // ניקוי תיבות הטקסט
        for (EditText et : editHours) {
            et.setText("");
        }

        // אם הגענו ליום האחרון (שישי), נשנה את טקסט הכפתור
        if (currentDayIndex == days.length - 1) {
            btnNext.setText("Finish");
        }

        // גלילה אוטומטית לראש העמוד
        editHours[0].requestFocus();
    }

    private void finishSetup() {
        // 1. שליפת הנתונים שהגיעו ממסך ההרשמה (לפני הלו"ז)
        Intent lastIntent = getIntent();
        String user = lastIntent.getStringExtra("username");
        String phone = lastIntent.getStringExtra("phoneNum");
        String pass = lastIntent.getStringExtra("password");

        Intent intent = new Intent(School.this, SignInDetails.class);

        // 3. העברת כל המידע הלאה: גם הלו"ז וגם פרטי המשתמש
        intent.putExtra("school", allWeekSchedule);
        intent.putExtra("username", user);
        intent.putExtra("phoneNum", phone);
        intent.putExtra("password", pass);

        // מעבר למסך הבא עם כל הנתונים
        startActivity(intent);
        finish();
    }
}