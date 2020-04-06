package com.example.megacabpool;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.Calendar;

public class requestDetails extends AppCompatActivity {
    private Button submit;
    public final static String TAG="requestDetails";
    private EditText leaving_from, going_to, time, seats, name, date, contact;
    private ImageButton imageButton2;
    Context cont = this;

    private FirebaseFirestore fb = FirebaseFirestore.getInstance();
    private CollectionReference cr = fb.collection("Users");
    private CollectionReference cr2 = fb.collection("Data");
    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        cr.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
            if(e!=null){
                return;
            }
                String n = "";
                String c = "";
                for (QueryDocumentSnapshot qd : queryDocumentSnapshots) {
                    User user = qd.toObject(User.class);
                    if (user.getEmail().equals(mAuth.getCurrentUser().getEmail())) {
                        c += user.getContact();
                        n += user.getName();
                        name.setText(n);
                        contact.setText(c);
                    }
                    }
        }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);
        submit = findViewById(R.id.submit);
        imageButton2 = findViewById(R.id.imageButton2);
        leaving_from = findViewById(R.id.leaving_from);
        going_to = findViewById(R.id.going_to);
        contact = findViewById(R.id.contact);
        date = findViewById(R.id.date);
        name = findViewById(R.id.name);
        time = findViewById(R.id.time);
        seats = findViewById(R.id.seats);

        mAuth = FirebaseAuth.getInstance();

        Calendar calendar = Calendar.getInstance();
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int month = calendar.get(Calendar.MONTH);
        final int year = calendar.get(Calendar.YEAR);



        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog tpd = new TimePickerDialog(cont,new TimePickerDialog.OnTimeSetListener(){
                            @Override
                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                time.setText(new DecimalFormat("00").format(i)+ " : " +new DecimalFormat("00").format(i1)+ " hrs.");
                            }
                        },hour,minute,false);
                tpd.show();
            }
        });


        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd = new DatePickerDialog(cont, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        date.setText(new DecimalFormat("00").format(i2)+"/"+ new DecimalFormat("00").format(i1+1) +"/"+i);
                    }
                },year,month,day);
                dpd.show();
            }
        });


        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requestDetails.this,mainPage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String l = leaving_from.getText().toString().trim();
                String d = date.getText().toString().trim();
                String ti = time.getText().toString().trim();
                String s = seats.getText().toString().trim();
                String g = going_to.getText().toString().trim();
                String na = name.getText().toString().trim();
                String co = contact.getText().toString().trim();

                if (l.isEmpty()) {
                    leaving_from.setError("This is a mandatory field");
                    leaving_from.requestFocus();
                    return;
                }
                if (d.isEmpty()) {
                    date.setError("This is a mandatory field");
                    date.requestFocus();
                    return;
                }
                if (ti.isEmpty()) {
                    time.setError("This is a mandatory field");
                    time.requestFocus();
                    return;
                }
                if (s.isEmpty()) {
                    seats.setError("This is a mandatory field");
                    seats.requestFocus();
                    return;
                }
                if (g.isEmpty()) {
                    going_to.setError("This is a mandatory field");
                    going_to.requestFocus();
                    return;
                }
                request r = new request(co, na, l, g, ti, s, d,mAuth.getCurrentUser().getEmail());
                cr2.add(r);
                Intent intent = new Intent(requestDetails.this,mainPage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
         }
}
