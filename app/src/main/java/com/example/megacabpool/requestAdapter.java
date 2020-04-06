package com.example.megacabpool;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class requestAdapter extends FirestoreRecyclerAdapter<request, requestAdapter.requestHolder> {
    public static final String TAG = "requestAdapter";
    private OnItemClickListener listener;

    public requestAdapter(FirestoreRecyclerOptions<request> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(requestAdapter.requestHolder holder, int position, request request) {
        holder.from.setText(request.getFrom());
        holder.time.setText(request.getTime());
        holder.to.setText(request.getTo());
        holder.seats.setText(request.getSeats());
        holder.date.setText(request.getDate());
        holder.name.setText(request.getName());
        holder.contact.setText(request.getContact());
    }

    @Override
    public void onError(@NonNull FirebaseFirestoreException e) {
        super.onError(e);
        Log.d(TAG, "onError: " + e.toString());
    }

    @NonNull
    @Override
    public requestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_item, parent, false);
        return new requestHolder(v);

    }

    public void delete(int number) {
        getSnapshots().getSnapshot(number).getReference().delete();
    }


    class requestHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView from;
        TextView to;
        TextView contact;
        TextView date;
        TextView seats;
        TextView time;


        public requestHolder(@NonNull View itemView) {

            super(itemView);
            name = itemView.findViewById(R.id.name);
            from = itemView.findViewById(R.id.from);
            to = itemView.findViewById(R.id.to);
            time = itemView.findViewById(R.id.time);
            seats = itemView.findViewById(R.id.seats);
            contact = itemView.findViewById(R.id.contact);
            date = itemView.findViewById(R.id.date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    listener.onItemClick(getSnapshots().getSnapshot(position), position);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}