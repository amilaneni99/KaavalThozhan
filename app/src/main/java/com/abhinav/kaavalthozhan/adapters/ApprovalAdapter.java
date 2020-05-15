package com.abhinav.kaavalthozhan.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abhinav.kaavalthozhan.R;
import com.abhinav.kaavalthozhan.model.UserApprovalRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;

public class ApprovalAdapter extends RecyclerView.Adapter<ApprovalAdapter.MyViewHolder> {

    List<UserApprovalRequest> userApprovalRequests;
    private Context mCtx;
    FirebaseFirestore db;

    public ApprovalAdapter(List<UserApprovalRequest> userApprovalRequests, Context mCtx, FirebaseFirestore db) {
        this.userApprovalRequests = userApprovalRequests;
        this.mCtx = mCtx;
        this.db = db;
    }

    @NonNull
    @Override
    public ApprovalAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.approve_item, parent, false);
        return new ApprovalAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApprovalAdapter.MyViewHolder holder, final int position) {
        final UserApprovalRequest userApprovalRequest = userApprovalRequests.get(position);
        holder.name.setText(userApprovalRequest.getName());
        holder.mobile.setText(userApprovalRequest.getUsername());
        final HashMap<String,Object> map = new HashMap<>();
        map.put("username",userApprovalRequest.getUsername());
        map.put("password",userApprovalRequest.getPassword());
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("credentials")
                        .document(userApprovalRequest.getUsername())
                        .set(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    userApprovalRequests.remove(userApprovalRequest);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position,userApprovalRequests.size());
                                }
                            }
                        });

                db.collection("pending").document(userApprovalRequest.getUsername())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(mCtx,"Removed",Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        holder.deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("pending").document(userApprovalRequest.getUsername())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                userApprovalRequests.remove(userApprovalRequest);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position,userApprovalRequests.size());
                                Toast.makeText(mCtx,"Removed",Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return userApprovalRequests.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, mobile;
        Button accept, deny;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameUser);
            mobile = itemView.findViewById(R.id.mobileNoUser);
            accept = itemView.findViewById(R.id.approve);
            deny = itemView.findViewById(R.id.deny);
        }
    }
}
