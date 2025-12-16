package com.example.myjoints;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.VH> {

    private final List<Complaint> items;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Complaint complaint);
    }

    public ComplaintAdapter(List<Complaint> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_complaint, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Complaint c = items.get(position);
        holder.title.setText(c.getTitle());
        holder.date.setText("Date: " + c.getDateIso());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(c);
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView title, date;
        VH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvComplaintTitle);
            date = itemView.findViewById(R.id.tvComplaintDate);
        }
    }
}
