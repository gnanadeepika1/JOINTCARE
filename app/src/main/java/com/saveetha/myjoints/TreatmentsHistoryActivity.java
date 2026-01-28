package com.saveetha.myjoints;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TreatmentsHistoryActivity extends AppCompatActivity {

    private static final String TAG = "TREAT_DEBUG";

    // ⚠️ USE YOUR REAL BACKEND URL
    private static final String BASE_URL =
            "https://3cxr1p7f-80.inc1.devtunnels.ms/jointcare/";

    private static final String ADD_URL = BASE_URL + "add_treatment.php";
    private static final String GET_URL = BASE_URL + "get_treatments.php";

    private TextView tvPatientId;
    private RecyclerView rvTreatments;
    private FloatingActionButton fabAddTreatment;

    private final List<TreatmentRecord> items = new ArrayList<>();
    private TreatmentsHistoryAdapter adapter;
    private String patientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatments_history);

        tvPatientId = findViewById(R.id.tvPatientId);
        rvTreatments = findViewById(R.id.rvTreatments);
        fabAddTreatment = findViewById(R.id.fabAddTreatment);

        patientId = getIntent().getStringExtra("patient_id");

        if (TextUtils.isEmpty(patientId)) {
            Toast.makeText(this, "No patient ID", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        patientId = patientId.toLowerCase().trim();

        tvPatientId.setText("Patient ID: " + patientId);

        adapter = new TreatmentsHistoryAdapter(items);
        rvTreatments.setLayoutManager(new LinearLayoutManager(this));
        rvTreatments.setAdapter(adapter);

        loadTreatmentsFromServer();

        fabAddTreatment.setOnClickListener(v -> showAddTreatmentDialog());
    }

    // ================= ADD DIALOG =================
    private void showAddTreatmentDialog() {

        View v = getLayoutInflater().inflate(R.layout.dialog_add_treatment, null);

        EditText etName = v.findViewById(R.id.etMedicationName);
        EditText etDose = v.findViewById(R.id.etDose);
        Spinner spinnerRoute = v.findViewById(R.id.spinnerRoute);
        EditText etFreqNum = v.findViewById(R.id.etFrequencyNumber);
        EditText etFreqText = v.findViewById(R.id.etFrequencyText);
        EditText etDuration = v.findViewById(R.id.etTimePeriodWeeks);

        String[] routes = {"Tablet", "Injection", "Infusion", "Other"};
        spinnerRoute.setAdapter(
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item, routes)
        );

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add Treatment")
                .setView(v)
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d -> {
            Button btn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btn.setOnClickListener(x -> {

                String name = etName.getText().toString().trim();
                if (name.isEmpty()) {
                    etName.setError("Required");
                    return;
                }

                addTreatmentToServer(
                        name,
                        etDose.getText().toString().trim(),
                        spinnerRoute.getSelectedItem().toString(),
                        etFreqNum.getText().toString().trim(),
                        etFreqText.getText().toString().trim(),
                        etDuration.getText().toString().trim()
                );

                dialog.dismiss();
            });
        });

        dialog.show();
    }

    // ================= ADD =================
    private void addTreatmentToServer(
            String name,
            String dose,
            String route,
            String freqNum,
            String freqText,
            String duration
    ) {
        new Thread(() -> {
            try {
                URL url = new URL(ADD_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject body = new JSONObject();
                body.put("patient_id", patientId);
                body.put("name", name);
                body.put("dose", dose);
                body.put("route", route);
                body.put("frequency_number", freqNum);
                body.put("frequency_text", freqText);
                body.put("duration", duration);

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes());
                os.close();

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);

                Log.d(TAG, "ADD RESPONSE: " + sb);

                runOnUiThread(() -> {
                    Toast.makeText(this,
                            "Treatment added", Toast.LENGTH_SHORT).show();
                    loadTreatmentsFromServer();
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this,
                            "Error "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    loadTreatmentsFromServer();
                });
            }
        }).start();
    }

    // ================= GET =================
    private void loadTreatmentsFromServer() {
        new Thread(() -> {
            try {
                URL url = new URL(GET_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject body = new JSONObject();
                body.put("patient_id", patientId);

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes());
                os.close();

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);

                Log.d(TAG, "GET RESPONSE: " + sb);

                JSONObject json = new JSONObject(sb.toString());
                JSONArray arr = json.getJSONArray("treatments");

                items.clear();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject t = arr.getJSONObject(i);
                    items.add(new TreatmentRecord(
                            t.getString("name"),
                            t.getString("dose"),
                            t.getString("route"),
                            t.getString("frequency_number"),
                            t.getString("frequency_text"),
                            t.getString("duration"),
                            patientId
                    ));
                }

                runOnUiThread(() -> adapter.notifyDataSetChanged());

            } catch (Exception e) {
                Log.e(TAG, "GET ERROR", e);
            }
        }).start();
    }
}
