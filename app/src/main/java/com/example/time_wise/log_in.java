package com.example.time_wise;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class log_in extends AppCompatActivity {

    private Button logIn, signIn;
    private TextView user, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.log_in_screen);

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
                    Intent intent = new Intent(log_in.this, LogInUser.class);
                    intent.putExtra("username", userName);
                    intent.putExtra("password", password);
                    startActivity(intent);
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
}