package com.saveetha.myjoints.adapters.activity_disease_scores;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saveetha.myjoints.databinding.ItemDiseaseScoreBinding;

import java.util.List;

public class DiseaseScoreAdapter extends RecyclerView.Adapter<DiseaseScoreAdapter.ViewHolder> {

    private final List<DiseaseScore> list;

    public interface OnDeleteClickListener {
        void onDelete(int position);
    }

    public DiseaseScoreAdapter(List<DiseaseScore> list) {
        this.list = list;
    }

    public void removeItem(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDiseaseScoreBinding binding = ItemDiseaseScoreBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DiseaseScore item = list.get(position);

        holder.binding.tvSdai.setText("SDAI: " + item.getSdai());
        holder.binding.tvDas.setText("DAS28-CRP: " + item.getDas28Crp());
        holder.binding.tvDate.setText("Date: " + item.getDate());

        holder.binding.ivDelete.setOnClickListener(v -> {

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

