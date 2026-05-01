package com.example.time_wise;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.OkHttpClient;

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

        // הוספת אפשרות חזרה אחורה ב-Action Bar אם קיים
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void summarizeWithGemini(String textToSummarize) {
        llSummaryArea.setVisibility(View.VISIBLE);
        tvSummaryResult.setText("Summarizing... ✨");

        OkHttpClient client = new OkHttpClient();

        // 1. טיפול בתווים שעלולים לשבור את ה-JSON
        String safeContent = textToSummarize.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");

        // 2. בניית ה-JSON
        String json = "{\"contents\":[{\"parts\":[{\"text\":\"Summarize this briefly and in the same language as the text: " + safeContent + "\"}]}]}";

        okhttp3.RequestBody body = okhttp3.RequestBody.create(
                json, okhttp3.MediaType.parse("application/json; charset=utf-8"));

        // 3. בניית ה-URL
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + GEMINI_API_KEY;
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, java.io.IOException e) {
                runOnUiThread(() -> tvSummaryResult.setText("Error: " + e.getMessage()));
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws java.io.IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    try {
                        org.json.JSONObject jsonObject = new org.json.JSONObject(responseBody);
                        String summary = jsonObject.getJSONArray("candidates")
                                .getJSONObject(0)
                                .getJSONObject("content")
                                .getJSONArray("parts")
                                .getJSONObject(0)
                                .getString("text");

                        runOnUiThread(() -> tvSummaryResult.setText(summary.trim()));
                    } catch (org.json.JSONException e) {
                        runOnUiThread(() -> tvSummaryResult.setText("Parsing error: " + e.getMessage()));
                    }
                } else {
                    // הצגת פירוט השגיאה מהשרת כדי שנדע למה יש 404 או 400
                    runOnUiThread(() -> tvSummaryResult.setText("Server error " + response.code() + ": " + responseBody));
                }
            }
        });
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