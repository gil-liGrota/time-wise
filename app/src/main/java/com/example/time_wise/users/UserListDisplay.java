package com.example.time_wise.users;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.time_wise.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserListDisplay extends AppCompatActivity {
    private ListView lvUser;
    private ArrayList<User> users;
    private ArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_list_display);
        lvUser = findViewById(R.id.lvUsers);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        users = new ArrayList<>();

        db.collection("users")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<DocumentSnapshot> list = snapshot.getDocuments();
                    for (DocumentSnapshot d : snapshot.getDocuments()) {

                        String phoneNumber = d.getString("phoneNumber");
                        String userName = d.getString("userName");
                        String password = d.getString("password");

                        int notificationHour = d.getLong("notificationHour").intValue();
                        int notificationMinute = d.getLong("notificationMinute").intValue();

                        User u = new User(phoneNumber, userName, password,
                                null, null, null, null, null, notificationHour, notificationMinute);

                        users.add(u);
                    }
                    adapter.notifyDataSetChanged();
                });


        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, users);

        lvUser.setAdapter(adapter);
    }

}