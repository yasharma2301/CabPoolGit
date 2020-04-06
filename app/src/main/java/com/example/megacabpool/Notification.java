package com.example.megacabpool;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;

public class Notification extends AppCompatActivity {
    private static final String SHARED = "Shared";
    private static final String TAG = "Notification";
    private ImageButton back;
    private FirebaseFirestore fb = FirebaseFirestore.getInstance();
    private CollectionReference cr = fb.collection("Users");
    private CollectionReference cr2;
    private ynAdapter adapter;
    private FirebaseAuth mAuth;
    protected String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        back = findViewById(R.id.back);
        mAuth = FirebaseAuth.getInstance();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Notification.this, mainPage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        final String em = mAuth.getCurrentUser().getEmail();

        final SharedPreferences spd = getSharedPreferences(SHARED, MODE_PRIVATE);

        cr.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                Map<String, Object> m = new HashMap<>();
                for (QueryDocumentSnapshot qd : queryDocumentSnapshots) {
                    User u = qd.toObject(User.class);
                    String emd = u.getEmail();
                    if (emd.equals(em)) {
                        id = qd.getId();
                        Log.d(TAG, "ae " + id);
                        spd.edit().putString("abc", id).apply();
                        Log.d(TAG, "aaaaaaae1 " + id);
                        Log.d(TAG, "aaaaaaae1 "+spd.getString("abc",null));
                    }
                }
            }
        });

        cr2 = cr.document(spd.getString("abc","abc")).collection("Mailbox");

        Query query = cr2.orderBy("name", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions fcr = new FirestoreRecyclerOptions.Builder<request>().setQuery(query, request.class).build();
        adapter = new ynAdapter(fcr);

        RecyclerView recyclerView = findViewById(R.id.r);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(Notification.this));
        recyclerView.setAdapter(adapter);

    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
