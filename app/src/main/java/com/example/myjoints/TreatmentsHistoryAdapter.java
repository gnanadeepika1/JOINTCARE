package com.example.myjoints;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TreatmentsHistoryAdapter
        extends RecyclerView.Adapter<TreatmentsHistoryAdapter.TreatmentViewHolder> {

    private final List<TreatmentRecord> items;

    public TreatmentsHistoryAdapter(List<TreatmentRecord> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public TreatmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_treatment_card, parent, false);
        return new TreatmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TreatmentViewHolder holder, int position) {
        TreatmentRecord record = items.get(position);

        holder.tvDose.setText("Dose: " + record.getDose());
        holder.tvRoute.setText("Route: " + record.getRoute());
        holder.tvFrequency.setText("Frequency: " + record.getFrequency());
        holder.tvDuration.setText("Duration: " + record.getDuration());
        holder.tvPatientIdBottom.setText("Patient ID: " + record.getPatientId());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class TreatmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvDose, tvRoute, tvFrequency, tvDuration, tvPatientIdBottom;

        TreatmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDose = itemView.findViewById(R.id.tvDose);
            tvRoute = itemView.findViewById(R.id.tvRoute);
            tvFrequency = itemView.findViewById(R.id.tvFrequency);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvPatientIdBottom = itemView.findViewById(R.id.tvPatientIdBottom);
        }
    }
}
