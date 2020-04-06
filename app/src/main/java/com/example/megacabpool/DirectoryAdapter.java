package com.example.megacabpool;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class DirectoryAdapter extends FirestoreRecyclerAdapter<request,DirectoryAdapter.directoryHolder> {
    public static final String TAG = "DirectoryAdapter";
    private OnItemClickListener listener;
    public DirectoryAdapter(@NonNull FirestoreRecyclerOptions<request> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(DirectoryAdapter.directoryHolder holder, int position, @NonNull request request) {
        holder.name.setText(request.getName());
        holder.contact.setText(request.getContact());
    }

    @NonNull
    @Override
    public DirectoryAdapter.directoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.directory_card, parent, false);
        return new directoryHolder(v);
    }

    class directoryHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView contact;

        public directoryHolder(@NonNull final View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            contact=itemView.findViewById(R.id.contact);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //Intent intent = new Intent(itemView.getContext(), sendtext.class);
                    int position =getAdapterPosition();
                   // request r = getSnapshots().getSnapshot(position);
                    //intent.putExtra("CustomObject",r.getName());


                    listener.onItemClick(getSnapshots().getSnapshot(position),position);
                }
            });
        }
    }
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot ds,int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener=listener;
    }

}
