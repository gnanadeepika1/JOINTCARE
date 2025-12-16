package com.example.myjoints;

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

public class InvestigationAdapter extends RecyclerView.Adapter<InvestigationAdapter.VH> {

    private final List<Investigation> items;
    private final Context ctx;

    public InvestigationAdapter(Context ctx, List<Investigation> items) {
        this.ctx = ctx;
        this.items = items;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_investigation, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Investigation inv = items.get(position);
        holder.title.setText(inv.getTitle());

        // Clear previous views (safeguard due to recycling)
        holder.llDetails.removeAllViews();

        for (String d : inv.getDetails()) {
            // horizontal row: dot + text
            LinearLayout row = new LinearLayout(ctx);
            row.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rowParams.topMargin = dpToPx(6);
            row.setLayoutParams(rowParams);

            View dot = new View(ctx);
            LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(dpToPx(8), dpToPx(8));
            dotParams.leftMargin = dpToPx(2);
            dotParams.topMargin = dpToPx(4);
            dot.setLayoutParams(dotParams);
            dot.setBackgroundResource(R.drawable.circle_blue);

            TextView tv = new TextView(ctx);
            LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tvParams.leftMargin = dpToPx(10);
            tv.setLayoutParams(tvParams);
            tv.setText(d);
            tv.setTextSize(15f);
            tv.setTextColor(0xFF000000);

            row.addView(dot);
            row.addView(tv);

            holder.llDetails.addView(row);
        }
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
        LinearLayout llDetails;
        VH(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.ic_inv);
            title = itemView.findViewById(R.id.tvInvestigationTitle);
            llDetails = itemView.findViewById(R.id.llDetails);
        }
    }

    private int dpToPx(int dp) {
        float density = ctx.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
