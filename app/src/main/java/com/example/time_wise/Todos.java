package com.example.time_wise;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Map;

public class Todos extends AppCompatActivity {

    public String userID;
    private Intent intent;
    private RecyclerView recyclerView;
    private TodoAdapter adapter;
    private ArrayList<Todo> todos;
    private FirebaseFirestore db;
    private LinearLayout sideMenu;
    private TextView btnMenu;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todos);

        userID = getIntent().getStringExtra("userId");

        recyclerView = findViewById(R.id.todosRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sideMenu = findViewById(R.id.sideMenu);
        btnMenu = findViewById(R.id.btnMenu);

        todos = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        adapter = new TodoAdapter(todos, updatedTodos -> {
            todos = updatedTodos; // מעדכן רשימה
            saveToFirestore();   // שומר ל-Firestore
        });

        recyclerView.setAdapter(adapter);

        loadFromFirestore();
    }

    private void loadFromFirestore() {
        if (userID == null || userID.isEmpty()) return;

        db.collection("users")
                .document(userID)
                .addSnapshotListener((@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) -> {
                    if (e != null) {
                        Log.e("FIRESTORE", "Listen failed", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        todos.clear();

                        ArrayList<Map<String, Object>> list =
                                (ArrayList<Map<String, Object>>) snapshot.get("todos");

                        if (list != null) {
                            for (Map<String, Object> map : list) {
                                String name = (String) map.get("name");
                                boolean done = map.get("done") != null && (boolean) map.get("done");
                                todos.add(new Todo(name, done));
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void saveToFirestore() {
        if (userID == null || userID.isEmpty()) return;

        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for (Todo t : todos) {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("name", t.name);
            map.put("done", t.done);
            list.add(map);
        }

        Map<String, Object> data = new java.util.HashMap<>();
        data.put("todos", list);

        db.collection("users")
                .document(userID)
                .update(data)
                .addOnSuccessListener(aVoid -> Log.d("FIRESTORE", "Todos saved"))
                .addOnFailureListener(e -> Log.e("FIRESTORE", "Failed to save todos", e));
    }

    private void setMenuClickListener(int id, Constant.Menu menu) {
        TextView item = findViewById(id);

        item.setOnClickListener(v -> {
            switch (menu){
                case Home:
                    intent = new Intent(Todos.this, HomeScreen.class);
                    Toast.makeText(this, "home", Toast.LENGTH_LONG).show();
                    break;
                case ACTIVITY:
                    intent = new Intent(Todos.this, TasksScreem.class);
                    break;
                case SCHOOL_SCHEDULE:
                    intent = new Intent(Todos.this, SchoolSchedule.class);
                    Toast.makeText(this, "School Schedule", Toast.LENGTH_LONG).show();
                    break;

                case FOLLOW_EFFICIENCY:
                    intent = new Intent(Todos.this, HomeScreen.class);
                    Toast.makeText(this, "Follow Efficiency", Toast.LENGTH_LONG).show();
                    break;

                case TODO:
                    intent = new Intent(Todos.this, Todos.class);
                    Toast.makeText(this, "To-Do", Toast.LENGTH_LONG).show();
                    break;

                case CALENDER:
                    intent = new Intent(Todos.this, HomeScreen.class);
                    Toast.makeText(this, "Calendar", Toast.LENGTH_LONG).show();
                    break;

                case NOTES:
                    intent = new Intent(Todos.this, HomeScreen.class);
                    Toast.makeText(this, "Notes", Toast.LENGTH_LONG).show();
                    break;

                case LERNING_PLAN:
                    intent = new Intent(Todos.this, HomeScreen.class);
                    Toast.makeText(this, "Learning Plan", Toast.LENGTH_LONG).show();
                    break;

                case FOLLOW_GOAL:
                    intent = new Intent(Todos.this, HomeScreen.class);
                    Toast.makeText(this, "Follow Goal", Toast.LENGTH_LONG).show();
                    break;

                default:
                    intent = new Intent(Todos.this, HomeScreen.class);
                    Toast.makeText(this, "not working", Toast.LENGTH_LONG).show();
                    break;
            }
            intent.putExtra("userId", userID);
            startActivity(intent);
            sideMenu.setVisibility(View.GONE);
        });
    }
}
