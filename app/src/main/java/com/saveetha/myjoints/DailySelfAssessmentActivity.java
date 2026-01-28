package com.saveetha.myjoints;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.button.MaterialButton;
import com.saveetha.myjoints.adapters.activity_disease_scores.DiseaseScoreAdapter;
import com.saveetha.myjoints.data.DiseaseScores;
import com.saveetha.myjoints.databinding.ActivityDailyPainBinding;
import com.saveetha.myjoints.util.Static;
import com.saveetha.network.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DailySelfAssessmentActivity extends AppCompatActivity {

    private SeekBar seekPain;
    private MaterialButton btnSave;
    private LineChart lineChart;
    private ImageView backBtn;

    PainEntryAdapter adapter;
    private final List<PainEntry> entries = new ArrayList<>();

    private ActivityDailyPainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDailyPainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());  // Keep SAME layout

        seekPain = findViewById(R.id.seekPain);
        btnSave = findViewById(R.id.btnSave);
        lineChart = findViewById(R.id.lineChart);
        backBtn = findViewById(R.id.back_btn);

        backBtn.setOnClickListener(v -> onBackPressed());

        Intent intent = getIntent();
        String patientId = intent.getStringExtra("patient_id");

        btnSave.setOnClickListener(v -> {
            int value = seekPain.getProgress();
            saveValue(patientId, value);
        });

        getGraph(patientId);
    }

    private void getGraph(String patientID) {
        AlertDialog progress = Static.showProgress(this);
        progress.show();

        RetrofitClient.getService()
                .getPainValues(patientID)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<PainResponse> call,
                                           Response<PainResponse> response) {
                        progress.dismiss();

                        if (response.isSuccessful()) {
                            if (response.body().getData() != null
                                    && !response.body().getData().isEmpty()) {

                                List<PainEntry> data = response.body().getData();
                                entries.addAll(data);
                                setupChart();

                                adapter = new PainEntryAdapter(data);
                                binding.rvEntries.setLayoutManager(
                                        new LinearLayoutManager(
                                                DailySelfAssessmentActivity.this));
                                binding.rvEntries.setAdapter(adapter);

                            } else {
                                Static.toast(
                                        DailySelfAssessmentActivity.this,
                                        "No Patient Records Found");
                            }
                        } else {
                            Static.showErrorResponse(
                                    DailySelfAssessmentActivity.this,
                                    response.errorBody());
                        }
                    }

                    @Override
                    public void onFailure(Call<PainResponse> call, Throwable t) {
                        progress.dismiss();
                        Static.showError(
                                DailySelfAssessmentActivity.this,
                                t.getMessage());
                    }
                });
    }

    private void saveValue(String id, int value1) {
        Map<String, Object> request = new HashMap<>();
        request.put("user_id", id);
        request.put("pain_value", value1);

        AlertDialog progress = Static.showProgress(this);
        progress.show();

        RetrofitClient.getService()
                .saveDailyPainValue(request)
                .enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call,
                                           Response<Map<String, Object>> response) {
                        progress.dismiss();

                        if (response.isSuccessful()) {
                            String iso = nowIso();
                            entries.add(0, new PainEntry(value1, iso));

                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            }

                            updateChart();
                            Static.showResponse(
                                    DailySelfAssessmentActivity.this,
                                    response.body().get("message").toString());
                        } else {
                            Static.showErrorResponse(
                                    DailySelfAssessmentActivity.this,
                                    response.errorBody());
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        progress.dismiss();
                        Static.showError(
                                DailySelfAssessmentActivity.this,
                                t.getMessage());
                    }
                });
    }

    private void setupChart() {
        lineChart.setTouchEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setDrawGridBackground(false);

        Description desc = new Description();
        desc.setText("");
        lineChart.setDescription(desc);

        Legend l = lineChart.getLegend();
        l.setEnabled(false);

        XAxis x = lineChart.getXAxis();
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setDrawGridLines(false);
        x.setGranularity(1f);
        x.setLabelRotationAngle(-45f);

        updateChart();
    }

    private void updateChart() {
        List<Entry> chartEntries = new ArrayList<>();
        int N = Math.min(entries.size(), 12);

        if (N == 0) {
            chartEntries.add(new Entry(0f, 0f));
        } else {
            for (int i = N - 1; i >= 0; i--) {
                PainEntry p = entries.get(i);
                int index = (N - 1) - i;
                chartEntries.add(new Entry(index, p.getValue()));
            }
        }

        LineDataSet set = new LineDataSet(chartEntries, "Pain");
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(false);
        set.setLineWidth(2.2f);
        set.setColor(0xFFE85C7B);
        set.setDrawFilled(true);
        set.setFillColor(0xFFF8D8E5);
        set.setFillAlpha(200);
        set.setCircleColor(0xFFE85C7B);
        set.setCircleRadius(4f);
        set.setDrawCircles(true);

        LineData data = new LineData(set);
        lineChart.setData(data);
        lineChart.invalidate();
    }

    private String nowIso() {
        SimpleDateFormat sdf =
                new SimpleDateFormat(
                        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }
}
