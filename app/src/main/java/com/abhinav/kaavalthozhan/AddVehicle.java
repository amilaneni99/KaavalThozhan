package com.abhinav.kaavalthozhan;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.abhinav.kaavalthozhan.model.Incident;
import com.abhinav.kaavalthozhan.model.Vehicle;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class AddVehicle extends Fragment {
    private Spinner spinner;
    private int PERMISSION_ID = 22;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private String latitude, longitude, registrationNo, driverName, remarksText, vehicleTypeText, locationAddressText;
    private Vehicle vehicle;
    private LottieAnimationView lottieAnimationView;
    private AlertDialog.Builder builder;

    private TextView lat, lon;
    private EditText regNo, name, remarks, locationAddress;

    private Button submit;

    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getActivity());
        getLastLocation();

         db = FirebaseFirestore.getInstance();

        View view = inflater.inflate(R.layout.fragment_add_vehicle, container, false);
        lat = view.findViewById(R.id.lat);
        lon = view.findViewById(R.id.lon);
        regNo = view.findViewById(R.id.regNoAdd);
        name = view.findViewById(R.id.driverName);
        remarks = view.findViewById(R.id.remarks);
        submit = view.findViewById(R.id.submit);
        locationAddress = view.findViewById(R.id.location);
        lottieAnimationView = view.findViewById(R.id.lottieAnimationView);

        ActivityCompat.requestPermissions( this.getActivity(),
                new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 2);

        builder = new AlertDialog.Builder(this.getContext());

        spinner = view.findViewById(R.id.vehicleType);
        final List<String> vehicleTypes = new ArrayList<>();
        vehicleTypes.add("Motorbike");
        vehicleTypes.add("Car");
        vehicleTypes.add("Heavy Vehicle");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this.getActivity(),R.layout.spinner_item,vehicleTypes);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                vehicleTypeText = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                vehicleTypeText = "Motorbike";
            }
        });


        view.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (remarks.getText().toString().trim().length() != 0){
                    remarksText = remarks.getText().toString();
                }else{
                    remarksText = "";
                }
                if (name.getText().toString().trim().length() != 0){
                    driverName = name.getText().toString();
                }else{
                    driverName = "";
                }
                if (locationAddress.getText().toString().trim().length() != 0){
                    locationAddressText = locationAddress.getText().toString();
                    String temp = "";
                    Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                    try {
                        List<Address> addressList = geocoder.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 1);
                        if (addressList != null && addressList.size() > 0){
                            Address address = addressList.get(0);
                            if (address.getLocality() != ""){
                                temp = address.getLocality();
                            }
                            locationAddressText += ", "+temp;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                    try {
                        List<Address> addressList = geocoder.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 1);
                        if (addressList != null && addressList.size() > 0){
                            Address address = addressList.get(0);
                            StringBuilder stringBuilder = new StringBuilder();
                            for(int i=0;i<address.getMaxAddressLineIndex();i++){
                                stringBuilder.append(address.getAddressLine(i)).append(", ");
                            }
                            if (address.getLocality() != ""){
                                stringBuilder.append(address.getLocality()).append(", ");
                            }
                            locationAddressText = stringBuilder.toString();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                String format = "dd/MM/yy hh:mm:ssa";
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
                final String timeStamp = sdf.format(new Date());
                final Incident incident = new Incident(timeStamp,new GeoPoint(Double.parseDouble(latitude),Double.parseDouble(longitude)),driverName, locationAddressText, remarksText);


                if (regNo.getText().toString().trim().length() == 0){
                    Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }else{
                    lottieAnimationView.setVisibility(View.VISIBLE);
                    registrationNo = regNo.getText().toString().replace("\\s+","");
                    DocumentReference docRef = db.collection("vehicles").document(registrationNo);
                    vehicle = new Vehicle(registrationNo,vehicleTypeText,1, Arrays.asList(incident));

                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                final DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()){
                                    try {
                                        if(checkLastTime(documentSnapshot,timeStamp)){
                                            builder.setMessage("This vehicle was recorded less than 5 mins ago.Do you want to add the vehicle?")
                                                    .setTitle("Note")
                                                    .setCancelable(false)
                                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            updateVehicle(documentSnapshot, incident);
                                                        }
                                                    })
                                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            if(regNo.getText() != null){
                                                                regNo.getText().clear();
                                                            }
                                                            if(remarks.getText() != null){
                                                                remarks.getText().clear();
                                                            }
                                                            if (name.getText() != null){
                                                                name.getText().clear();
                                                            }
                                                            if (locationAddress.getText().toString().trim().length() != 0){
                                                                locationAddress.getText().clear();
                                                            }
                                                            lottieAnimationView.setVisibility(View.GONE);
                                                            Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                                                            dialogInterface.cancel();
                                                        }
                                                    });
                                            AlertDialog alert = builder.create();
                                            alert.show();
                                        }else{
                                            updateVehicle(documentSnapshot, incident);
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                }else{
                                    db.collection("vehicles").document(registrationNo).set(vehicle).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            lottieAnimationView.setVisibility(View.GONE);
                                            Toast.makeText(getContext(), "Successfully Recorded", Toast.LENGTH_SHORT).show();
                                            TabLayout tabhost = (TabLayout) getActivity().findViewById(R.id.tabs);
                                            tabhost.getTabAt(0).select();
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            }
        });

        return view;
    }

    private boolean checkLastTime(DocumentSnapshot documentSnapshot, String timeStamp) throws ParseException {
        System.out.println("Last Time Called");
        Vehicle v = documentSnapshot.toObject(Vehicle.class);
        List<Incident> incidents = v.getIncidents();
        String format = "dd/MM/yy hh:mm:ssa";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        Date d1 = sdf.parse(incidents.get(0).getTimeStamp());
        Date d2 = sdf.parse(timeStamp);

        long difference = d2.getTime() - d1.getTime();
        int days = (int) (difference / (1000*60*60*24));
        int hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
        int min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);
        System.out.println("Diff"+days+" "+hours+" "+min);
        if ((days == 0) && (hours == 0) && (min <= 5)){
            return true;
        }
        return false;
    }


    private void updateVehicle(DocumentSnapshot documentSnapshot, Incident incident) {
        Vehicle updateVehicle = documentSnapshot.toObject(Vehicle.class);
        List<Incident> listOfIncidents = updateVehicle.getIncidents();
        listOfIncidents.add(0,incident);
        int offenses = updateVehicle.getOffenses()+1;
        Vehicle updated = new Vehicle(registrationNo,vehicleTypeText,offenses,listOfIncidents);
        db.collection("vehicles").document(registrationNo).set(updated).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                lottieAnimationView.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Successfully Recorded", Toast.LENGTH_SHORT).show();
                TabLayout tabhost = (TabLayout) getActivity().findViewById(R.id.tabs);
                tabhost.getTabAt(0).select();
            }
        });
    }

    private boolean checkPermissions(){
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    private void requestPermissions(){
        ActivityCompat.requestPermissions(
                this.getActivity(),
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }
        }
    }

    private boolean isLocationEnabled(){
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    latitude = location.getLatitude()+"";
                                    longitude = location.getLongitude()+"";
                                    lat.setText(latitude);
                                    lon.setText(longitude);
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this.getContext(), "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getActivity());
        fusedLocationProviderClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latitude = mLastLocation.getLatitude()+"";
            longitude = mLastLocation.getLongitude()+"";
            lat.setText(latitude);
            lon.setText(longitude);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if(regNo.getText() != null){
            regNo.getText().clear();
        }
        if(remarks.getText() != null){
            remarks.getText().clear();
        }
        if (name.getText() != null){
            name.getText().clear();
        }
        if (locationAddress.getText().toString().trim().length() != 0){
            locationAddress.getText().clear();
        }
        lat.setText("");
        lon.setText("");
        getLastLocation();

    }
}
