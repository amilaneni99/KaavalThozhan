package com.abhinav.kaavalthozhan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.abhinav.kaavalthozhan.adapters.ApprovalAdapter;
import com.abhinav.kaavalthozhan.model.UserApprovalRequest;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ApproveActivity extends AppCompatActivity {

    RecyclerView approveRecycler;
    RecyclerView.LayoutManager layoutManager;
    ApprovalAdapter approvalAdapter;
    List<UserApprovalRequest> userApprovalRequests = new ArrayList<>();
    LottieAnimationView lottieAnimationView;
    FirebaseFirestore db;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userApprovalRequests.clear();
        setContentView(R.layout.activity_approve);
        approveRecycler = findViewById(R.id.approveRecycler);
        lottieAnimationView = findViewById(R.id.lottieAnimationView);
        toolbar = findViewById(R.id.toolbar);
        layoutManager = new LinearLayoutManager(this);
        approveRecycler.setLayoutManager(layoutManager);
        lottieAnimationView.setVisibility(View.VISIBLE);
        approveRecycler.setVisibility(View.GONE);

        toolbar.setTitle("Approve Requests");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = FirebaseFirestore.getInstance();
        db.collection("pending")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().size() != 0){
                                for (QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                                    String nameTemp = queryDocumentSnapshot.getData().get("name").toString();
                                    String userNameTemp = queryDocumentSnapshot.getData().get("username").toString();
                                    String passwordTemp = queryDocumentSnapshot.getData().get("password").toString();
                                    String id = queryDocumentSnapshot.getId();
                                    userApprovalRequests.add(new UserApprovalRequest(id,nameTemp,userNameTemp,passwordTemp));
                                }
                                approvalAdapter = new ApprovalAdapter(userApprovalRequests,ApproveActivity.this,db);
                                approveRecycler.setAdapter(approvalAdapter);
                                lottieAnimationView.setVisibility(View.GONE);
                                approveRecycler.setVisibility(View.VISIBLE);
                            }else{
                                lottieAnimationView.setVisibility(View.GONE);
                                Toast.makeText(ApproveActivity.this, "No Pending Requests", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
