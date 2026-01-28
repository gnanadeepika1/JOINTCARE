package com.saveetha.myjoints.adapters.activity_disease_scores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saveetha.myjoints.data.DiseaseScores;
import com.saveetha.myjoints.databinding.ItemDiseaseScoreBinding;

import java.util.List;

public class DiseaseScoreAdapter
        extends RecyclerView.Adapter<DiseaseScoreAdapter.ViewHolder> {

    private final List<DiseaseScores.Data> list;

    public interface OnDeleteClickListener {
        void onDelete(int position);
    }

    public DiseaseScoreAdapter(List<DiseaseScores.Data> list) {
        this.list = list;
    }

    public void removeItem(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDiseaseScoreBinding binding =
                ItemDiseaseScoreBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DiseaseScores.Data item = list.get(position);

        holder.binding.ivDelete.setVisibility(View.INVISIBLE);

        // ✅ CORRECT VALUES (FIXED)
        holder.binding.tvSdai.setText("SDAI: " + item.getSdai());
        holder.binding.tvDas.setText("DAS28-CRP: " + item.getDas28_crp());

        holder.binding.tvDate.setText("Date: " + item.getCreated_at());

        holder.binding.ivDelete.setOnClickListener(v -> {
            // delete handled elsewhere
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemDiseaseScoreBinding binding;

        ViewHolder(ItemDiseaseScoreBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
