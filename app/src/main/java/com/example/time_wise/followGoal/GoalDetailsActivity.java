package com.example.time_wise.followGoal;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.time_wise.Constant;
import com.example.time_wise.Date;
import com.example.time_wise.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GoalDetailsActivity extends AppCompatActivity {
    private TextView tvTitle, tvCountdown, tvEncouragement;
    private CheckBox cbDaily;
    private Goal currentGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_details);

        currentGoal = (Goal) getIntent().getSerializableExtra("goal");

        tvTitle = findViewById(R.id.tvGoalTitleDetail);
        tvCountdown = findViewById(R.id.tvCountdown);
        tvEncouragement = findViewById(R.id.tvEncouragement);
        cbDaily = findViewById(R.id.cbDailyDone);

        tvTitle.setText(currentGoal.getTitle());

        if (currentGoal.isDaily()) {
            setupDailyGoal();
        } else {
            setupTargetGoal();
        }
    }

    private void setupDailyGoal() {
        tvCountdown.setVisibility(View.GONE);
        cbDaily.setVisibility(View.VISIBLE);

        Calendar today = Calendar.getInstance();
        Date lastDate = currentGoal.getLastCheckedDate();

        if (lastDate != null && lastDate.getDay() == today.get(Calendar.DAY_OF_MONTH) &&
                lastDate.getMonth() == (today.get(Calendar.MONTH) + 1)) {
            cbDaily.setChecked(true);
            tvEncouragement.setText(EncouragementSystem.getCongratulation());
        } else {
            cbDaily.setChecked(false);
            tvEncouragement.setText(EncouragementSystem.getEncouragement());
        }

        cbDaily.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Calendar now = Calendar.getInstance();
                currentGoal.setLastCheckedDate(new Date(
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH) + 1,
                        now.get(Calendar.DAY_OF_MONTH)
                ));
                tvEncouragement.setText(EncouragementSystem.getCongratulation());

                updateGoalInFirebase();
            }
        });
    }
    private void updateGoalInFirebase() {
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();

        db.collection("users").document(Constant.USER_ID).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                ArrayList<java.util.Map<String, Object>> goalsData = (ArrayList<Map<String, Object>>) doc.get("goals");
                if (goalsData != null) {
                    for (java.util.Map<String, Object> map : goalsData) {
                        if (map.get("id").equals(currentGoal.getId())) {
                            java.util.HashMap<String, Object> dateMap = new java.util.HashMap<>();
                            dateMap.put("day", currentGoal.getLastCheckedDate().getDay());
                            dateMap.put("month", currentGoal.getLastCheckedDate().getMonth());
                            dateMap.put("year", currentGoal.getLastCheckedDate().getYear());

                            map.put("lastCheckedDate", dateMap);
                        }
                    }
                    db.collection("users").document(Constant.USER_ID).update("goals", goalsData);
                }
            }
        });
    }

    private void setupTargetGoal() {
        cbDaily.setVisibility(View.GONE);

        Calendar today = Calendar.getInstance();
        Calendar target = Calendar.getInstance();
        target.set(currentGoal.getTargetDate().getYear(), currentGoal.getTargetDate().getMonth()-1, currentGoal.getTargetDate().getDay());

        long diff = target.getTimeInMillis() - today.getTimeInMillis();
        long daysLeft = TimeUnit.MILLISECONDS.toDays(diff);

        if (daysLeft < 0) {
            tvCountdown.setText("Goal Expired");
        } else if (daysLeft == 0) {
            tvCountdown.setText("Today is the Last Day!");
            tvEncouragement.setText(EncouragementSystem.getCongratulation());
        } else {
            tvCountdown.setText(daysLeft + " Days Remaining");
            tvEncouragement.setText(EncouragementSystem.getEncouragement());
        }
    }

    public void finishActivity(View view) {
        finish();
    }
}