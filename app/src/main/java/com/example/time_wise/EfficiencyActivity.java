package com.example.time_wise;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class EfficiencyActivity extends AppCompatActivity {
    private TextView btnMenu;
    private Intent intent;
    private LinearLayout sideMenu;
    private TextView tvScore;
    private SeekBar sbRate;
    private Button btnSend;
    private ListView lvHistory;
    private ArrayList<DayEfficiency> historyList = new ArrayList<>();
    private ArrayAdapter<DayEfficiency> adapter;
    private String userID;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_efficiency);

        db = FirebaseFirestore.getInstance();
        userID = getIntent().getStringExtra("userId");
        if (userID == null) userID = Constant.USER_ID;

        tvScore = findViewById(R.id.tvScore);
        sbRate = findViewById(R.id.sbRate);
        btnSend = findViewById(R.id.btnSend);
        lvHistory = findViewById(R.id.lvEfficiencyHistory);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyList);
        lvHistory.setAdapter(adapter);
        btnMenu = findViewById(R.id.btnMenu);
        sideMenu = findViewById(R.id.sideMenu);


        btnMenu.setOnClickListener(v -> {
            if (sideMenu.getVisibility() == View.GONE) {
                sideMenu.setVisibility(View.VISIBLE);
            } else {
                sideMenu.setVisibility(View.GONE);
            }
        });

        setMenuClickListener(R.id.nav_home, Constant.Menu.Home);
        setMenuClickListener(R.id.nav_activity, Constant.Menu.ACTIVITY);
        setMenuClickListener(R.id.nav_school_schedule, Constant.Menu.SCHOOL_SCHEDULE);
        setMenuClickListener(R.id.nav_follow_efficiency, Constant.Menu.FOLLOW_EFFICIENCY);
        setMenuClickListener(R.id.nav_todo, Constant.Menu.TODO);
        setMenuClickListener(R.id.nav_calendar, Constant.Menu.CALENDER);
        setMenuClickListener(R.id.nav_notes, Constant.Menu.NOTES);
        setMenuClickListener(R.id.nav_learning_plan, Constant.Menu.LERNING_PLAN);
        setMenuClickListener(R.id.nav_follow_goal, Constant.Menu.FOLLOW_GOAL);
        setMenuClickListener(R.id.nav_sign_out, Constant.Menu.SIGN_OUT);


        // עדכון הטקסט כשהמשתמש מזיז את הסליידר
        sbRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvScore.setText("Score: " + (progress + 1));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        loadHistoryFromDB();

        btnSend.setOnClickListener(v -> saveCurrentEfficiency());
    }

    private void setMenuClickListener(int id, Constant.Menu menu) {
        TextView item = findViewById(id);

        item.setOnClickListener(v -> {
            switch (menu){
                case Home:
                    intent = new Intent(EfficiencyActivity.this, HomeScreen.class);
                    Toast.makeText(this, "home", Toast.LENGTH_LONG).show();
                    break;

                case ACTIVITY:
                    intent = new Intent(EfficiencyActivity.this, TasksScreem.class);
                    break;

                case SCHOOL_SCHEDULE:
                    intent = new Intent(EfficiencyActivity.this, SchoolSchedule.class);
                    Toast.makeText(this, "School Schedule", Toast.LENGTH_LONG).show();
                    break;

                case FOLLOW_EFFICIENCY:
                    intent = new Intent(EfficiencyActivity.this, EfficiencyActivity.class);
                    Toast.makeText(this, "Follow Efficiency", Toast.LENGTH_LONG).show();
                    break;

                case TODO:
                    intent = new Intent(EfficiencyActivity.this, Todos.class);
                    Toast.makeText(this, "To-Do", Toast.LENGTH_LONG).show();
                    break;

                case CALENDER:
                    intent = new Intent(EfficiencyActivity.this, CalendarActivity.class);
                    Toast.makeText(this, "Calendar", Toast.LENGTH_LONG).show();
                    break;

                case NOTES:
                    intent = new Intent(EfficiencyActivity.this, NotesActivity.class);
                    Toast.makeText(this, "Notes", Toast.LENGTH_LONG).show();
                    break;

                case LERNING_PLAN:
                    intent = new Intent(EfficiencyActivity.this, HomeScreen.class);
                    Toast.makeText(this, "Learning Plan", Toast.LENGTH_LONG).show();
                    break;

                case FOLLOW_GOAL:
                    intent = new Intent(EfficiencyActivity.this, GoalsActivity.class);
                    Toast.makeText(this, "Follow Goal", Toast.LENGTH_LONG).show();
                    break;

                case SIGN_OUT:
                    intent = new Intent(EfficiencyActivity.this, log_in.class);
                    break;

                default:
                    intent = new Intent(EfficiencyActivity.this, HomeScreen.class);
                    Toast.makeText(this, "not working", Toast.LENGTH_LONG).show();
                    break;
            }
            intent.putExtra("userId", userID);
            startActivity(intent);
            sideMenu.setVisibility(View.GONE);
        });
    }

    private void loadHistoryFromDB() {
        db.collection("users").document(userID).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                ArrayList<Map<String, Object>> data = (ArrayList<Map<String, Object>>) doc.get("efficiencyHistory");
                if (data != null) {
                    historyList.clear();
                    for (Map<String, Object> map : data) {
                        // שיחזור אובייקט ה-Date מתוך ה-Map של Firebase
                        Map<String, Object> dateMap = (Map<String, Object>) map.get("date");
                        Date dateObj = new Date(
                                ((Long) dateMap.get("year")).intValue(),
                                ((Long) dateMap.get("month")).intValue(),
                                ((Long) dateMap.get("day")).intValue()
                        );
                        historyList.add(new DayEfficiency(((Long) map.get("midDay")).intValue(), dateObj));
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void saveCurrentEfficiency() {
        int score = sbRate.getProgress() + 1;

        // יצירת תאריך נוכחי בעזרת ה-Calendar של Java
        Calendar cal = Calendar.getInstance();
        Date today = new Date(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));

        DayEfficiency newEntry = new DayEfficiency(score, today);

        // הוספה לרשימה המקומית ועדכון ה-DB
        historyList.add(newEntry);

        db.collection("users").document(userID)
                .update("efficiencyHistory", historyList)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Efficiency saved!", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    btnSend.setEnabled(false); // מניעת שליחה כפולה באותו יום
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error saving", Toast.LENGTH_SHORT).show());
    }
}