package com.example.time_wise.enterApp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.time_wise.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class LogInActivity extends AppCompatActivity {

    private Button logIn, signIn;
    private TextView user, pass;
    private FirebaseFirestore db;
    private SecurityManager securityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);

        db = FirebaseFirestore.getInstance();

        securityManager = new SecurityManager(this);


        logIn = findViewById(R.id.btnLogin);
        signIn = findViewById(R.id.btnSignIn);
        user = findViewById(R.id.editTextEmail);
        pass = findViewById(R.id.editTextPassword);

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEmpty()){
                    if(user.getText().toString().isEmpty()){
                        user.setError("enter username");
                    }
                    if(pass.getText().toString().isEmpty()){
                        pass.setError("enter password");
                    }
                } else {
                    String userName = user.getText().toString();
                    String password = pass.getText().toString();
                    checkLogin(userName, password);
                }
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogInActivity.this, SignInUserActivity.class);
                startActivity(intent);
            }
        });
    }

    private  boolean isEmpty(){
        return user.getText().toString().isEmpty() || pass.getText().toString().isEmpty();
    }

    private void checkLogin(String username, String password) {
        String encryptedInput = securityManager.hashPassword(password);

        db.collection("users")
                .whereEqualTo("userName", username)
                .get()
                .addOnSuccessListener(query -> {

                    if (query.isEmpty()) {
                        user.setError("username not found");
                        return;
                    }

                    String realPasswordInDB = query.getDocuments().get(0).getString("password");
                    String docId = query.getDocuments().get(0).getId();

                    if (realPasswordInDB == null || !realPasswordInDB.equals(encryptedInput)) {
                        pass.setError("password incorrect");
                        return;
                    }

                    securityManager.saveUserId(docId);

                    Intent intent = new Intent(LogInActivity.this, HomeActivity.class);
                    intent.putExtra("userId", docId);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error connecting to database", Toast.LENGTH_SHORT).show();
                });
    }
}