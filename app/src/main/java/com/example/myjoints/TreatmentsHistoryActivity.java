package com.example.myjoints;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TreatmentsHistoryActivity extends AppCompatActivity {

    private static final String TAG = "TreatmentsHistory";
    private static final String PREFS_NAME = "patient_prefs";
    private static final String KEY_PATIENT_ID = "patient_id";

    private static final String BASE_URL = "http://10.217.226.180/jointcare/";
    private static final String ADD_TREATMENT_URL = BASE_URL + "add_treatment.php";
    private static final String GET_TREATMENTS_URL = BASE_URL + "get_treatments.php";

    private ImageView backBtn;
    private TextView tvPatientId;
    private RecyclerView rvTreatments;
    private FloatingActionButton fabAddTreatment;

    private final List<TreatmentRecord> treatmentList = new ArrayList<>();
    private TreatmentsHistoryAdapter adapter;
    private String patientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatments_history);

        backBtn = findViewById(R.id.back_btn);
        tvPatientId = findViewById(R.id.tvPatientId);
        rvTreatments = findViewById(R.id.rvTreatments);
        fabAddTreatment = findViewById(R.id.fabAddTreatment);

        backBtn.setOnClickListener(v -> onBackPressed());

        Intent intent = getIntent();
        if (intent != null) {
            patientId = intent.getStringExtra("patient_id");
        }

        if (TextUtils.isEmpty(patientId)) {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            patientId = prefs.getString(KEY_PATIENT_ID, null);
        }

        if (TextUtils.isEmpty(patientId)) {
            patientId = "P0001";
            Toast.makeText(this, "Using test patient P0001", Toast.LENGTH_SHORT).show();
        }

        tvPatientId.setText("Patient ID: " + patientId);

        rvTreatments.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TreatmentsHistoryAdapter(treatmentList);
        rvTreatments.setAdapter(adapter);

        loadTreatmentsFromServer();
        fabAddTreatment.setOnClickListener(v -> showAddTreatmentDialog());
    }

    private void showAddTreatmentDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_treatment, null);

        EditText etMedicationName = dialogView.findViewById(R.id.etMedicationName);
        EditText etDose = dialogView.findViewById(R.id.etDose);
        Spinner spinnerRoute = dialogView.findViewById(R.id.spinnerRoute);
        EditText etFreqNumber = dialogView.findViewById(R.id.etFrequencyNumber);
        EditText etFreqText = dialogView.findViewById(R.id.etFrequencyText);
        EditText etTimePeriodWeeks = dialogView.findViewById(R.id.etTimePeriodWeeks);

        ArrayAdapter<String> routeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Tablet", "Injection", "Infusion", "Other"}
        );
        routeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoute.setAdapter(routeAdapter);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add Treatment")
                .setView(dialogView)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Add", null)
                .create();

        dialog.setOnShowListener(d -> {
            Button btnAdd = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btnAdd.setOnClickListener(v -> {
                String medName = etMedicationName.getText().toString().trim();
                String dose = etDose.getText().toString().trim();
                String route = spinnerRoute.getSelectedItem().toString();
                String freqNum = etFreqNumber.getText().toString().trim();
                String freqText = etFreqText.getText().toString().trim();
                String weeks = etTimePeriodWeeks.getText().toString().trim();

                if (TextUtils.isEmpty(medName)) {
                    etMedicationName.setError("Required");
                    return;
                }

                addTreatmentToServer(medName, dose, route, freqNum, freqText, weeks);
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private void addTreatmentToServer(String medName, String dose, String route,
                                      String freqNum, String freqText, String weeks) {

        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(ADD_TREATMENT_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                JSONObject body = new JSONObject();
                body.put("patient_id", patientId);
                body.put("medication_name", medName);
                body.put("dose", dose);
                body.put("route", route);
                body.put("frequency_number", freqNum);
                body.put("frequency_text", freqText);
                body.put("time_period_weeks", weeks); // ✅ FIXED

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes("UTF-8"));
                os.close();

                int code = conn.getResponseCode();
                InputStream is = (code < 400) ? conn.getInputStream() : conn.getErrorStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();

                JSONObject response = new JSONObject(sb.toString());

                runOnUiThread(() -> {
                    if (response.optBoolean("success")) {
                        Toast.makeText(this, "Treatment added", Toast.LENGTH_SHORT).show();
                        loadTreatmentsFromServer();
                    } else {
                        Toast.makeText(this,
                                response.optString("message", "Failed"),
                                Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Add treatment error", e);
                runOnUiThread(() ->
                        Toast.makeText(this, "Error adding treatment", Toast.LENGTH_LONG).show());
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    private void loadTreatmentsFromServer() {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(GET_TREATMENTS_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                JSONObject body = new JSONObject();
                body.put("patient_id", patientId);

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes("UTF-8"));
                os.close();

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();

                JSONObject response = new JSONObject(sb.toString());
                if (response.optBoolean("success")) {
                    JSONArray arr = response.optJSONArray("treatments");
                    treatmentList.clear();

                    if (arr != null) {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            treatmentList.add(new TreatmentRecord(
                                    obj.optString("frequency_number"),
                                    obj.optString("route"),
                                    obj.optString("dose"),
                                    obj.optString("time_period_weeks") + " weeks",
                                    patientId
                            ));
                        }
                    }
                    runOnUiThread(() -> adapter.notifyDataSetChanged());
                }

            } catch (Exception e) {
                Log.e(TAG, "Load treatments error", e);
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }
}
