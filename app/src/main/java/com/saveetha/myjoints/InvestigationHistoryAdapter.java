package com.saveetha.myjoints;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class InvestigationHistoryAdapter
        extends RecyclerView.Adapter<InvestigationHistoryAdapter.InvestigationViewHolder> {

    private final Context context;
    private final List<InvestigationItem> data;

    public InvestigationHistoryAdapter(Context context, List<InvestigationItem> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public InvestigationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_investigation_card, parent, false);
        return new InvestigationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvestigationViewHolder holder, int position) {
        InvestigationItem item = data.get(position);
        holder.tvTitle.setText(item.getTitle());

        holder.llDetails.removeAllViews();

        for (String line : item.getDetails()) {
            // row container
            LinearLayout row = new LinearLayout(context);
            row.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT
            );
            rowParams.topMargin = dpToPx(4);
            row.setLayoutParams(rowParams);

            // blue dot
            View dot = new View(context);
            int size = dpToPx(8);
            LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(size, size);
            dotParams.topMargin = dpToPx(6);
            dot.setLayoutParams(dotParams);
            dot.setBackgroundResource(R.drawable.circle_blue); // your existing blue circle

            // text
            TextView tv = new TextView(context);
            LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
            );
            tvParams.leftMargin = dpToPx(10);
            tv.setLayoutParams(tvParams);
            tv.setText(line);
            tv.setTextSize(15f);
            tv.setTextColor(0xFF000000); // black

            row.addView(dot);
            row.addView(tv);

            holder.llDetails.addView(row);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class InvestigationViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        LinearLayout llDetails;

        InvestigationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvInvestigationTitle);
            llDetails = itemView.findViewById(R.id.llDetails);
        }
    }

    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
