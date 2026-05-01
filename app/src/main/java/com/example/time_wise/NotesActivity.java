package com.example.time_wise;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NotesActivity extends AppCompatActivity {
    private Intent intent;
    private LinearLayout sideMenu;
    private TextView btnMenu;
    private ListView lvNotesAndFolders;
    private ArrayList<Object> displayList;
    private ArrayAdapter<Object> adapter;
    private String currentFolderId = null;
    private FirebaseFirestore db;
    private String userId; // יתקבל מה-Intent
    private TextView tvHeader;
    private String userID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        Intent lastIntent = getIntent();
        userID = lastIntent.getStringExtra("userId");

        db = FirebaseFirestore.getInstance();
        TextView btnBack = findViewById(R.id.btnBackFromFolder);
        if (currentFolderId != null) {
            btnBack.setVisibility(View.VISIBLE);
            btnBack.setOnClickListener(v -> finish()); // סוגר את המסך של התיקייה וחוזר לראשי
        } else {
            btnBack.setVisibility(View.GONE);
        }
        // מקבלים את ה-userId מהמסך הקודם
        userId = getIntent().getStringExtra("userId");
        if (userId == null) {
            // אם שכחנו לשלוח אותו, האפליקציה לא תדע לאן לשמור
            Toast.makeText(this, "Error: User ID missing", Toast.LENGTH_SHORT).show();
        }
        sideMenu = findViewById(R.id.sideMenu);
        btnMenu = findViewById(R.id.btnMenu);

        lvNotesAndFolders = findViewById(R.id.lvNotesAndFolders);
        tvHeader = findViewById(R.id.tvNotesHeader);
        displayList = new ArrayList<>();

        currentFolderId = getIntent().getStringExtra("folderId");
        String folderName = getIntent().getStringExtra("folderName");

        if (folderName != null) {
            tvHeader.setText(folderName);
        }
        adapter = new ArrayAdapter<>(this, R.layout.note_item, R.id.tvItemText, displayList);
        lvNotesAndFolders.setAdapter(adapter);

        findViewById(R.id.fabAddFolder).setOnClickListener(v -> showAddFolderDialog());
        findViewById(R.id.fabAddNote).setOnClickListener(v -> {
            Intent intent = new Intent(this, EditNoteActivity.class);
            intent.putExtra("userId", userId); // חשוב להעביר הלאה
            intent.putExtra("folderId", currentFolderId);
            startActivity(intent);
        });

        if (currentFolderId != null) {
            findViewById(R.id.fabAddFolder).setVisibility(View.GONE);
        }

        lvNotesAndFolders.setOnItemClickListener((parent, view, position, id) -> {
            Object selected = displayList.get(position);
            if (selected instanceof Folder) {
                Intent intent = new Intent(this, NotesActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("folderId", ((Folder) selected).getId());
                intent.putExtra("folderName", ((Folder) selected).getName());
                startActivity(intent);
            } else if (selected instanceof Note) {
                Intent intent = new Intent(this, EditNoteActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("note", (Note) selected);
                startActivity(intent);
            }
        });

        loadData();

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


    }

    private void setMenuClickListener(int id, Constant.Menu menu) {
        TextView item = findViewById(id);

        item.setOnClickListener(v -> {
            switch (menu){
                case Home:
                    intent = new Intent(NotesActivity.this, HomeScreen.class);
                    Toast.makeText(this, "home", Toast.LENGTH_LONG).show();
                    break;
                case ACTIVITY:
                    intent = new Intent(NotesActivity.this, TasksScreem.class);
                    break;
                case SCHOOL_SCHEDULE:
                    intent = new Intent(NotesActivity.this, SchoolSchedule.class);
                    Toast.makeText(this, "School Schedule", Toast.LENGTH_LONG).show();
                    break;

                case FOLLOW_EFFICIENCY:
                    intent = new Intent(NotesActivity.this, EfficiencyActivity.class);
                    Toast.makeText(this, "Follow Efficiency", Toast.LENGTH_LONG).show();
                    break;

                case TODO:
                    intent = new Intent(NotesActivity.this, Todos.class);
                    Toast.makeText(this, "To-Do", Toast.LENGTH_LONG).show();
                    break;

                case CALENDER:
                    intent = new Intent(NotesActivity.this, CalendarActivity.class);
                    Toast.makeText(this, "Calendar", Toast.LENGTH_LONG).show();
                    break;

                case NOTES:
                    intent = new Intent(NotesActivity.this, NotesActivity.class);
                    Toast.makeText(this, "Notes", Toast.LENGTH_LONG).show();
                    break;

                case LERNING_PLAN:
                    intent = new Intent(NotesActivity.this, HomeScreen.class);
                    Toast.makeText(this, "Learning Plan", Toast.LENGTH_LONG).show();
                    break;

                case FOLLOW_GOAL:
                    intent = new Intent(NotesActivity.this, GoalsActivity.class);
                    Toast.makeText(this, "Follow Goal", Toast.LENGTH_LONG).show();
                    break;

                case SIGN_OUT:
                    intent = new Intent(NotesActivity.this, log_in.class);
                    break;
                default:
                    intent = new Intent(NotesActivity.this, HomeScreen.class);
                    Toast.makeText(this, "not working", Toast.LENGTH_LONG).show();
                    break;
            }
            intent.putExtra("userId", userID);
            startActivity(intent);
            sideMenu.setVisibility(View.GONE);
        });
    }

    private void loadData() {
        if (userId == null) return;

        db.collection("users").document(userId).addSnapshotListener((snapshot, e) -> {
            if (snapshot != null && snapshot.exists()) {
                displayList.clear();

                if (currentFolderId == null) {
                    List<Map<String, Object>> folders = (List<Map<String, Object>>) snapshot.get("folders");
                    if (folders != null) {
                        for (Map<String, Object> f : folders) {
                            displayList.add(new Folder((String)f.get("id"), (String)f.get("name")));
                        }
                    }
                }

                List<Map<String, Object>> notes = (List<Map<String, Object>>) snapshot.get("notes");
                if (notes != null) {
                    for (Map<String, Object> n : notes) {
                        String fId = (String) n.get("folderId");
                        if ((currentFolderId == null && fId == null) || (currentFolderId != null && currentFolderId.equals(fId))) {
                            displayList.add(new Note((String)n.get("id"), (String)n.get("title"), (String)n.get("content"), fId));
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void showAddFolderDialog() {
        EditText et = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("New Folder")
                .setView(et)
                .setPositiveButton("Create", (d, w) -> {
                    String name = et.getText().toString();
                    if (!name.isEmpty()) saveFolder(name);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveFolder(String name) {
        if (userId == null) return;
        Folder newFolder = new Folder(UUID.randomUUID().toString(), name);
        db.collection("users").document(userId)
                .update("folders", FieldValue.arrayUnion(newFolder))
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Folder Created!", Toast.LENGTH_SHORT).show());
    }
}