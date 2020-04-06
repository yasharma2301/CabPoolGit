package com.example.megacabpool;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.megacabpool.MainActivity.SHARED_PREFS;

public class mainPage extends AppCompatActivity {
    private static final String TAG = "mainPage";
    private Button plus, signout;
    private static final String SHARED = "Shared";
    private static final String SHARED_PREFS = "sharedPrefs";
    private FirebaseFirestore fb = FirebaseFirestore.getInstance();
    private CollectionReference cr = fb.collection("Data");
    private CollectionReference cr3 = fb.collection("Users");

    protected SharedPreferences pr;

    private DocumentReference dr;
    private requestAdapter requestadapter;
    private ImageButton notifiaction, chat;
    private CollectionReference cr2;
    private static int SPLASH_TIME = 1000;
    public static final String KEY_SEATS = "seats";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        plus = findViewById(R.id.plus);


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


        chat = findViewById(R.id.chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mainPage.this, message.class);
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


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                //requestadapter.delete(viewHolder.getAdapterPosition());
                //For chat options
            }
        }).attachToRecyclerView(recyclerView);


        requestadapter.setOnItemClickListener(new requestAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final DocumentSnapshot documentSnapshot, int position) {
                new AlertDialog.Builder(mainPage.this)
                        .setMessage("Do you want to book a seat?").setTitle("We are happy that you found a ride of your choice!")
                        .setPositiveButton("Book a seat", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.d(TAG, "aaaaaa  ");
                                final SharedPreferences spd = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                                cr.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                        if (e != null) {
                                            return;
                                        }

                                        final String mae = mAuth.getCurrentUser().getEmail();
                                        spd.edit().putString("mauth",mae).apply();
                                        pr=getSharedPreferences(SHARED,MODE_PRIVATE);
                                        pr.edit().putString("mauth",mae).apply();

                                        final SharedPreferences spd = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

                                        final request req = documentSnapshot.toObject(request.class);
                                        int t = Integer.parseInt(req.getSeats()) - 1;
                                        final String id = documentSnapshot.getId();
                                        spd.edit().putString("important_id", id).apply();
                                        Log.d(TAG, "aaaaaa  " + id);



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
                                                        spd.edit().putString("lele", id2).apply();
                                                    }


                                                }
                                            }
                                        });

                                        dr = cr.document(id);
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("seats", String.valueOf(t));
                                        dr.update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "Updated the data successfully");
                                                Toast.makeText(mainPage.this, "Congrats you have successfully booked a seat!", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "onFailure: " + e);
                                            }
                                        });
                                    }
                                });


                                cr3.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                                        String mae = mAuth.getCurrentUser().getEmail();

                                        for (QueryDocumentSnapshot q : queryDocumentSnapshots) {
                                            User ur = q.toObject(User.class);
                                            if (ur.getEmail().equals(mae)) {

                                                String hhj = spd.getString("lele", "aaabbb");
                                                cr2 = cr3.document(hhj).collection("Mailbox");

                                                cr2.add(ur).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentReference> task) {

                                                        cr2.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onEvent(@Nullable QuerySnapshot q, @Nullable FirebaseFirestoreException e) {
                                                                for (QueryDocumentSnapshot d:q){

                                                                    Map<String,Object> ma = new HashMap<>();
                                                                    Map<String,Object> max = new HashMap<>();
                                                                    max.put("impid",documentSnapshot.getId());
                                                                    ma.put("nid",documentSnapshot.toObject(request.class).getEmail());

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
                        }).setNegativeButton("Cancel", null).show();
            }
        });
    }
}







