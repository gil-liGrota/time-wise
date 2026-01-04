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

public class SchoolMon extends AppCompatActivity {
    private EditText[] editHours = new EditText[11];
    private TextView txtDayTitle;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.school_sun);

        txtDayTitle = findViewById(R.id.txtDayTitle);
        btnNext = findViewById(R.id.btnNext);

        // מאחסנים את כל ה-EditText במערך כדי להקל על הלולאה
        editHours[0] = findViewById(R.id.edtHour1);
        editHours[1] = findViewById(R.id.edtHour2);
        editHours[2] = findViewById(R.id.edtHour3);
        editHours[3] = findViewById(R.id.edtHour4);
        editHours[4] = findViewById(R.id.edtHour5);
        editHours[5] = findViewById(R.id.edtHour6);
        editHours[6] = findViewById(R.id.edtHour7);
        editHours[7] = findViewById(R.id.edtHour8);
        editHours[8] = findViewById(R.id.edtHour9);
        editHours[9] = findViewById(R.id.edtHour10);
        editHours[10] = findViewById(R.id.edtHour11);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Lesson> lessonsList = new ArrayList<>();

                for (int i = 0; i < editHours.length; i++) {
                    String lessonName = editHours[i].getText().toString().trim();

                    if (!lessonName.isEmpty()) {
                        Lesson lesson = new Lesson(i + 1, lessonName);
                        lessonsList.add(lesson);
                    }
                }

                if (lessonsList.isEmpty()) {
                    Toast.makeText(SchoolMon.this, "No lessons entered!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String dayName = txtDayTitle.getText().toString();
                SchoolDay schoolDay = new SchoolDay("Monday", lessonsList);

                ArrayList<SchoolDay> schoolDays = (ArrayList<SchoolDay>) getIntent().getSerializableExtra("school");

                schoolDays.add(schoolDay);


                Intent intent = new Intent(SchoolMon.this, SchoolTue.class);
                intent.putExtra("school", schoolDays);
                startActivity(intent);
            }
        });
    }
}