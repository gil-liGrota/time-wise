package com.example.time_wise;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class AddNote extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;

    private EditText etText;
    private ImageView img;
    private Uri imageUri;

    private FirebaseFirestore db;
    private String userID = Notes.userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_note);

        etText = findViewById(R.id.etNote);
        img = findViewById(R.id.imgNote);

        db = FirebaseFirestore.getInstance();

        findViewById(R.id.btnAddImage).setOnClickListener(v -> openGallery());
        findViewById(R.id.btnSave).setOnClickListener(v -> saveNote());
    }

    private void openGallery() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            img.setImageURI(imageUri);
        }
    }

    private void saveNote() {
        String text = etText.getText().toString();

        String noteId = db.collection("users")
                .document(userID)
                .collection("notes")
                .document()
                .getId();

        if (imageUri != null) {
            StorageReference ref = FirebaseStorage.getInstance()
                    .getReference("notes_images/" + userID + "/" + noteId + ".jpg");

            ref.putFile(imageUri)
                    .continueWithTask(task -> ref.getDownloadUrl())
                    .addOnSuccessListener(uri ->
                            saveToFirestore(noteId, text, uri.toString())
                    );
        } else {
            saveToFirestore(noteId, text, null);
        }
    }

    private void saveToFirestore(String id, String text, String imageUrl) {
        Map<String, Object> note = new HashMap<>();
        note.put("text", text);
        note.put("imageUrl", imageUrl);

        db.collection("users")
                .document(userID)
                .collection("notes")
                .document(id)
                .set(note)
                .addOnSuccessListener(aVoid -> finish());
    }
}
