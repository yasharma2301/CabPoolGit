package com.example.megacabpool;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class directory extends Fragment {
    private TextView name,contact;
    private DirectoryAdapter DirectoryAdapter;
    private FirebaseFirestore fb = FirebaseFirestore.getInstance();
    private CollectionReference c = fb.collection("Users");
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_directory,container,false);
        mAuth = FirebaseAuth.getInstance();
        name=v.findViewById(R.id.name);
        contact=v.findViewById(R.id.contact);
        Query query = c.orderBy("name", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions fcr = new FirestoreRecyclerOptions.Builder<request>().setQuery(query, request.class).build();
        DirectoryAdapter = new DirectoryAdapter(fcr);

        RecyclerView recyclerView =v.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(DirectoryAdapter);

        DirectoryAdapter.setOnItemClickListener(new DirectoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot ds, int position) {
                Intent intent = new Intent(getActivity(),sendtext.class);
                intent.putExtra("customObject", (Parcelable) ds);
                startActivity(intent);
            }
        });

        return v;
    }
    @Override
    public void onStart() {
        super.onStart();
        DirectoryAdapter.startListening();
    }


    @Override
    public void onStop() {
        super.onStop();
        DirectoryAdapter.stopListening();
    }
}
