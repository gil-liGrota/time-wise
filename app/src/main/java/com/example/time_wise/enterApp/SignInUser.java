package com.example.time_wise.enterApp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.time_wise.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class SignInUser extends AppCompatActivity {

    private EditText userName, phoneNum, password;
    private Button next;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.sign_in_user);

        userName = findViewById(R.id.etUsername);
        phoneNum = findViewById(R.id.etPhone);
        password = findViewById(R.id.etPassword);
        next = findViewById(R.id.btnNext);

        db = FirebaseFirestore.getInstance();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isEmpty()) {
                    String username = userName.getText().toString().trim();
                    checkIfUsernameExists(username);
                }
            }
        });
    }

    private boolean isEmpty(){
        if(userName.getText().toString().isEmpty()){
            userName.setError("enter username");
        }
        if(phoneNum.getText().toString().isEmpty()){
            phoneNum.setError("enter phone number");
        }
        if(password.getText().toString().isEmpty()){
            password.setError("enter password");
        }

        return userName.getText().toString().isEmpty()
                || phoneNum.getText().toString().isEmpty()
                || password.getText().toString().isEmpty();
    }

    private void checkIfUsernameExists(String username) {

        db.collection("users")
                .whereEqualTo("userName", username)
                .get()
                .addOnSuccessListener(query -> {

                    if (!query.isEmpty()) {
                        userName.setError("This username is already taken");
                        return;
                    }
                    else {
                        goToNextScreen();
                    }
                })
                .addOnFailureListener(e -> {
                });
    }


    private void goToNextScreen() {
        Intent intent = new Intent(SignInUser.this, School.class);

        String user = userName.getText().toString();
        String phone = phoneNum.getText().toString();
        String pass = password.getText().toString();

        intent.putExtra("username", user);
        intent.putExtra("phoneNum", phone);
        intent.putExtra("password", pass);
        startActivity(intent);
    }
}
