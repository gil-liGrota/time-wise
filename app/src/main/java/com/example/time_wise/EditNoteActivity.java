package com.example.time_wise;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EditNoteActivity extends AppCompatActivity {

    private EditText etNoteTitle, etNoteContent;
    private Button btnSave, btnSummarize;
    private FirebaseFirestore db;
    private String userId;
    private String folderId;
    private Note existingNote = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        db = FirebaseFirestore.getInstance();
        userId = getIntent().getStringExtra("userId");
        folderId = getIntent().getStringExtra("folderId");
        existingNote = (Note) getIntent().getSerializableExtra("note");

        etNoteTitle = findViewById(R.id.etNoteTitle);
        etNoteContent = findViewById(R.id.etNoteContent);
        btnSave = findViewById(R.id.btnSaveNote);
        btnSummarize = findViewById(R.id.btnSummarize);

        if (existingNote != null) {
            etNoteTitle.setText(existingNote.getTitle());
            etNoteContent.setText(existingNote.getContent());
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> saveNote());

        // הוספת אפשרות חזרה אחורה ב-Action Bar אם קיים
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // יציאה מהפתק בלי לשמור
        return true;
    }

    private void saveNote() {
        if (userId == null) return;

        String title = etNoteTitle.getText().toString().trim();
        String content = etNoteContent.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. נכין את נתוני הפתק החדש
        String noteId = (existingNote != null) ? existingNote.getId() : UUID.randomUUID().toString();
        Map<String, Object> newNoteData = new HashMap<>();
        newNoteData.put("id", noteId);
        newNoteData.put("title", title);
        newNoteData.put("content", content);
        newNoteData.put("folderId", folderId);

        // 2. במקום למחוק ולהוסיף, אנחנו נעדכן את המערך בצורה חכמה
        db.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<Map<String, Object>> notes = (List<Map<String, Object>>) documentSnapshot.get("notes");
                if (notes == null) notes = new ArrayList<>();

                // אם זה פתק קיים - נחפש אותו ונעדכן. אם לא - נוסיף חדש.
                boolean updated = false;
                for (int i = 0; i < notes.size(); i++) {
                    if (notes.get(i).get("id").equals(noteId)) {
                        notes.set(i, newNoteData); // מעדכן את הקיים
                        updated = true;
                        break;
                    }
                }

                if (!updated) {
                    notes.add(newNoteData); // מוסיף חדש
                }

                // 3. שומרים את כל הרשימה המעודכנת חזרה ל-Firestore
                db.collection("users").document(userId)
                        .update("notes", notes)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Saved successfully!", Toast.LENGTH_SHORT).show();
                            finish(); // סוגר את המסך וחוזר אחורה
                        });
            }
        });
    }
    private void uploadNote(Map<String, Object> noteData) {
        db.collection("users").document(userId)
                .update("notes", FieldValue.arrayUnion(noteData))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
}