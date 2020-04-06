package com.example.megacabpool;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.example.megacabpool.MainActivity.SHARED_PREFS;

public class ynAdapter extends FirestoreRecyclerAdapter<request,ynAdapter.ynHolder> {
    private FirebaseAuth mAuth;
    private FirebaseFirestore fb = FirebaseFirestore.getInstance();
    private CollectionReference cr = fb.collection("Users");
    private CollectionReference cr3 = fb.collection("Data");

    private CollectionReference cr1,cr2;

    private static final String SHARED_PREFS = "SharedPrefs";
    private onButtonClickListener listener;
    private static final String TAG = "hi";


    public ynAdapter(@NonNull FirestoreRecyclerOptions<request> options) {
        super(options);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ynHolder holder, int position, @NonNull request model) {
        holder.prompt.setText(model.getName()+", contact- " + model.getContact() +"\nhas requested to ride with you!");
    }

    @NonNull
    @Override
    public ynHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.yes_no_card,parent,false);
        return new ynHolder(v);
    }

    public void delete(int number) {
        getSnapshots().getSnapshot(number).getReference().delete();
    }

    class ynHolder extends RecyclerView.ViewHolder{

        TextView prompt;
        Button yes,no;

        public ynHolder(final View itemView){
            super(itemView);
            prompt=itemView.findViewById(R.id.prompt);
            yes=itemView.findViewById(R.id.yes);
            no=itemView.findViewById(R.id.no);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener!=null){
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onbuttonclick(getSnapshots().getSnapshot(position),position);
                    }
                }
            }
        });
        //match nid from mailbox with email in data collection and decrease the seat count
        //jaha req kia hai usme jisne kia hai uska email jaega
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            cr.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if(e!=null){
                        return;
                    }

                    for (QueryDocumentSnapshot qw: queryDocumentSnapshots){
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if(user!=null){
                            final String em =user.getEmail();
                            if (em.equals(qw.toObject(User.class).getEmail())){
                                String id1 = qw.getId();
                                cr2=cr.document(id1).collection("Mailbox");

                            }
                        }

                        cr2.addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                for (QueryDocumentSnapshot er : queryDocumentSnapshots) {

                                }
                                }
                        });

//                    cr3.addSnapshotListener(new EventListener<QuerySnapshot>() {
//                        @Override
//                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                            for (QueryDocumentSnapshot er : queryDocumentSnapshots){
//                                String id = er.getId();
//                                if(id.equals(impid)){
//
//                                    String s = er.toObject(request.class).getSeats();
//                                    int i = Integer.parseInt(s)-1;
//                                    String reset = String.valueOf(i);
//                                    Map<String,Object> mop = new HashMap<>();
//                                    mop.put("seats",reset);
//                                    cr3.document("seats").update(mop);
//
//                                }
//                            }
//                        }
//                    });






                    }
                }
            });}
        });




        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                cr.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e!=null){
                            return;
                        }

                        for (QueryDocumentSnapshot q: queryDocumentSnapshots){
                            String a = q.toObject(User.class).getNid();

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if(user!=null){
                                final String em =user.getEmail();
                                if (em.equals(q.toObject(User.class).getEmail())){
                                    String id1 = q.getId();
                                    cr1=cr.document(id1).collection("Mailbox");
                                }
                            }


                        }

                        cr1.addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                for (QueryDocumentSnapshot f : queryDocumentSnapshots){

                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    if(user!=null){
                                        final String em =user.getEmail();

                                        if(em.equals(f.toObject(User.class).getNid())){

                                        String idp =f.getId();
                                        cr1.document(idp).delete().addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "nahi hua");
                                            }
                                        });
                                    }}
                                }
                            }
                        });




                    }
                });

            }
        });
        }


    }
    public interface onButtonClickListener{
        void onbuttonclick(DocumentSnapshot documentSnapshot,int position);

    }
    public void setOnButtonClick(onButtonClickListener listener){
        this.listener=listener;
    }
}
