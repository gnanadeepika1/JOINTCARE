package com.saveetha.myjoints;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ComorbidityAdapter extends RecyclerView.Adapter<ComorbidityAdapter.VH> {

    private final List<Comorbidity> items;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Comorbidity item);
    }

    public ComorbidityAdapter(List<Comorbidity> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comorbidity, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Comorbidity c = items.get(position);

        // 🔸 Always show the caption "Comorbidity" on top
        holder.title.setText("Comorbidity");

        // 🖤 Description text from data (black color is set in XML)
        holder.detail.setText(c.getDetail());

        // Emoji/icon + its orange tint are handled in XML, no need to modify here

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(c);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title, detail;

        VH(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.ic_comorb);
            title = itemView.findViewById(R.id.tvComorbTitle);
            detail = itemView.findViewById(R.id.tvComorbDetail);
        }
    }
}
