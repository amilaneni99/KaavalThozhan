package com.abhinav.kaavalthozhan.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abhinav.kaavalthozhan.R;
import com.abhinav.kaavalthozhan.model.Vehicle;

import java.util.List;

public class FrequencyAdapter extends RecyclerView.Adapter<FrequencyAdapter.MyViewHolder> {

    private List<Vehicle> vehicles;
    private Context mCtx;

    public FrequencyAdapter(List<Vehicle> incidents, Context mCtx) {
        this.vehicles = incidents;
        this.mCtx = mCtx;
    }

    @NonNull
    @Override
    public FrequencyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_recycler, parent, false);
        return new FrequencyAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FrequencyAdapter.MyViewHolder holder, int position) {
        Vehicle vehicle = vehicles.get(position);
        holder.timeStamp.setText(vehicle.getIncidents().get(0).getTimeStamp());
        holder.regNo.setText(vehicle.getRegistrationNumber());
        holder.type.setText(vehicle.getType());
        holder.offenses.setText(String.valueOf(vehicle.getOffenses()));
        holder.copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) mCtx.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("registration", holder.regNo.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(mCtx, "Registration Number Copied", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return vehicles.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView timeStamp, offenses, type, regNo;
        ImageView copy;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            timeStamp = itemView.findViewById(R.id.timeStampFrequent);
            offenses = itemView.findViewById(R.id.noof);
            type = itemView.findViewById(R.id.type);
            regNo = itemView.findViewById(R.id.regNo);
            copy = itemView.findViewById(R.id.copy);
        }
    }
}
