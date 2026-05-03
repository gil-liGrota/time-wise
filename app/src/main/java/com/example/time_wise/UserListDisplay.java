package com.example.time_wise;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

                        ArrayList<EfficientTime> efficiency = parseEfficientTimeList(d.get("efficiency"));
                        ArrayList<EfficientTime> unefficiency = parseEfficientTimeList(d.get("unefficiency"));
                        ArrayList<EfficientTime> sleep = parseEfficientTimeList(d.get("sleep"));

                        User u = new User(phoneNumber, userName, password,
                                efficiency, unefficiency, sleep,
                                null, null, null, null, null);

                        users.add(u);
                    }
                    adapter.notifyDataSetChanged();
                });


        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, users);

        lvUser.setAdapter(adapter);
    }

    private ArrayList<EfficientTime> parseEfficientTimeList(Object listObj) {
        ArrayList<EfficientTime> result = new ArrayList<>();
        if (listObj instanceof List<?>) {
            List<?> rawList = (List<?>) listObj;
            for (Object item : rawList) {
                if (item instanceof Map<?, ?>) {
                    Map<?, ?> map = (Map<?, ?>) item;
                    try {
                        DayOfWeek day = null;
                        if(map.get("day") != null) {
                            day = DayOfWeek.valueOf(map.get("day").toString());
                        }

                        String startStr = map.get("start").toString();
                        String endStr = map.get("end").toString();
                        LocalTime start = LocalTime.parse(startStr);
                        LocalTime end = LocalTime.parse(endStr);

                        EfficientTime et = new EfficientTime(day, start, end);
                        result.add(et);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }

}