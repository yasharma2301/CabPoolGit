package com.example.megacabpool;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;

public class sendtext extends AppCompatActivity {
public static final String TAG = "sendtext";
    private TextView title;

FirebaseAuth mAuth = FirebaseAuth.getInstance();
FirebaseFirestore fb = FirebaseFirestore.getInstance();
CollectionReference cr = fb.collection("Users");
DocumentReference dr ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendtext);



        title = findViewById(R.id.title);

        Intent i  = getIntent();
        request r = i.getParcelableExtra("customObject");
        String name = r.getName();

        title.setText(name);

//final String ui = i.getStringExtra("userid");
//        cr.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                if (e != null) {
//                    return;
//                }
//
//                    for (QueryDocumentSnapshot qd : queryDocumentSnapshots) {
//
//                        User u = qd.toObject(User.class);
//
//                        if (u.getUi()!=null&&ui!=null&& ui.equalsIgnoreCase(u.getUi())){
//                        if(u.getUi().equals(ui)) {
//                            title.setText(u.getName());
//                        }
//            }
//        }}});



    }
}
