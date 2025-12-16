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

public class TreatmentAdapter extends RecyclerView.Adapter<TreatmentAdapter.VH> {

    private final List<Treatment> items;
    private final Context ctx;

    public TreatmentAdapter(Context ctx, List<Treatment> items) {
        this.ctx = ctx;
        this.items = items;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_treatment, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Treatment t = items.get(position);
        holder.title.setText(t.getTitle());

        holder.llDetails.removeAllViews();

        for (String d : t.getDetails()) {
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
            icon = itemView.findViewById(R.id.ic_treat);
            title = itemView.findViewById(R.id.tvTreatmentTitle);
            llDetails = itemView.findViewById(R.id.llDetails);
        }
    }

    private int dpToPx(int dp) {
        float density = ctx.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
