package com.example.megacabpool;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
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

public class ynAdapter extends FirestoreRecyclerAdapter<request, ynAdapter.ynHolder> {
    private FirebaseAuth mAuth;
    private FirebaseFirestore fb = FirebaseFirestore.getInstance();
    private CollectionReference cr = fb.collection("Users");
    private CollectionReference cr3 = fb.collection("Data");
    public static  final String TAG="notification";
    private CollectionReference cr1, cr2;

    private CollectionReference crf = fb.collection("Data");
    private DocumentReference dcr;

    private DocumentReference dr;
    private onButtonClickListener listener;

// yes set click listener -> decrease seats


    public ynAdapter(@NonNull FirestoreRecyclerOptions<request> options) {
        super(options);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ynHolder holder, int position, @NonNull request model) {
        holder.prompt.setText(model.getName() + ", contact- " + model.getContact() + "\nhas requested to ride with you!");
    }

    @NonNull
    @Override
    public ynHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.yes_no_card, parent, false);
        return new ynHolder(v);
    }

    public void delete(int number) {
        getSnapshots().getSnapshot(number).getReference().delete();
    }

    public void confirm(int number){
        Log.d(TAG, "confirm: "+"asdhbasdkjfbsdkjlfbsadkjbfklsadjdblkjasdbkljbsklbsdkljvbkajlsd");
        User u = getSnapshots().getSnapshot(number).toObject(User.class);
        String imp = u.getImpid();
        Log.d(TAG, "confirm: "+imp);
        dcr=crf.document(imp);

        crf.document(imp).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String s = (String) documentSnapshot.get("seats");
                int t = Integer.parseInt(s) - 1;
                Map<String, Object> map = new HashMap<>();
                map.put("seats", String.valueOf(t));
                Log.d(TAG, "gghh "+ t);
                dcr.update(map);
                map.clear();
            }
        });

    }

    class ynHolder extends RecyclerView.ViewHolder {

        TextView prompt;


        public ynHolder(final View itemView) {
            super(itemView);
            prompt = itemView.findViewById(R.id.prompt);
          //  yes = itemView.findViewById(R.id.yes);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onbuttonclick(getSnapshots().getSnapshot(position), position);
                            Log.d(TAG, "onClick: ");
                        }
                    }
                }
            });
            //match nid from mailbox with email in data collection and decrease the seat count
            //jaha req kia hai usme jisne kia hai uska email jaega


        }
    }

    public interface onButtonClickListener {
        void onbuttonclick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(onButtonClickListener listener) {
        this.listener = listener;
    }

    public void doThisOnYes(final String imp){
        cr3.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                for (QueryDocumentSnapshot qd : queryDocumentSnapshots) {

                    if(qd.getId().equals(imp)){
                        request c = qd.toObject(request.class);
                        int t = Integer.parseInt(c.getSeats()) - 1;
                        Map<String, Object> map = new HashMap<>();
                        map.put("seats", String.valueOf(t));
                        Log.d(TAG, "seatCount new "+t);
                        dr.update(map);
                        break;
                    }
                }
            }});
    }
}
