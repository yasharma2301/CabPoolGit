package com.example.megacabpool;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class mainPage extends AppCompatActivity {
    private static final String TAG = "mainPage";
    private Button plus, signout;
    private static final String SHARED = "Shared";
    private static final String SHARED_PREFS = "sharedPrefs";
    private FirebaseFirestore fb = FirebaseFirestore.getInstance();
    private CollectionReference cr = fb.collection("Data");
    private CollectionReference cr3 = fb.collection("Users");
    private Boolean xoxo =true;
    protected SharedPreferences pr;
    private DocumentReference dr;
    private requestAdapter requestadapter;
    private ImageButton notifiaction, chat;
    private CollectionReference cr2;
    public static final int sms = 0;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        plus = findViewById(R.id.plus);
        if (ContextCompat.checkSelfPermission(mainPage.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mainPage.this, new String[]{Manifest.permission.SEND_SMS}, sms);
        }

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mainPage.this, requestDetails.class));
            }
        });
        setUpRecyclerView();

        notifiaction = findViewById(R.id.notification);
        notifiaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mainPage.this, Notification.class);
                startActivity(intent);
            }
        });

        signout = findViewById(R.id.signout);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences spd = getSharedPreferences(SHARED, MODE_PRIVATE);
                //spd.edit().putString("logged", false).apply();
                spd.edit().clear().apply();
                preferences.edit().clear().apply();

                mAuth.signOut();
                Intent intent = new Intent(mainPage.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();
        requestadapter.startListening();

        cr.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                for (QueryDocumentSnapshot qd : queryDocumentSnapshots) {
                    request re = qd.toObject(request.class);
                    String dataDate = re.getDate();
                    String seats = re.getSeats();
                    int seat = Integer.parseInt(seats);

                    if (seat <= 0) {
                        fb.collection("Data").document(qd.getId()).delete();
                    }


                    LocalDate currentdate = LocalDate.now();
                    int currentDay = currentdate.getDayOfMonth();

                    int d = Integer.parseInt(dataDate.substring(0, 2));
                    int w = Integer.parseInt(dataDate.substring(3, 5));
                    int y = Integer.parseInt(dataDate.substring(6));

                    SimpleDateFormat day = new SimpleDateFormat("DD");
                    SimpleDateFormat month = new SimpleDateFormat("MM");
                    SimpleDateFormat year = new SimpleDateFormat("YYYY");
                    Date date = new Date();

                    int CurDay = Integer.parseInt(day.format(date));
                    int CurMonth = Integer.parseInt(month.format(date));
                    int CurYear = Integer.parseInt(year.format(date));
                    Log.d(TAG, "hullu" + CurDay);

                    if (CurYear > y) {
                        fb.collection("Data").document(qd.getId()).delete();
                    } else if (CurYear == y && CurMonth > w) {
                        fb.collection("Data").document(qd.getId()).delete();
                    } else if (CurYear == y && CurMonth == w && currentDay > d) {
                        fb.collection("Data").document(qd.getId()).delete();
                    }
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        requestadapter.stopListening();
    }

    public void setUpRecyclerView() {

        Query query = cr.orderBy("date", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions fcr = new FirestoreRecyclerOptions.Builder<request>().setQuery(query, request.class).build();
        requestadapter = new requestAdapter(fcr);

        RecyclerView recyclerView = findViewById(R.id.r);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(requestadapter);


        requestadapter.setOnItemClickListener(new requestAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final DocumentSnapshot documentSnapshot, int position) {
                new AlertDialog.Builder(mainPage.this)
                        .setMessage("Do you want to book a seat?").setTitle("We are happy that you found a ride of your choice!")
                        .setPositiveButton("Send a request", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final SharedPreferences spd = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

                                cr.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                        if (e != null) {
                                            return;
                                        }

                                        final String mae = mAuth.getCurrentUser().getEmail();
                                        spd.edit().putString("mauth", mae).apply();
                                        pr = getSharedPreferences(SHARED, MODE_PRIVATE);
                                        pr.edit().putString("mauth", mae).apply();

                                        final SharedPreferences spd = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

                                        final request req = documentSnapshot.toObject(request.class);
                                        final String id = documentSnapshot.getId();
                                        spd.edit().putString("important_id", id).apply();


                                        cr3.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    return;
                                                }
                                                String mae = req.getEmail();

                                                for (QueryDocumentSnapshot qdt : queryDocumentSnapshots) {
                                                    request rt = qdt.toObject(request.class);
                                                    if (mae.equals(rt.getEmail())) {
                                                        String id2 = qdt.getId();
                                                        Log.d(TAG, "tf "+id2);
                                                            doThis(documentSnapshot, id2);

                                                    }


                                                }
                                            }
                                        });
                                    }
                                });
                                Toast.makeText(mainPage.this, "Request sent successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("Cancel", null).show();

            }
        });
    }

    public void doThis(final DocumentSnapshot documentSnapshot, final String id) {
        cr3.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                String mae = mAuth.getCurrentUser().getEmail();

                for (QueryDocumentSnapshot q : queryDocumentSnapshots) {
                    User ur = q.toObject(User.class);
                    if (ur.getEmail().equals(mae)) {

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }

                        String hhj = id;
                        Log.d(TAG, "the value: " + hhj);
                        cr2 = cr3.document(hhj).collection("Mailbox");
                        cr2.add(ur).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {

                                cr2.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot q, @Nullable FirebaseFirestoreException e) {
                                        for (QueryDocumentSnapshot d : q) {

                                            Map<String, Object> ma = new HashMap<>();
                                            Map<String, Object> max = new HashMap<>();
                                            max.put("impid", documentSnapshot.getId());
                                            ma.put("nid", documentSnapshot.toObject(request.class).getEmail());

                                            cr2.document(d.getId()).update(ma);
                                            cr2.document(d.getId()).update(max);

                                        }
                                    }
                                });


                                Log.d(TAG, "onComplete: added the data successfully");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: " + e);
                            }
                        });
                    }
                }
            }
        });
    }
}







