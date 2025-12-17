package com.saveetha.myjoints;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.VH> {

    private final List<Medication> items;
    private final Context ctx;

    public MedicationAdapter(Context ctx, List<Medication> items) {
        this.ctx = ctx;
        this.items = items;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medication, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Medication m = items.get(position);
        holder.title.setText(m.getTitle());

        // clear previous detail views (recycling safety)
        holder.llDetails.removeAllViews();

        // create a row for each detail: blue dot + text
        for (String detail : m.getItems()) {
            LinearLayout row = new LinearLayout(ctx);
            row.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rowParams.topMargin = dpToPx(6);
            row.setLayoutParams(rowParams);

            View dot = new View(ctx);
            LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(dpToPx(8), dpToPx(8));
            dotParams.leftMargin = dpToPx(2);
            dotParams.topMargin = dpToPx(6);
            dot.setLayoutParams(dotParams);
            dot.setBackgroundResource(android.R.drawable.presence_online); // fallback shape
            // Better: set a circular colored background programmatically
            dot.setBackgroundColor(0xFF2B86F0); // blue

            TextView tv = new TextView(ctx);
            LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tvParams.leftMargin = dpToPx(10);
            tv.setLayoutParams(tvParams);
            tv.setText(detail);
            tv.setTextSize(15f);
            tv.setTextColor(0xFF000000);

            row.addView(dot);
            row.addView(tv);

            holder.llDetails.addView(row);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
        LinearLayout llDetails;
        VH(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.ic_med);
            title = itemView.findViewById(R.id.tvPrescriptionTitle);
            llDetails = itemView.findViewById(R.id.llDetails);
        }
    }

    private int dpToPx(int dp) {
        float density = ctx.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
