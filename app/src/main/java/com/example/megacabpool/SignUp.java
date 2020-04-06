package com.example.megacabpool;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
public static final String TAG = "SignUp";
private static final String SHARED_PREFS = "SharedPrefs";
private Button register,login;
private EditText name,email,password,contact;
private ProgressBar progressbar;
private FirebaseAuth mAuth;
private FirebaseFirestore fb=FirebaseFirestore.getInstance();
private CollectionReference cr = fb.collection("Users");
private  DocumentReference dr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final SharedPreferences spd = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);

        register = findViewById(R.id.register);
        login = findViewById(R.id.login);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        contact = findViewById(R.id.contact);
        progressbar = findViewById(R.id.progressbar);
        mAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(SignUp.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String e= email.getText().toString();
                String p = password.getText().toString();
                final String n = name.getText().toString();
                final String c = contact.getText().toString();

                if (n.isEmpty()){
                    name.setError("Name is required");
                    name.requestFocus();
                    return;
                }
                if (e.isEmpty()){
                    email.setError("Email is required");
                    email.requestFocus();
                    return;
                }
                if(p.isEmpty()){
                    password.setError("Password is required");
                    password.requestFocus();
                    return;
                }

                if (c.isEmpty()){
                    contact.setError("Contact is required");
                    contact.requestFocus();
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(e).matches()){
                    email.setError("Enter a Valid email");
                }
                if(p.length()<6){
                    password.setError("Minimum length of password should be 6");
                    password.requestFocus();
                    return;
                }
                progressbar.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(e,p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            final User user = new User(n,e,c,null,null);
                            spd.edit().putString("name",n);
                            spd.edit().putString("contact",c);
                            Log.d(TAG, "onComplete: "+user.getNid());
                            cr.add(user).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        progressbar.setVisibility(View.GONE);
                                        Toast.makeText(SignUp.this, "Signup Success", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignUp.this,MainActivity.class));
                                    }else{
                                        Toast.makeText(SignUp.this,task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, task.getException().toString());
                                    }
                                }
                            });

                        }
                        else{
                            if (task.getException() instanceof FirebaseAuthUserCollisionException){
                                Toast.makeText(SignUp.this, "User already exists\nTry logging in", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(SignUp.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
    }
}
