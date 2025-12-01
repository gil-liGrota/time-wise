package com.example.time_wise;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

public class log_in extends AppCompatActivity {

    private Button logIn, signIn;
    private TextView user, pass;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.log_in_screen);
        db = FirebaseFirestore.getInstance();
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
                Intent intent = new Intent(log_in.this, SignInUser.class);
                startActivity(intent);
            }
        });
    }

    private  boolean isEmpty(){
        return user.getText().toString().isEmpty() || pass.getText().toString().isEmpty();
    }

    private void checkLogin(String username, String password) {

        db.collection("users")
                .whereEqualTo("userName", username)
                .get()
                .addOnSuccessListener(query -> {

                    if (query.isEmpty()) {
                        // אין משתמש כזה
                        user.setError("username not found");
                        Toast.makeText(this, "Username incorrect", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // יש משתמש → בודקים את הסיסמה
                    String realPassword = query.getDocuments().get(0).getString("password");

                    if (realPassword == null || !realPassword.equals(password)) {
                        pass.setError("password incorrect");
                        Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // הצלחה — עוברים למסך הבא
                    Intent intent = new Intent(log_in.this, HomeScreen.class);
                    intent.putExtra("username", username);
                    intent.putExtra("password", password);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error connecting to database", Toast.LENGTH_SHORT).show();
                });
    }
}