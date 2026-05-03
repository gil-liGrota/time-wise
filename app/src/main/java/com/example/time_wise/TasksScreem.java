package com.example.time_wise;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TasksScreem extends AppCompatActivity {

    private ListView lvActivity;
    private ArrayList<Task> tasks;
    private ArrayList<Map<String, Object>> rawTasksFromDB; // קריטי למניעת שכפול
    private String userID;
    private TaskAdapter taskAdapter;
    private LinearLayout sideMenu;
    private TextView btnMenu;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.tasks_screen);

        db = FirebaseFirestore.getInstance();
        lvActivity = findViewById(R.id.activityList);
        tasks = new ArrayList<>();
        rawTasksFromDB = new ArrayList<>();

        userID = getIntent().getStringExtra("userId");
        if (userID == null) userID = Constant.USER_ID;

        // כפתורי הוספה
        FloatingActionButton btnAddActivity = findViewById(R.id.btnAddActivity);
        btnAddActivity.setOnClickListener(v -> openAddTaskDialog());

        FloatingActionButton btnAddTopic = findViewById(R.id.btnAddTopic);
        btnAddTopic.setOnClickListener(v -> showAddTopicDirectDialog());

        // תפריט צד
        sideMenu = findViewById(R.id.sideMenu);
        btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> {
            sideMenu.setVisibility(sideMenu.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        });

        setupNavigationMenu();
        loadTasks();
    }

    // --- ניהול תפריט הניווט (כל הכפתורים שחיפשת) ---

    private void setupNavigationMenu() {
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
        if (item == null) return;
        item.setOnClickListener(v -> {
            Intent intent;
            switch (menu) {
                case Home: intent = new Intent(this, HomeScreen.class); break;
                case ACTIVITY: intent = new Intent(this, TasksScreem.class); break;
                case CALENDER: intent = new Intent(this, CalendarActivity.class); break;
                case SIGN_OUT: intent = new Intent(this, log_in.class); break;
                // הוסיפי כאן את שאר ה-Activities שלך לפי הצורך
                default: intent = new Intent(this, HomeScreen.class); break;
            }
            intent.putExtra("userId", userID);
            startActivity(intent);
            sideMenu.setVisibility(View.GONE);
        });
    }

    // --- לוגיקת מסד נתונים ---

    private void loadTasks() {
        db.collection("users").document(userID)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists() && doc.get("tasks") != null) {
                        rawTasksFromDB = (ArrayList<Map<String, Object>>) doc.get("tasks");
                        tasks.clear();
                        for (Map<String, Object> map : rawTasksFromDB) {
                            Task t = convertMapToTask(map);
                            if (t != null) tasks.add(t);
                        }
                        updateListView();
                    }
                });
    }

    private void updateListView() {
        if (taskAdapter == null) {
            taskAdapter = new TaskAdapter(this, tasks, userID);
            lvActivity.setAdapter(taskAdapter);
        } else {
            taskAdapter.notifyDataSetChanged();
        }
    }

    // --- המרות (Mappers) ---

    private Map<String, Object> taskToMap(Task t) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", t.getName());
        map.put("priority", (long) t.getPriority());
        map.put("isImportant", t.isImportant());
        map.put("strict", t.isStrict());
        map.put("type", t.getType() != null ? t.getType().name() : "NONE");

        if (t.getDate() != null) {
            Map<String, Object> d = new HashMap<>();
            d.put("day", (long) t.getDate().getDay());
            d.put("month", (long) t.getDate().getMonth());
            d.put("year", (long) t.getDate().getYear());
            map.put("date", d);
        }

        if (t.getStart() != null) {
            Map<String, Object> s = new HashMap<>();
            s.put("hour", (long) t.getStart().getHour());
            s.put("minute", (long) t.getStart().getMinute());
            map.put("start", s);
        }

        if (t.getEnd() != null) {
            Map<String, Object> e = new HashMap<>();
            e.put("hour", (long) t.getEnd().getHour());
            e.put("minute", (long) t.getEnd().getMinute());
            map.put("end", e);
        }

        if (t.getTopic() != null) {
            Map<String, Object> top = new HashMap<>();
            top.put("name", t.getTopic().getName());
            map.put("topic", top);
        }
        return map;
    }

    private Task convertMapToTask(Map<String, Object> map) {
        try {
            Task task = new Task();
            task.setName((String) map.get("name"));
            task.setPriority(((Long) map.get("priority")).intValue());
            task.setImportant((Boolean) map.get("isImportant"));
            task.setStrict((Boolean) map.get("strict"));
            task.setType(Constant.RepeatType.valueOf((String) map.get("type")));

            if (map.containsKey("date")) {
                Map<String, Object> d = (Map<String, Object>) map.get("date");
                task.setDate(new Date(((Long) d.get("year")).intValue(), ((Long) d.get("month")).intValue(), ((Long) d.get("day")).intValue()));
            }
            if (map.containsKey("start")) {
                Map<String, Object> s = (Map<String, Object>) map.get("start");
                task.setStart(LocalTime.of(((Long) s.get("hour")).intValue(), ((Long) s.get("minute")).intValue()));
            }
            if (map.containsKey("end")) {
                Map<String, Object> e = (Map<String, Object>) map.get("end");
                task.setEnd(LocalTime.of(((Long) e.get("hour")).intValue(), ((Long) e.get("minute")).intValue()));
            }
            if (map.containsKey("topic")) {
                Map<String, Object> top = (Map<String, Object>) map.get("topic");
                task.setTopic(new Topic((String) top.get("name")));
            }
            return task;
        } catch (Exception e) { return null; }
    }

    // --- דיאלוגים (עריכה והוספה) ---

    public void openEditTaskDialog(Task task, int position) {
        // שימוש במפה המקורית מה-DB למחיקה בטוחה
        final Map<String, Object> originalMap = rawTasksFromDB.get(position);

        View v = getLayoutInflater().inflate(R.layout.dialog_add_task, null);
        EditText etName = v.findViewById(R.id.etTaskName);
        EditText etStart = v.findViewById(R.id.etTaskStart);
        EditText etEnd = v.findViewById(R.id.etTaskEnd);
        EditText etDate = v.findViewById(R.id.etTaskDate);
        EditText etPriority = v.findViewById(R.id.etPriority);
        Switch swTime = v.findViewById(R.id.switchHasTime);
        Spinner spTopic = v.findViewById(R.id.spinnerTopic);
        Spinner spRepeat = v.findViewById(R.id.spinnerRepeat);
        Spinner spImp = v.findViewById(R.id.spinnerImportance);
        Spinner spStrict = v.findViewById(R.id.spinnerStrict);

        // טעינת ערכים קיימים
        etName.setText(task.getName());
        etPriority.setText(String.valueOf(task.getPriority()));
        if (task.getDate() != null) etDate.setText(String.format("%02d/%02d/%04d", task.getDate().getDay(), task.getDate().getMonth(), task.getDate().getYear()));

        swTime.setChecked(task.getStart() != null);
        etStart.setVisibility(swTime.isChecked() ? View.VISIBLE : View.GONE);
        etEnd.setVisibility(swTime.isChecked() ? View.VISIBLE : View.GONE);
        if (task.getStart() != null) {
            etStart.setText(task.getStart().toString());
            etEnd.setText(task.getEnd().toString());
        }

        swTime.setOnCheckedChangeListener((btn, isChecked) -> {
            etStart.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            etEnd.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        etDate.setOnClickListener(view -> showDatePicker(etDate));
        etStart.setOnClickListener(view -> showTimePicker(etStart));
        etEnd.setOnClickListener(view -> showTimePicker(etEnd));

        loadUserTopics(spTopic);
        setupEnumSpinners(spRepeat, spImp, spStrict, task);

        new AlertDialog.Builder(this)
                .setTitle("Edit Task")
                .setView(v)
                .setPositiveButton("Save", (dialog, which) -> {
                    // עדכון אובייקט מקומי
                    task.setName(etName.getText().toString().trim());
                    task.setPriority(Integer.parseInt(etPriority.getText().toString()));
                    if (swTime.isChecked()) {
                        task.setStart(LocalTime.parse(etStart.getText().toString()));
                        task.setEnd(LocalTime.parse(etEnd.getText().toString()));
                    } else {
                        task.setStart(null); task.setEnd(null);
                    }
                    String[] dp = etDate.getText().toString().split("/");
                    if (dp.length == 3) task.setDate(new Date(Integer.parseInt(dp[2]), Integer.parseInt(dp[1]), Integer.parseInt(dp[0])));

                    task.setType(Constant.RepeatType.values()[spRepeat.getSelectedItemPosition()]);
                    task.setImportant(spImp.getSelectedItemPosition() == 1);
                    task.setStrict(spStrict.getSelectedItemPosition() == 1);
                    task.setTopic(new Topic(spTopic.getSelectedItem().toString()));

                    // עדכון ב-Firestore: מחיקת הישן והוספת החדש
                    db.collection("users").document(userID)
                            .update("tasks", FieldValue.arrayRemove(originalMap))
                            .addOnSuccessListener(aVoid -> {
                                db.collection("users").document(userID)
                                        .update("tasks", FieldValue.arrayUnion(taskToMap(task)))
                                        .addOnSuccessListener(aVoid2 -> loadTasks());
                            });
                })
                .setNegativeButton("Cancel", null).show();
    }

    private void openAddTaskDialog() {
        View v = getLayoutInflater().inflate(R.layout.dialog_add_task, null);
        EditText etDate = v.findViewById(R.id.etTaskDate);
        EditText etStart = v.findViewById(R.id.etTaskStart);
        EditText etEnd = v.findViewById(R.id.etTaskEnd);
        Switch swTime = v.findViewById(R.id.switchHasTime);

        swTime.setOnCheckedChangeListener((btn, isChecked) -> {
            etStart.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            etEnd.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        etDate.setOnClickListener(view -> showDatePicker(etDate));
        etStart.setOnClickListener(view -> showTimePicker(etStart));
        etEnd.setOnClickListener(view -> showTimePicker(etEnd));

        loadUserTopics(v.findViewById(R.id.spinnerTopic));
        setupEnumSpinners(v.findViewById(R.id.spinnerRepeat), v.findViewById(R.id.spinnerImportance), v.findViewById(R.id.spinnerStrict), null);

        new AlertDialog.Builder(this)
                .setTitle("Add New Task")
                .setView(v)
                .setPositiveButton("Add", (dialog, which) -> {
                    Task newTask = new Task();
                    newTask.setName(((EditText)v.findViewById(R.id.etTaskName)).getText().toString().trim());
                    newTask.setPriority(Integer.parseInt(((EditText)v.findViewById(R.id.etPriority)).getText().toString()));

                    if (swTime.isChecked()) {
                        newTask.setStart(LocalTime.parse(etStart.getText().toString()));
                        newTask.setEnd(LocalTime.parse(etEnd.getText().toString()));
                    }
                    String[] dp = etDate.getText().toString().split("/");
                    if (dp.length == 3) newTask.setDate(new Date(Integer.parseInt(dp[2]), Integer.parseInt(dp[1]), Integer.parseInt(dp[0])));

                    newTask.setType(Constant.RepeatType.values()[((Spinner)v.findViewById(R.id.spinnerRepeat)).getSelectedItemPosition()]);
                    newTask.setImportant(((Spinner)v.findViewById(R.id.spinnerImportance)).getSelectedItemPosition() == 1);
                    newTask.setStrict(((Spinner)v.findViewById(R.id.spinnerStrict)).getSelectedItemPosition() == 1);
                    newTask.setTopic(new Topic(((Spinner)v.findViewById(R.id.spinnerTopic)).getSelectedItem().toString()));

                    db.collection("users").document(userID)
                            .update("tasks", FieldValue.arrayUnion(taskToMap(newTask)))
                            .addOnSuccessListener(aVoid -> loadTasks());
                }).show();
    }

    // --- עזרים לדיאלוגים ---

    private void setupEnumSpinners(Spinner r, Spinner i, Spinner s, Task t) {
        r.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"NONE", "DAILY", "WEEKLY", "MONTHLY"}));
        i.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Not Important", "Important"}));
        s.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Not Constant", "Constant"}));
        if (t != null) {
            r.setSelection(t.getType().ordinal());
            i.setSelection(t.isImportant() ? 1 : 0);
            s.setSelection(t.isStrict() ? 1 : 0);
        }
    }

    private void showDatePicker(EditText et) {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, y, m, d) -> et.setText(String.format("%02d/%02d/%04d", d, m + 1, y)),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker(EditText et) {
        new TimePickerDialog(this, (view, h, m) -> et.setText(String.format("%02d:%02d", h, m)), 12, 0, true).show();
    }

    private void loadUserTopics(Spinner sp) {
        db.collection("users").document(userID).collection("topics").get().addOnSuccessListener(snapshot -> {
            ArrayList<String> names = new ArrayList<>();
            for (DocumentSnapshot doc : snapshot.getDocuments()) names.add(doc.getString("name"));
            sp.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names));
        });
    }

    private void showAddTopicDirectDialog() {
        EditText et = new EditText(this);
        new AlertDialog.Builder(this).setTitle("New Topic").setView(et)
                .setPositiveButton("Add", (d, w) -> {
                    String n = et.getText().toString().trim();
                    if (!n.isEmpty()) db.collection("users").document(userID).collection("topics").add(Map.of("name", n));
                }).show();
    }
}