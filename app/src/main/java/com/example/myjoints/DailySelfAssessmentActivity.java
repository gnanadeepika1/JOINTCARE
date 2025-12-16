package com.example.myjoints;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class DailySelfAssessmentActivity extends AppCompatActivity {

    private SeekBar seekPain;
    private MaterialButton btnSave;
    private LineChart lineChart;
    private RecyclerView rvEntries;
    private ImageView backBtn;

    private final List<PainEntry> entries = new ArrayList<>();
    private PainEntryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_pain);  // Keep SAME layout

        seekPain = findViewById(R.id.seekPain);
        btnSave = findViewById(R.id.btnSave);
        lineChart = findViewById(R.id.lineChart);
        rvEntries = findViewById(R.id.rvEntries);
        backBtn = findViewById(R.id.back_btn);

        backBtn.setOnClickListener(v -> onBackPressed());

        adapter = new PainEntryAdapter(entries);
        rvEntries.setLayoutManager(new LinearLayoutManager(this));
        rvEntries.setAdapter(adapter);

        setupChart();
        addSampleData(); // optional

        btnSave.setOnClickListener(v -> {
            int value = seekPain.getProgress();
            String iso = nowIso();

            entries.add(0, new PainEntry(value, iso));
            adapter.notifyItemInserted(0);
            rvEntries.scrollToPosition(0);

            updateChart();
            Toast.makeText(this, "Saved pain: " + value, Toast.LENGTH_SHORT).show();
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
        set.setColor(0xFFE85C7B);      // Pink line
        set.setDrawFilled(true);
        set.setFillColor(0xFFF8D8E5);  // Light pink fill
        set.setFillAlpha(200);
        set.setCircleColor(0xFFE85C7B);
        set.setCircleRadius(4f);
        set.setDrawCircles(true);

        LineData data = new LineData(set);
        lineChart.setData(data);
        lineChart.invalidate();
    }

    private String nowIso() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }

    private void addSampleData() {
        entries.add(new PainEntry(3, nowIso()));
        entries.add(new PainEntry(5, nowIso()));
        entries.add(new PainEntry(5, nowIso()));
        entries.add(new PainEntry(6, nowIso()));
        entries.add(new PainEntry(5, nowIso()));

        adapter.notifyDataSetChanged();
        updateChart();
    }
}
