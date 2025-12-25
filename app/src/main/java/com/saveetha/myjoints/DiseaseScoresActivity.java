package com.saveetha.myjoints;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.saveetha.myjoints.data.DiseaseScores;
import com.saveetha.myjoints.databinding.ActivityDiseaseScoresBinding;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.saveetha.myjoints.util.Static;
import com.saveetha.network.RetrofitClient;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiseaseScoresActivity extends AppCompatActivity {

    ActivityDiseaseScoresBinding binding;
    private static final String PREFS_NAME    = "doctor_prefs";
    private static final String KEY_DOCTOR_ID = "doctor_id";
    private SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        binding = ActivityDiseaseScoresBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

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

        Intent intet = getIntent();
        String patientId = intet.getStringExtra("patient_id");
        String title = patientId + " - Disease Scores";
        binding.titleTV.setText(title);

        binding.fabAddScore.setOnClickListener(view -> {
            Intent intent = new Intent(this, TenderJointsActivity.class);
            intent.putExtra("patient_id", patientId);
            startActivity(intent);
        });

        getGraph(patientId);

    }

    private void getGraph(String patientID) {

        AlertDialog progress = Static.showProgress(this);
        progress.show();

        RetrofitClient.getService()
                .getGraph(patientID)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<DiseaseScores> call, Response<DiseaseScores> response) {
                        progress.dismiss();

                        if (response.isSuccessful()) {
                            if (response.body().getData()!=null && !response.body().getData().isEmpty()) {
                                List<DiseaseScores.Data> data = response.body().getData();
                                List<Entry> entries = getEntites(data);
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
                                List<Entry> secondEntries = getSecondEntries(data);
                                List<Integer> secondColors = getCircleColorsForSecondGraph(secondEntries);

                                setupFirstChart(chart2, secondEntries, secondColors);

                                DiseaseScoreAdapter adapter = new DiseaseScoreAdapter(response.body().getData());
                                binding.recyclerView.setLayoutManager(new LinearLayoutManager(DiseaseScoresActivity.this));
                                binding.recyclerView.setAdapter(adapter);
                            } else {
                                Static.toast(DiseaseScoresActivity.this, "No Patient Records Found");
                            }
                        } else {
                            Static.showErrorResponse(DiseaseScoresActivity.this, response.errorBody());
                        }
                    }

                    @Override
                    public void onFailure(Call<DiseaseScores> call, Throwable t) {
                        progress.dismiss();
                        Static.showError(DiseaseScoresActivity.this, t.getMessage());
                    }
                });
    }


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

    private List<Entry> getEntites(List<DiseaseScores.Data> data) {

        List<Entry> entries =  new ArrayList<>();
        for(int i = 0;i<data.size();i++) {
            entries.add(new Entry(i, data.get(i).getCrp()));
        }

        return entries;
    }

    private List<Entry> getSecondEntries(List<DiseaseScores.Data> data) {
        List<Entry> entries =  new ArrayList<>();
        for(int i = 0;i<data.size();i++) {
            entries.add(new Entry(i, data.get(i).getPga()));
        }

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
