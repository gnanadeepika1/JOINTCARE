package com.saveetha.myjoints;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.saveetha.myjoints.adapters.activity_disease_scores.DiseaseScore;
import com.saveetha.myjoints.adapters.activity_disease_scores.DiseaseScoreAdapter;
import com.saveetha.myjoints.databinding.ActivityDiseaseScoresBinding;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

public class DiseaseScoresActivity extends AppCompatActivity {

    ActivityDiseaseScoresBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        binding = ActivityDiseaseScoresBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        WindowInsetsControllerCompat controller =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());

        controller.setAppearanceLightStatusBars(true);
        View rootView = binding.main;

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

        binding.fabAddScore.setOnClickListener(view -> {
            startActivity(new Intent(this, TenderJointsActivity.class));
        });

        List<Entry> entries = getEntites();
        List<Integer> colors = getCircleColors(entries);

        LineChart chart1 = binding.sdaiChart;
         getYaxis(
                chart1,
                0.0f,
                100.0f,
                6,
                createLimitLine(36.1f, "High"),
                createLimitLine( 18.0f, "Moderate"),
                createLimitLine( 10.0f, "Low")
        );
        setupFirstChart(chart1, entries, colors);

        LineChart chart2 = binding.dasChart;
        getYaxis(
                chart2,
                0.0f,
                10.0f,
                5,
                createLimitLine(3.4f, "High"),
                createLimitLine( 3.9f, "Moderate"),
                createLimitLine( 5.8f, "Low")
        );
        List<Entry> secondEntries = getSecondEntries();
        List<Integer> secondColors = getCircleColorsForSecondGraph(secondEntries);

        setupFirstChart(chart2, secondEntries, secondColors);

        List<DiseaseScore> list = new ArrayList<>();
        list.add(new DiseaseScore(
                21,
                3.027125898179314,
                "2025-09-27T05:23:30.533Z"
        ));list.add(new DiseaseScore(
                21,
                3.027125898179314,
                "2025-09-27T05:23:30.533Z"
        ));list.add(new DiseaseScore(
                21,
                3.027125898179314,
                "2025-09-27T05:23:30.533Z"
        ));

        DiseaseScoreAdapter adapter = new DiseaseScoreAdapter(list);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

    }

    /*private void setupSecondChart(ActivityDiseaseScoresBinding binding) {

        LineChart chart = binding.dasChart;

        // -------------------------
        // Chart base config
        // -------------------------
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(false);
        chart.setScaleEnabled(false);
        chart.setPinchZoom(false);
        chart.getLegend().setEnabled(false);

        // -------------------------
        // X Axis
        // -------------------------
        XAxis xAxis = chart.getXAxis();
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
//        xAxis.setTextColor(Color.GRAY);
        xAxis.setDrawLabels(false);

        // -------------------------
        // Y Axis (LEFT only)
        // -------------------------
        YAxis left = chart.getAxisLeft();
        left.setAxisMinimum(0f);
        left.setAxisMaximum(8f);
        left.setTextColor(Color.GRAY);
        left.setDrawGridLines(true);
        left.enableGridDashedLine(8f, 8f, 0f);

        chart.getAxisRight().setEnabled(false);

        // -------------------------
        // Reference Lines (High / Moderate / Low)
        // -------------------------
        left.removeAllLimitLines();

        left.addLimitLine(createLimitLine(5.1f, "High"));
        left.addLimitLine(createLimitLine(3.3f, "Moderate"));
        left.addLimitLine(createLimitLine(2.6f, "Low"));

        // -------------------------
        // Chart Data
        // -------------------------
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 3.0f));
        entries.add(new Entry(1, 2.4f));
        entries.add(new Entry(2, 4.4f));
        entries.add(new Entry(3, 4.5f));
        entries.add(new Entry(4, 4.7f));
        entries.add(new Entry(5, 2.6f));
        entries.add(new Entry(6, 3.0f));
        entries.add(new Entry(7, 4.2f));
        entries.add(new Entry(8, 4.3f));
        entries.add(new Entry(9, 4.2f));
        entries.add(new Entry(10, 4.1f));

        LineDataSet dataSet = new LineDataSet(entries, "");

        // -------------------------
        // Curved wave
        // -------------------------
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setCubicIntensity(0.18f);

        // -------------------------
        // Line style
        // -------------------------
        dataSet.setColor(Color.parseColor("#2E6DA4"));
        dataSet.setLineWidth(2.5f);
        dataSet.setDrawValues(false);

        // -------------------------
        // Gradient fill (important)
        // -------------------------
        dataSet.setDrawFilled(true);
        dataSet.setFillDrawable(
                ContextCompat.getDrawable(this, R.drawable.chart_gradient)
        );

        // -------------------------
        // Point circles (value based color)
        // -------------------------
        dataSet.setDrawCircles(true);
        dataSet.setCircleRadius(6f);
        dataSet.setCircleHoleRadius(3f);
        dataSet.setDrawCircleHole(true);

        dataSet.setCircleColors(getCircleColors(entries));

        // -------------------------
        // Set data
        // -------------------------
        chart.setData(new LineData(dataSet));
        chart.invalidate();
    }*/

    private void setupFirstChart(
            LineChart chart, List<Entry> entries,
            List<Integer> colorInts) {

//        LineChart chart = binding.sdaiChart;
        // -------------------------
        // Chart base config
        // -------------------------
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(false);
        chart.setScaleEnabled(false);
        chart.setPinchZoom(false);
        chart.getLegend().setEnabled(false);

        // -------------------------
        // X Axis
        // -------------------------
        XAxis xAxis = chart.getXAxis();
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
//        xAxis.setTextColor(Color.GRAY);
        xAxis.setDrawLabels(false);

        // -------------------------
        // Y Axis (LEFT only)
        // -------------------------

        chart.getAxisRight().setEnabled(false);
        // -------------------------
        // Chart Data
        // -------------------------

        LineDataSet dataSet = new LineDataSet(entries, "");

        // -------------------------
        // Curved wave
        // -------------------------
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setCubicIntensity(0.18f);

        // -------------------------
        // Line style
        // -------------------------
        dataSet.setColor(Color.parseColor("#2E6DA4"));
        dataSet.setLineWidth(4f);
        dataSet.setDrawValues(false);

        // -------------------------
        // Gradient fill (important)
        // -------------------------
        dataSet.setDrawFilled(true);
        dataSet.setFillDrawable(
                ContextCompat.getDrawable(this, R.drawable.chart_gradient)
        );

        // -------------------------
        // Point circles (value based color)
        // -------------------------
        dataSet.setDrawCircles(true);
        dataSet.setCircleRadius(6f);
        dataSet.setCircleHoleRadius(3f);
        dataSet.setDrawCircleHole(true);

        dataSet.setCircleColors(colorInts);

        // -------------------------
        // Set data
        // -------------------------
        chart.setData(new LineData(dataSet));
        chart.invalidate();
    }

    private YAxis getYaxis(LineChart chart, Float startY, Float endY, int lableCount, LimitLine... limitLine) {
        YAxis left = chart.getAxisLeft();
        left.setAxisMinimum(startY);
        left.setAxisMaximum(endY);
        // Force exact 5 labels
        left.setLabelCount(lableCount, true);
        left.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.US, "%.1f", value);
            }
        });
        left.setTextColor(Color.GRAY);
        left.setDrawGridLines(true);
        left.enableGridDashedLine(8f, 8f, 0f);


        // -------------------------
        // Reference Lines (High / Moderate / Low)
        // -------------------------
        left.removeAllLimitLines();

        for(int i = 0;i < limitLine.length; i++ ) {
            left.addLimitLine(limitLine[i]);
        }


        return left;
    }

    private List<Entry> getEntites() {
        List<Entry> entries =  new ArrayList<>();
        entries.add(new Entry(0, 38.0f));
        entries.add(new Entry(1, 14.4f));
        entries.add(new Entry(2, 40.4f));
        entries.add(new Entry(3, 40.5f));
        entries.add(new Entry(4, 64.7f));
        entries.add(new Entry(5, 20.6f));
        entries.add(new Entry(6, 21.0f));
        entries.add(new Entry(7, 82.2f));
        entries.add(new Entry(8, 81.4f));
        entries.add(new Entry(9, 81.0f));
        entries.add(new Entry(10, 80.0f));

        return entries;
    }

    private List<Entry> getSecondEntries() {
        List<Entry> entries =  new ArrayList<>();
        entries.add(new Entry(0, 3.4f));
        entries.add(new Entry(1, 2.3f));
        entries.add(new Entry(2, 4.2f));
        entries.add(new Entry(3, 4.1f));
        entries.add(new Entry(4, 2.6f));
        entries.add(new Entry(6, 3.0f));
        entries.add(new Entry(7, 4.0f));
        entries.add(new Entry(8, 3.9f));
        entries.add(new Entry(9, 3.8f));
        entries.add(new Entry(10, 3.7f));

        return entries;
    }

    private LimitLine createLimitLine(float value, String label) {
        LimitLine line = new LimitLine(value, label);
        line.setLineWidth(1f);
        line.enableDashedLine(10f, 10f, 0f);
        line.setTextColor(Color.GRAY);
        line.setLineColor(Color.GRAY);
        line.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        line.setTextSize(10f);
        return line;
    }

    // First graph Dynamic circle colors
    private List<Integer> getCircleColors(List<Entry> entries) {
        List<Integer> colors = new ArrayList<>();

        for (Entry e : entries) {
            float v = e.getY();

            if (v <= 10.0f) {
                colors.add(Color.parseColor("#4CAF50")); // Green (Remission)
            } else if (v <= 15.0f) {
                colors.add(Color.parseColor("#2196F3")); // Blue (Low)
            } else if (v <= 25.0f) {
                colors.add(Color.parseColor("#FFA000")); // Orange (Moderate)
            } else {
                colors.add(Color.parseColor("#F44336")); // Red (High)
            }
        }
        return colors;
    }

    // Second graph Dynamic circle colors
    private List<Integer> getCircleColorsForSecondGraph(List<Entry> entries) {
        List<Integer> colors = new ArrayList<>();

        for (Entry e : entries) {
            float v = e.getY();
            if (v <= 2.3f) {
                colors.add(Color.parseColor("#4CAF50")); // Green (Remission)
            } else if (v <= 3.0f) {
                colors.add(Color.parseColor("#2196F3")); // Blue (Low)
            } else if (v <= 5.0f) {
                colors.add(Color.parseColor("#FFA000")); // Orange (Moderate)
            } else {
                colors.add(Color.parseColor("#F44336")); // Red (High)
            }
        }
        return colors;
    }

}
