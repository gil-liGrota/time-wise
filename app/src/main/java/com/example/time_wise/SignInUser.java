package com.example.time_wise;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class SignInUser extends AppCompatActivity {

    private EditText userName, phoneNum, password;
    private Button next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.sign_in_user);

        userName = findViewById(R.id.etUsername);
        phoneNum = findViewById(R.id.etPhone);
        password = findViewById(R.id.etPassword);
        next = findViewById(R.id.btnNext);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isEmpty()){
                    String user = userName.getText().toString();
                    System.out.println(user);
                    String phone = phoneNum.getText().toString();
                    String pass = password.getText().toString();
                    Intent intent = new Intent(SignInUser.this, SignInUserSunday.class);
                    intent.putExtra("username",user);
                    intent.putExtra("phoneNum", phone);
                    intent.putExtra("password", pass);
                    startActivity(intent);
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

        return userName.getText().toString().isEmpty() || phoneNum.getText().toString().isEmpty() || password.getText().toString().isEmpty();
    }
}