package com.abhinav.kaavalthozhan.adapters;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abhinav.kaavalthozhan.R;
import com.abhinav.kaavalthozhan.model.Incident;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class HistroyAdapter extends RecyclerView.Adapter<HistroyAdapter.MyViewHolder> {
    private List<Incident> incidents;
    private Context mCtx;

    public HistroyAdapter(List<Incident> historyDataItemList, Context mCtx) {
        this.incidents = historyDataItemList;
        this.mCtx = mCtx;
    }

    @NonNull
    @Override
    public HistroyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.offense_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistroyAdapter.MyViewHolder holder, int position) {
        final Incident incident = incidents.get(position);
        if (incident.getDriverName() == ""){
            holder.driverName.setText("Not Provided");
        }else{
            String temp1 = incident.getDriverName();
            String temp2 = temp1.substring(0,1)+temp1.substring(1).toLowerCase();
            holder.driverName.setText(temp2);
        }

        holder.timeStamp.setText(incident.getTimeStamp());
        Double lat = incident.getGeoPoint().getLatitude();
        Double lon = incident.getGeoPoint().getLongitude();
        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.CEILING);
        holder.coordinates.setText(df.format(lat)+", "+df.format(lon));
        holder.address.setText(incident.getLocation());
        holder.remarks.setText(incident.getRemarks());
    }

    @Override
    public int getItemCount() {
        return incidents.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView timeStamp, address, driverName, coordinates, remarks;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            timeStamp = itemView.findViewById(R.id.timeStampRec);
            driverName = itemView.findViewById(R.id.driverNameRec);
            address = itemView.findViewById(R.id.address);
            coordinates = itemView.findViewById(R.id.coordinates);
            remarks = itemView.findViewById(R.id.remarks);
        }
    }
}
