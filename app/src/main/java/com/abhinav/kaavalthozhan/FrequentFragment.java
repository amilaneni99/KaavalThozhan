package com.abhinav.kaavalthozhan;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abhinav.kaavalthozhan.adapters.FrequencyAdapter;
import com.abhinav.kaavalthozhan.adapters.HistroyAdapter;
import com.abhinav.kaavalthozhan.model.Incident;
import com.abhinav.kaavalthozhan.model.Vehicle;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class FrequentFragment extends Fragment {

    RecyclerView.LayoutManager layoutManager;
    FrequencyAdapter frequencyAdapter;
    RecyclerView recyclerView;
    List<Vehicle> vehicleList = new ArrayList<>();
    LottieAnimationView lottieAnimationView;
    FirebaseFirestore db;
    TextView noOffenders;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_frequent, container, false);
        vehicleList.clear();
        lottieAnimationView = view.findViewById(R.id.lottieAnimationView);
        recyclerView = view.findViewById(R.id.frequentRecycler);
        noOffenders = view.findViewById(R.id.noOffenders);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        db = FirebaseFirestore.getInstance();

//        db.collection("vehicles").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                vehicleList.clear();
//                if (task.isSuccessful()){
//                    if (task.getResult().size() != 0){
//                        for(QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
//                            vehicleList.add(queryDocumentSnapshot.toObject(Vehicle.class));
//                        }
//
//                        Collections.sort(vehicleList, new Comparator<Vehicle>() {
//                            @Override
//                            public int compare(Vehicle vehicle, Vehicle t1) {
//                                return t1.getOffenses()-vehicle.getOffenses();
//                            }
//                        });
//
//                        frequencyAdapter = new FrequencyAdapter(vehicleList,getContext());
//                        recyclerView.setAdapter(frequencyAdapter);
//                        lottieAnimationView.setVisibility(View.GONE);
//                        recyclerView.setVisibility(View.VISIBLE);
//                        noOffenders.setVisibility(View.GONE);
//                    }else{
//                        lottieAnimationView.setVisibility(View.GONE);
//                        recyclerView.setVisibility(View.INVISIBLE);
//                        noOffenders.setVisibility(View.VISIBLE);
//                    }
//                }else{
//                    lottieAnimationView.setVisibility(View.GONE);
//                    recyclerView.setVisibility(View.INVISIBLE);
//                    noOffenders.setVisibility(View.VISIBLE);
//                    noOffenders.setText("Error Occurred");
//                }
//            }
//        });

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        vehicleList.clear();
        System.out.println("Resumed");
        lottieAnimationView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        db.collection("vehicles").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().size() != 0){
                        for(QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                            vehicleList.add(queryDocumentSnapshot.toObject(Vehicle.class));
                        }
                        Collections.sort(vehicleList, new Comparator<Vehicle>() {
                            @Override
                            public int compare(Vehicle vehicle, Vehicle t1) {
                                if((t1.getOffenses()-vehicle.getOffenses())!=0){
                                    return t1.getOffenses()-vehicle.getOffenses();
                                }
                                String format = "dd/MM/yy hh:mm:ssa";
                                SimpleDateFormat sdf = new SimpleDateFormat(format);
                                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
                                String time1 = vehicle.getIncidents().get(vehicle.getIncidents().size()-1).getTimeStamp();
                                String time2 = t1.getIncidents().get(t1.getIncidents().size()-1).getTimeStamp();
                                int dC = 0;
                                try {
                                    Date d1 = sdf.parse(time1);
                                    Date d2 = sdf.parse(time2);
                                    dC = d2.compareTo(d1);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                return dC;
                            }
                        });
                        frequencyAdapter = new FrequencyAdapter(vehicleList,getContext());
                        recyclerView.setAdapter(frequencyAdapter);
                        lottieAnimationView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        noOffenders.setVisibility(View.GONE);
                    }else{
                        lottieAnimationView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.INVISIBLE);
                        noOffenders.setVisibility(View.VISIBLE);
                    }
                }else{
                    lottieAnimationView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.INVISIBLE);
                    noOffenders.setVisibility(View.VISIBLE);
                    noOffenders.setText("Error Occurred");
                }
            }
        });
    }
}
