package com.example.firstaidhero.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstaidhero.R;
import com.example.firstaidhero.models.Emergency;

import java.util.ArrayList;
import java.util.List;

public class EmergencyAdapter extends RecyclerView.Adapter<EmergencyAdapter.EmergencyViewHolder> {

    public interface OnEmergencyClickListener {
        void onEmergencyClick(Emergency emergency);
    }

    private List<Emergency> emergencies = new ArrayList<>();
    private final OnEmergencyClickListener listener;

    public EmergencyAdapter(OnEmergencyClickListener listener) {
        this.listener = listener;
    }

    public void setEmergencies(List<Emergency> emergencies) {
        this.emergencies = emergencies != null ? emergencies : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EmergencyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_emergency, parent, false);
        return new EmergencyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmergencyViewHolder holder, int position) {
        Emergency emergency = emergencies.get(position);
        holder.bind(emergency, listener);
    }

    @Override
    public int getItemCount() {
        return emergencies.size();
    }

    static class EmergencyViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleText;
        private final TextView dangerBadge;
        private final TextView symptomsText;

        public EmergencyViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.itemTitle);
            dangerBadge = itemView.findViewById(R.id.itemDangerBadge);
            symptomsText = itemView.findViewById(R.id.itemSymptoms);
        }

        public void bind(Emergency emergency, OnEmergencyClickListener listener) {
            titleText.setText(emergency.getTitle());
            dangerBadge.setText(emergency.getDangerLevel());
            symptomsText.setText(emergency.getSymptoms());

            // Color-code badge based on level
            if (emergency.isCritical()) {
                dangerBadge.setBackgroundResource(R.drawable.bg_danger_badge); // Red
            } else {
                dangerBadge.setBackgroundColor(0xFFFFA000); // Amber
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEmergencyClick(emergency);
                }
            });
        }
    }
}
