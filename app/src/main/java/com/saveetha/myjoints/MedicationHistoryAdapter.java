package com.saveetha.myjoints;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MedicationHistoryAdapter
        extends RecyclerView.Adapter<MedicationHistoryAdapter.MedViewHolder> {

    private final List<MedicationHistoryItem> items;

    public MedicationHistoryAdapter(List<MedicationHistoryItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public MedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medication_history, parent, false);
        return new MedViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MedViewHolder holder, int position) {
        MedicationHistoryItem item = items.get(position);

        holder.tvEmoji.setText("💊");          // emoji as requested
        holder.tvHeaderTitle.setText("Medications");

        holder.tvMedName.setText(item.getName());
        holder.tvDose.setText("Dose: " + item.getDose());
        holder.tvPeriod.setText("Period: " + item.getPeriod());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MedViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmoji, tvHeaderTitle, tvMedName, tvDose, tvPeriod;

        MedViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmoji = itemView.findViewById(R.id.tvEmoji);
            tvHeaderTitle = itemView.findViewById(R.id.tvHeaderTitle);
            tvMedName = itemView.findViewById(R.id.tvMedName);
            tvDose = itemView.findViewById(R.id.tvDose);
            tvPeriod = itemView.findViewById(R.id.tvPeriod);
        }
    }
}
