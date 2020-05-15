package com.abhinav.kaavalthozhan;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.abhinav.kaavalthozhan.adapters.HistroyAdapter;
import com.abhinav.kaavalthozhan.model.Incident;
import com.abhinav.kaavalthozhan.model.Vehicle;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;


public class SearchFragment extends Fragment {
    EditText regNo;
    Button search,addVehicle;
    CardView cardView;
    FirebaseFirestore db;
    TextView notFound, regNoText,vehicleTypeText,noOfOffenses,timeStamp,historyTextView;
    RecyclerView historyRecycler;
    RecyclerView.LayoutManager layoutManager;
    HistroyAdapter histroyAdapter;
    List<Incident> incidentList;
    LottieAnimationView lottieAnimationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        db = FirebaseFirestore.getInstance();

        regNo = view.findViewById(R.id.search);
        search = view.findViewById(R.id.searchButton);
        notFound = view.findViewById(R.id.notFound);
        addVehicle = view.findViewById(R.id.addVehicle);
        cardView = view.findViewById(R.id.card);
        regNoText = view.findViewById(R.id.reg);
        vehicleTypeText = view.findViewById(R.id.vehicleType);
        noOfOffenses = view.findViewById(R.id.offensesNo);
        timeStamp = view.findViewById(R.id.timeStampDisp);
        historyRecycler = view.findViewById(R.id.historyRecycler);
        historyTextView = view.findViewById(R.id.historyTextView);
        lottieAnimationView = view.findViewById(R.id.lottieAnimationView);

        layoutManager = new LinearLayoutManager(this.getActivity());
        historyRecycler.setLayoutManager(layoutManager);
        historyRecycler.setNestedScrollingEnabled(false);


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lottieAnimationView.setVisibility(View.VISIBLE);
                historyRecycler.setVisibility(View.GONE);
                historyTextView.setVisibility(View.GONE);
                notFound.setVisibility(View.GONE);
                addVehicle.setVisibility(View.GONE);
                cardView.setVisibility(View.GONE);
                if (regNo.getText().toString().trim().length() != 0){
                    String registrationNumber = regNo.getText().toString();
                    db.collection("vehicles").document(registrationNumber).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()){
                                    Vehicle vehicle = document.toObject(Vehicle.class);
                                    regNoText.setText(vehicle.getRegistrationNumber());
                                    vehicleTypeText.setText(vehicle.getType());
                                    noOfOffenses.setText(vehicle.getOffenses()+"");
                                    timeStamp.setText(vehicle.getIncidents().get(0).getTimeStamp());

                                    incidentList = vehicle.getIncidents();
                                    histroyAdapter = new HistroyAdapter(incidentList, getContext());
                                    historyRecycler.setAdapter(histroyAdapter);
                                    lottieAnimationView.setVisibility(View.GONE);
                                    historyRecycler.setVisibility(View.VISIBLE);
                                    historyTextView.setVisibility(View.VISIBLE);
                                    notFound.setVisibility(View.GONE);
                                    addVehicle.setVisibility(View.GONE);
                                    cardView.setVisibility(View.VISIBLE);
                                }else{
                                    lottieAnimationView.setVisibility(View.GONE);
                                    notFound.setVisibility(View.VISIBLE);
                                    addVehicle.setVisibility(View.VISIBLE);
                                    cardView.setVisibility(View.GONE);
                                }
                            }else{
                                lottieAnimationView.setVisibility(View.GONE);
                                notFound.setVisibility(View.VISIBLE);
                                notFound.setText("Error Occurred");
                                addVehicle.setVisibility(View.GONE);
                                cardView.setVisibility(View.GONE);
                            }
                        }
                    });
                }else{
                    lottieAnimationView.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Please fill in Reg. No. to search", Toast.LENGTH_SHORT).show();
                }
            }
        });

        addVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TabLayout tabhost = (TabLayout) getActivity().findViewById(R.id.tabs);
                tabhost.getTabAt(1).select();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        regNo.getText().clear();
        historyTextView.setVisibility(View.GONE);
        cardView.setVisibility(View.GONE);
        historyRecycler.setVisibility(View.GONE);
    }
}
