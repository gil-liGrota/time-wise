package com.example.time_wise;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.GenerativeModel;
import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerateContentResponse;
import com.google.firebase.ai.type.GenerativeBackend;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LearningPlanActivity extends AppCompatActivity {
    private Intent intent;

    private EditText etTargetDate, etTotalHours, etMaxDailyHours, etTopicName, etExcludeDate;
    private Spinner spDifficulty;
    private Button btnAddTopic, btnAddExcludeDate, btnGeneratePlan;
    private TextView btnMenu;
    private LinearLayout sideMenu;
    private Switch swSchoolGaps;


    private List<StudyTopic> topicList = new ArrayList<>();
    private List<String> excludedDates = new ArrayList<>();
    private TopicAdapter adapter;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learning_plan_setup);

        Intent lastIntent = getIntent();
        SecurityManager securityManager = new SecurityManager(this);
        userID = getIntent().getStringExtra("userId");


        initViews();
        setupRecyclerView();
        setupSpinner();

        btnMenu.setOnClickListener(v -> {
            sideMenu.setVisibility(sideMenu.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        });

        btnAddTopic.setOnClickListener(v -> {
            String name = etTopicName.getText().toString().trim();
            int difficulty = spDifficulty.getSelectedItemPosition() + 1;

            if (!name.isEmpty()) {
                topicList.add(new StudyTopic(name, difficulty));
                adapter.notifyDataSetChanged();
                etTopicName.setText("");
            } else {
                Toast.makeText(this, "Please enter a topic name", Toast.LENGTH_SHORT).show();
            }
        });

        btnAddExcludeDate.setOnClickListener(v -> {
            String date = etExcludeDate.getText().toString().trim();
            if (!date.isEmpty()) {
                excludedDates.add(date);
                Toast.makeText(this, "Date " + date + " blocked", Toast.LENGTH_SHORT).show();
                etExcludeDate.setText("");
            }
        });

        btnGeneratePlan.setOnClickListener(v -> {
            if (topicList.isEmpty()) {
                Toast.makeText(this, "Please add at least one topic", Toast.LENGTH_SHORT).show();
                return;
            }
            if (etTotalHours.getText().toString().isEmpty() || etMaxDailyHours.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please fill in study hours", Toast.LENGTH_SHORT).show();
                return;
            }
            generateStudyPlan();
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
    }

    private void setMenuClickListener(int id, Constant.Menu menu) {
        TextView item = findViewById(id);

        item.setOnClickListener(v -> {
            switch (menu){
                case Home:
                    intent = new Intent(LearningPlanActivity.this, HomeScreen.class);
                    break;

                case ACTIVITY:
                    intent = new Intent(LearningPlanActivity.this, TasksScreem.class);
                    break;

                case SCHOOL_SCHEDULE:
                    intent = new Intent(LearningPlanActivity.this, SchoolSchedule.class);
                    break;

                case FOLLOW_EFFICIENCY:
                    intent = new Intent(LearningPlanActivity.this, EfficiencyActivity.class);
                    break;

                case TODO:
                    intent = new Intent(LearningPlanActivity.this, Todos.class);
                    break;

                case CALENDER:
                    intent = new Intent(LearningPlanActivity.this, CalendarActivity.class);
                    break;

                case NOTES:
                    intent = new Intent(LearningPlanActivity.this, NotesActivity.class);
                    break;

                case LERNING_PLAN:
                    intent = new Intent(LearningPlanActivity.this, LearningPlanActivity.class);
                    Toast.makeText(this, "Learning Plan", Toast.LENGTH_LONG).show();
                    break;

                case FOLLOW_GOAL:
                    intent = new Intent(LearningPlanActivity.this, GoalsActivity.class);
                    break;

                case SIGN_OUT:
                    intent = new Intent(LearningPlanActivity.this, log_in.class);
                    break;

                default:
                    intent = new Intent(LearningPlanActivity.this, HomeScreen.class);
                    Toast.makeText(this, "not working", Toast.LENGTH_LONG).show();
                    break;
            }
            intent.putExtra("userId", userID);
            startActivity(intent);
            sideMenu.setVisibility(View.GONE);
        });
    }

    private void initViews() {
        etTargetDate = findViewById(R.id.etTargetDate);
        etTotalHours = findViewById(R.id.etTotalHours);
        etMaxDailyHours = findViewById(R.id.etMaxDailyHours);
        etTopicName = findViewById(R.id.etTopicName);
        etExcludeDate = findViewById(R.id.etExcludeDate);
        spDifficulty = findViewById(R.id.spDifficulty);
        btnAddTopic = findViewById(R.id.btnAddTopic);
        btnAddExcludeDate = findViewById(R.id.btnAddExcludeDate);
        btnGeneratePlan = findViewById(R.id.btnGeneratePlan);
        btnMenu = findViewById(R.id.btnMenu);
        sideMenu = findViewById(R.id.sideMenu);
        swSchoolGaps = findViewById(R.id.swSchoolGaps);
    }

    private void setupRecyclerView() {
        RecyclerView rvTopics = findViewById(R.id.rvTopics);
        adapter = new TopicAdapter(topicList);
        rvTopics.setLayoutManager(new LinearLayoutManager(this));
        rvTopics.setAdapter(adapter);
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.difficulty_levels, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDifficulty.setAdapter(spinnerAdapter);
    }

    private void calculateTopicTimes(int totalHours) {
        int totalDifficulty = 0;
        for (StudyTopic topic : topicList) {
            totalDifficulty += topic.getDifficulty();
        }

        int totalMinutes = totalHours * 60;
        for (StudyTopic topic : topicList) {
            int allocated = (int) (((double) topic.getDifficulty() / totalDifficulty) * totalMinutes);
            topic.setAllocatedMinutes(allocated);
        }
    }

    private void generateStudyPlan() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        int totalHours = Integer.parseInt(etTotalHours.getText().toString());
        calculateTopicTimes(totalHours);

        Toast.makeText(this, "Generating plan with AI...", Toast.LENGTH_SHORT).show();

        db.collection("users").document(userID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Map<String, Object> weekSchedule = null;
                    if (documentSnapshot.exists()) {
                        weekSchedule = (Map<String, Object>) documentSnapshot.get("weekSchedule");
                    }
                    scheduleTopicsWithAI(weekSchedule);
                })
                .addOnFailureListener(e -> {
                    Log.e("TimeWise", "Firestore failed, scheduling without school data", e);
                    scheduleTopicsWithAI(null);
                });
    }

    private void scheduleTopicsWithAI(Map<String, Object> weekSchedule) {
        Calendar calendar = Calendar.getInstance();
        int dailyMaxMinutes = Integer.parseInt(etMaxDailyHours.getText().toString()) * 60;
        int daysProcessed = 0;

        for (StudyTopic topic : topicList) {
            int minutesToSchedule = topic.getAllocatedMinutes();

            while (minutesToSchedule > 0 && daysProcessed < 30) {
                String dateString = String.format("%02d/%02d/%04d",
                        calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));

                if (!excludedDates.contains(dateString)) {
                    if (swSchoolGaps.isChecked() && weekSchedule != null) {
                        String dayName = getDayName(calendar.get(Calendar.DAY_OF_WEEK));
                        Map<String, String> dayLessons = (Map<String, String>) weekSchedule.get(dayName);
                        if (dayLessons != null) {
                            for (int i = 1; i <= 11; i++) {
                                String lesson = dayLessons.get("hour" + i);
                                if (lesson == null || lesson.isEmpty()) {
                                    getAITaskBreakdown(topic.getTopicName(), 45, dateString, "gap in the schedule");
                                    minutesToSchedule -= 45;
                                }
                            }
                        }
                    }

                    if (minutesToSchedule > 0) {
                        int afternoonSession = Math.min(minutesToSchedule, dailyMaxMinutes);
                        getAITaskBreakdown(topic.getTopicName(), afternoonSession, dateString, "afternoon session");
                        minutesToSchedule -= afternoonSession;
                    }
                }
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                daysProcessed++;
            }
        }

        Toast.makeText(this, "your plane is ready! check your tasks", Toast.LENGTH_LONG).show();
    }

    private void getAITaskBreakdown(String topicName, int duration, String date, String note) {
        GenerativeModel ai = FirebaseAI.getInstance(GenerativeBackend.googleAI())
                .generativeModel("gemini-2.5-flash-lite");
        GenerativeModelFutures model = GenerativeModelFutures.from(ai);

        String promptText = "create a study plan for: " + topicName + " durating " + duration + " minutes. " +
                "break it down into 3 steps, answer in the language of the topic";

        Content prompt = new Content.Builder().addText(promptText).build();
        Executor executor = Executors.newSingleThreadExecutor();

        Futures.addCallback(model.generateContent(prompt), new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String aiText = result.getText();
                runOnUiThread(() -> saveTaskToFirestore(topicName, date, aiText + "\n(connection: " + note + ")"));
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("TimeWise-AI", "Gemini failed, saving generic task", t);
                runOnUiThread(() -> saveTaskToFirestore(topicName, date, "studing " + topicName + " (" + note + ")"));
            }
        }, executor);
    }

    private void saveTaskToFirestore(String title, String dateStr, String aiDescription) {
        String[] dateParts = dateStr.split("/");
        int day = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]);
        int year = Integer.parseInt(dateParts[2]);
        com.example.time_wise.Date taskDate = new com.example.time_wise.Date(day, month, year);

        Map<String, Object> taskMap = new HashMap<>();

        taskMap.put("name", title);
        taskMap.put("description", aiDescription);
        taskMap.put("date", taskDate);
        taskMap.put("isImportant", false);
        taskMap.put("priority", 3);
        taskMap.put("strict", false);

        taskMap.put("start", "16:00");
        taskMap.put("end", "17:00");

        //FIXME the path is worng, im to tired to fix this shit
        FirebaseFirestore.getInstance().collection("users").document(userID)
                .collection("tasks").add(taskMap)
                .addOnSuccessListener(ref -> Log.d("TimeWise", "Task saved with correct fields: " + title))
                .addOnFailureListener(e -> Log.e("TimeWise", "Error saving task", e));
    }

    private String getDayName(int day) {
        switch (day) {
            case Calendar.SUNDAY: return "Sunday";
            case Calendar.MONDAY: return "Monday";
            case Calendar.TUESDAY: return "Tuesday";
            case Calendar.WEDNESDAY: return "Wednesday";
            case Calendar.THURSDAY: return "Thursday";
            case Calendar.FRIDAY: return "Friday";
            case Calendar.SATURDAY: return "Saturday";
            default: return "Sunday";
        }
    }
}