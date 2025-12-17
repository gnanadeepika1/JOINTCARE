package com.saveetha.myjoints;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PainEntryAdapter extends RecyclerView.Adapter<PainEntryAdapter.VH> {

    private final List<PainEntry> items;

    public PainEntryAdapter(List<PainEntry> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pain_entry, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        PainEntry p = items.get(position);
        holder.painValue.setText("Pain: " + p.getValue());
        holder.painDate.setText(p.getIsoDate());
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView painValue, painDate;
        VH(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icHeart);
            painValue = itemView.findViewById(R.id.tvPainValue);
            painDate = itemView.findViewById(R.id.tvPainDate);
        }
    }
}
