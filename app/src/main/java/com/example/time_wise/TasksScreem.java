package com.example.time_wise;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class TasksScreem extends AppCompatActivity {

    private LinearLayout sideMenu;
    private TextView btnMenu;
    Intent intent;
    private ListView lvActivity;
    private ArrayList<Task> tasks;
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.tasks_screem);
        sideMenu = findViewById(R.id.sideMenu);
        btnMenu = findViewById(R.id.btnMenu);

        lvActivity = findViewById(R.id.activityList);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        tasks = new ArrayList<>();

        db.collection("users")
                .get()
                .addOnSuccessListener(snapshot -> {
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        // קח את רשימת המשימות (לדוגמה, "tasks") מה־User
                        ArrayList<Map<String, Object>> taskListFromDB =
                                (ArrayList<Map<String, Object>>) doc.get("tasks");

                        if (taskListFromDB != null) {
                            for (Map<String, Object> t : taskListFromDB) {
                                try {
                                    String name = t.get("name") != null ? t.get("name").toString() : "No Name";
                                    Task task = new Task();
                                    task.setName(name);
                                    // ניתן להוסיף קריאת שדות נוספים אם צריך: start, end וכו.
                                    tasks.add(task);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    // צור רשימת שמות המשימות להצגה
                    ArrayList<String> taskNames = new ArrayList<>();
                    for (Task t : tasks) {
                        taskNames.add(t.getName());
                    }

                    adapter = new ArrayAdapter<>(TasksScreem.this, android.R.layout.simple_list_item_1, taskNames);
                    lvActivity.setAdapter(adapter);
                });

        // לחיצה על כפתור המבורגר לפתיחה/סגירה
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

    }

    private void setMenuClickListener(int id, Constant.Menu menu) {
        TextView item = findViewById(id);

        item.setOnClickListener(v -> {
            switch (menu){
                case Home:
                    intent = new Intent(TasksScreem.this, TasksScreem.class);
                    startActivity(intent);

                    break;
                case ACTIVITY:
                    Toast.makeText(this, "activity", Toast.LENGTH_LONG).show();
                    break;
                case SCHOOL_SCHEDULE:
                    Toast.makeText(this, "School Schedule", Toast.LENGTH_LONG).show();
                    break;

                case FOLLOW_EFFICIENCY:
                    Toast.makeText(this, "Follow Efficiency", Toast.LENGTH_LONG).show();
                    break;

                case TODO:
                    Toast.makeText(this, "To-Do", Toast.LENGTH_LONG).show();
                    break;

                case CALENDER:
                    Toast.makeText(this, "Calendar", Toast.LENGTH_LONG).show();
                    break;

                case NOTES:
                    Toast.makeText(this, "Notes", Toast.LENGTH_LONG).show();
                    break;

                case LERNING_PLAN:
                    Toast.makeText(this, "Learning Plan", Toast.LENGTH_LONG).show();
                    break;

                case FOLLOW_GOAL:
                    Toast.makeText(this, "Follow Goal", Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(this, "not working", Toast.LENGTH_LONG).show();
                    break;
            }
//            startActivity(intent);
            sideMenu.setVisibility(View.GONE);
        });
    }
}