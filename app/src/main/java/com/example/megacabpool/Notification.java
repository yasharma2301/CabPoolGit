package com.example.megacabpool;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import static java.lang.System.exit;

public class Notification extends AppCompatActivity {
    private static final String SHARED = "Shared";
    private static final String TAG = "Notification";
    private ImageButton back;
    private FirebaseFirestore fb = FirebaseFirestore.getInstance();
    private CollectionReference cr = fb.collection("Users");
    private CollectionReference ir = fb.collection("Users");
    private CollectionReference crf = fb.collection("Data");
    private DocumentReference dcr;
    public static final int sms = 0;
    private CollectionReference cr2;
    private Boolean xoxo = true;
    private Boolean xoxot = true;
    private ynAdapter adapter;
    private FirebaseAuth mAuth;
    protected String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        final SharedPreferences spd = getSharedPreferences(SHARED, MODE_PRIVATE);
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
                    }
                }
            }
        });
        cr2 = cr.document(spd.getString("abc", "abc")).collection("Mailbox");
        Query query = cr2.orderBy("name", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions fcr = new FirestoreRecyclerOptions.Builder<request>().setQuery(query, request.class).build();
        adapter = new ynAdapter(fcr);

        RecyclerView recyclerView = findViewById(R.id.recycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(Notification.this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new ynAdapter.onButtonClickListener() {
            @Override
            public void onbuttonclick(DocumentSnapshot documentSnapshot, final int position) {
                User u = documentSnapshot.toObject(User.class);
                String impid = u.getImpid();
                dcr = crf.document(impid);
                ir.document(u.getNid()).collection("Mailbox");
                fb.collection("Data").document(impid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot dS, @Nullable FirebaseFirestoreException e) {
                        if (xoxot) {
                            xoxot = false;
                            final String s = (String) dS.get("seats");
                            final String Name = (String) dS.get("contact");
                            int t = Integer.parseInt(s) - 1;
                            Log.d(TAG, "av " + t);
                            if (xoxo) {
                                xoxo = false;
                                doThisAsWell(dcr, t);
                                adapter.delete(position);
                            }
                            sendSMS(Name);
                            deleteAllWithName(Name,ir);

                        }
                    }
                });
            }

        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.delete(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);


    }

    private void doThisAsWell(DocumentReference dcr, int t) {
        Map<String, Object> map = new HashMap<>();
        map.put("seats", String.valueOf(t));
        dcr.update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: Updated it");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: ", e);
            }
        });
        map.clear();
    }

    private void deleteAllWithName(final String Name,CollectionReference ir) {
        ir.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (QueryDocumentSnapshot q : queryDocumentSnapshots) {
                    User rt = q.toObject(User.class);
                    String con = rt.getContact();
                    if (con.equals(Name)) {
                        q.getReference().delete();
                    }
                }
                Intent intent = new Intent(Notification.this, mainPage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
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

    private void sendSMS(String number) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number, null, "Hi! I've accepted your CabPool request. " +
                "Let's pool and reach our destination." +
                "(This msg was autogenerated by Mcp)", null, null);
    }

}
