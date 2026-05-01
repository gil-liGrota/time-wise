package com.example.time_wise;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.GenerativeModel;
import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerateContentResponse;
import com.google.firebase.ai.type.GenerativeBackend;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class EditNoteActivity extends AppCompatActivity {
    private TextView tvSummaryResult;
    private LinearLayout llSummaryArea;
    private final String GEMINI_API_KEY = "AIzaSyCGWLFye5bE7xmQV77Yr0XjgCX8czgi9JY";
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
        tvSummaryResult = findViewById(R.id.tvSummaryResult);
        llSummaryArea = findViewById(R.id.llSummaryArea);

        btnSummarize.setOnClickListener(v -> {
            String content = etNoteContent.getText().toString();
            if (!content.isEmpty()) {
                summarizeWithGemini(content);
            } else {
                Toast.makeText(this, "Write something first!", Toast.LENGTH_SHORT).show();
            }
        });
        if (existingNote != null) {
            etNoteTitle.setText(existingNote.getTitle());
            etNoteContent.setText(existingNote.getContent());
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> saveNote());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void summarizeWithGemini(String textToSummarize) {
        llSummaryArea.setVisibility(View.VISIBLE);
        tvSummaryResult.setText("Summarizing... ✨");

        GenerativeModel ai = FirebaseAI.getInstance(GenerativeBackend.googleAI())
                .generativeModel("gemini-1.5-flash");
        GenerativeModelFutures model = GenerativeModelFutures.from(ai);

        Executor executor = Executors.newSingleThreadExecutor();

        Content prompt = new Content.Builder()
                .addText("Summarize this briefly and in the same language as the text: " + textToSummarize)
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(prompt);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();

                runOnUiThread(() -> {
                    if (resultText != null && !resultText.isEmpty()) {
                        tvSummaryResult.setText(resultText.trim());
                    } else {
                        tvSummaryResult.setText("לא התקבל סיכום.");
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(() -> {
                    tvSummaryResult.setText(t.getMessage());
                    Toast.makeText(EditNoteActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                });
                Log.d("TimeWise-AI", Log.getStackTraceString(t));
            }
        }, executor);
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