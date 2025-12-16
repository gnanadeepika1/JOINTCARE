package com.example.myjoints;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DietAdapter extends RecyclerView.Adapter<DietAdapter.VH> {

    private final List<DietItem> items;
    private final Context ctx;

    public DietAdapter(Context ctx, List<DietItem> items) {
        this.ctx = ctx;
        this.items = items;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_diet, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        DietItem it = items.get(position);
        holder.emoji.setText(it.getEmoji());
        holder.title.setText(it.getTitle());

        holder.llBullets.removeAllViews();
        for (String b : it.getBullets()) {
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
            tv.setText(b);
            tv.setTextSize(15f);
            tv.setTextColor(0xFF000000);

            row.addView(dot);
            row.addView(tv);

            holder.llBullets.addView(row);
        }
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView emoji;
        TextView title;
        LinearLayout llBullets;
        VH(@NonNull View itemView) {
            super(itemView);
            emoji = itemView.findViewById(R.id.tvEmoji);
            title = itemView.findViewById(R.id.tvTitle);
            llBullets = itemView.findViewById(R.id.llBullets);
        }
    }

    private int dpToPx(int dp) {
        float density = ctx.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
