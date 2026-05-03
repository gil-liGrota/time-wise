package com.example.time_wise.followGoal;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.time_wise.Constant;
import com.example.time_wise.Date;
import com.example.time_wise.R;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddGoalActivity extends AppCompatActivity {
    private EditText etTitle, etNote, etDate;
    private RadioGroup rgType;
    private Button btnSave;
    private Date selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);

        etTitle = findViewById(R.id.etGoalTitle);
        etNote = findViewById(R.id.etGoalNote);
        etDate = findViewById(R.id.etTargetDate);
        rgType = findViewById(R.id.rgGoalType);
        btnSave = findViewById(R.id.btnSaveGoal);

        rgType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbTarget) {
                etDate.setVisibility(View.VISIBLE);
            } else {
                etDate.setVisibility(View.GONE);
                selectedDate = null;
            }
        });

        etDate.setOnClickListener(v -> showDatePicker());

        btnSave.setOnClickListener(v -> saveGoal());
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedDate = new Date(year, month + 1, dayOfMonth);
            etDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveGoal() {
        String title = etTitle.getText().toString().trim();
        String note = etNote.getText().toString().trim();
        boolean isDaily = rgType.getCheckedRadioButtonId() == R.id.rbDaily;

        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isDaily && selectedDate == null) {
            Toast.makeText(this, "Please select a target date", Toast.LENGTH_SHORT).show();
            return;
        }

        String goalID = UUID.randomUUID().toString();
        Goal newGoal = new Goal(goalID, title, note, isDaily, selectedDate);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(Constant.USER_ID).get().addOnSuccessListener(doc -> {
            ArrayList<Goal> goalsList = new ArrayList<>();
            if (doc.exists() && doc.get("goals") != null) {
                db.collection("users").document(Constant.USER_ID)
                        .update("goals", com.google.firebase.firestore.FieldValue.arrayUnion(newGoal))
                        .addOnSuccessListener(aVoid -> {
                            finish();
                        });
            } else {
                goalsList.add(newGoal);
                db.collection("users").document(Constant.USER_ID).update("goals", goalsList)
                        .addOnSuccessListener(aVoid -> finish());
            }
        });
    }
}