package com.saveetha.myjoints;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReferralAdapter extends RecyclerView.Adapter<ReferralAdapter.ReferralViewHolder> {

    private final List<ReferralItem> data;

    public ReferralAdapter(List<ReferralItem> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ReferralViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_referral_card, parent, false);
        return new ReferralViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReferralViewHolder holder, int position) {
        ReferralItem item = data.get(position);
        holder.tvReferralMessage.setText(item.getMessage());
        holder.tvReferralPatientId.setText("Patient ID: " + item.getPatientId());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ReferralViewHolder extends RecyclerView.ViewHolder {
        TextView tvReferralMessage;
        TextView tvReferralPatientId;

        ReferralViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReferralMessage = itemView.findViewById(R.id.tvReferralMessage);
            tvReferralPatientId = itemView.findViewById(R.id.tvReferralPatientId);
        }
    }
}
