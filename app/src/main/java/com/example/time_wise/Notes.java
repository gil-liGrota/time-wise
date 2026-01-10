package com.example.time_wise;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class Notes extends AppCompatActivity
        implements NotesAdapter.OnNoteClickListener {

    private RecyclerView recyclerView;
    private NotesAdapter adapter;
    private ArrayList<Note> notes;

    private FirebaseFirestore db;
    public static String userID = "user123"; // מה-Auth שלך

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes);

        recyclerView = findViewById(R.id.notesRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        notes = new ArrayList<>();
        adapter = new NotesAdapter(notes, this);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        findViewById(R.id.btnAddNote).setOnClickListener(v ->
                startActivity(new Intent(this, AddNote.class))
        );

        loadNotes();
    }

    private void loadNotes() {
        db.collection("users")
                .document(userID)
                .collection("notes")
                .addSnapshotListener((value, error) -> {
                    if (value == null) return;

                    notes.clear();
                    for (DocumentSnapshot doc : value) {
                        Note note = doc.toObject(Note.class);
                        note.id = doc.getId();
                        notes.add(note);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    public void onNoteClick(Note note) {
        // בהמשך: עריכה
    }

    @Override
    public void onNoteLongClick(Note note) {
        db.collection("users")
                .document(userID)
                .collection("notes")
                .document(note.id)
                .delete();
    }
}
