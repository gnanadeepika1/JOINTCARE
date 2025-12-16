package com.example.myjoints;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class DiseaseScoresActivity extends AppCompatActivity {

    private ImageView backBtn;
    private FloatingActionButton fabAddScore;
    private LinearLayout containerScores;
    private LineChart sdaiChart, dasChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_disease_scores);

        WindowInsetsControllerCompat controller =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());

        controller.setAppearanceLightStatusBars(true);
        View rootView = findViewById(R.id.main);

        ViewCompat.setOnApplyWindowInsetsListener(rootView, (view, insets) -> {
            Insets systemBars =
                    insets.getInsets(WindowInsetsCompat.Type.systemBars());

            view.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
            );
            return insets;
        });

        backBtn = findViewById(R.id.back_btn);
        fabAddScore = findViewById(R.id.fabAddScore);
        containerScores = findViewById(R.id.containerScores);
        sdaiChart = findViewById(R.id.sdaiChart);
        dasChart = findViewById(R.id.dasChart);

        backBtn.setOnClickListener(v -> onBackPressed());

        // ✅ CORRECT & SAFE NAVIGATION
        fabAddScore.setOnClickListener(v -> {
            Intent intent = new Intent(
                    DiseaseScoresActivity.this,
                    TenderJointsActivity.class
            );
            startActivity(intent);
        });

        List<DiseaseScoreItem> items = new ArrayList<>();
        items.add(new DiseaseScoreItem("79.3", "4.1403", "2025-11-10"));
        items.add(new DiseaseScoreItem("83.9", "4.2534", "2025-11-11"));
        items.add(new DiseaseScoreItem("88.2", "4.5000", "2025-11-12"));

        setupSdaiChart(items);
        setupDasChart(items);
        loadHistoryCards(items);
    }

    private void setupSdaiChart(List<DiseaseScoreItem> items) {
        List<Entry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            float v = Float.parseFloat(items.get(i).sdai);
            entries.add(new Entry(i, v));
            colors.add(v > 26 ? Color.RED : Color.GREEN);
        }

        LineDataSet ds = new LineDataSet(entries, "SDAI");
        ds.setCircleColors(colors);
        sdaiChart.setData(new LineData(ds));
        sdaiChart.invalidate();
    }

    private void setupDasChart(List<DiseaseScoreItem> items) {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            entries.add(new Entry(i, Float.parseFloat(items.get(i).das28)));
        }

        LineDataSet ds = new LineDataSet(entries, "DAS28");
        dasChart.setData(new LineData(ds));
        dasChart.invalidate();
    }

    private void loadHistoryCards(List<DiseaseScoreItem> items) {
        LayoutInflater inflater = LayoutInflater.from(this);
        for (DiseaseScoreItem item : items) {
            View card = inflater.inflate(
                    R.layout.item_disease_score,
                    containerScores,
                    false
            );
            containerScores.addView(card);
        }
    }

    static class DiseaseScoreItem {
        final String sdai, das28, date;
        DiseaseScoreItem(String s, String d, String dt) {
            sdai = s;
            das28 = d;
            date = dt;
        }
    }
}
