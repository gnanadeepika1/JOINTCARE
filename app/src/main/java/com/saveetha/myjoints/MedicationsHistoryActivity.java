package com.saveetha.myjoints;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.saveetha.myjoints.util.AiMedicationAdvisor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MedicationsHistoryActivity extends AppCompatActivity {

    private static final String TAG = "MED_DEBUG";

    private ImageView backBtn;
    private RecyclerView rvMedications;
    private TextView tvPatientId;
    private FloatingActionButton fabAddMedication;
    private TextView tvAiSuggestion;

    private final List<MedicationHistoryItem> items = new ArrayList<>();
    private MedicationHistoryAdapter adapter;

    private String patientId;

    private static final String BASE_URL =
            "https://3cxr1p7f-80.inc1.devtunnels.ms/jointcare/";
    private static final String GET_MEDICATIONS_URL =
            BASE_URL + "get_medications.php";
    private static final String ADD_MEDICATION_URL =
            BASE_URL + "add_medication.php";
    private static final String GET_GRAPH_URL =
            BASE_URL + "get_graph.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medications_history);

        backBtn = findViewById(R.id.back_btn);
        rvMedications = findViewById(R.id.rvMedications);
        tvPatientId = findViewById(R.id.tvPatientId);
        fabAddMedication = findViewById(R.id.fabAddMedication);
        tvAiSuggestion = findViewById(R.id.tvAiSuggestion);

        backBtn.setOnClickListener(v -> onBackPressed());

        Intent intent = getIntent();
        patientId = intent != null ? intent.getStringExtra("patient_id") : null;

        if (TextUtils.isEmpty(patientId)) {
            Toast.makeText(this, "No patient ID provided", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        patientId = patientId.toLowerCase().trim();

        if (!patientId.matches("^pat_\\d{4}$")) {
            Toast.makeText(this, "Invalid patient ID: " + patientId, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        tvPatientId.setText("Patient ID: " + patientId);
        Log.d(TAG, "Using patientId = " + patientId);

        adapter = new MedicationHistoryAdapter(items);
        rvMedications.setLayoutManager(new LinearLayoutManager(this));
        rvMedications.setAdapter(adapter);

        loadMedicationsFromServer();
        loadLatestScoresAndRunAI();

        fabAddMedication.setOnClickListener(v -> showAddMedicationDialog());
    }

    // =====================================================
    // ADD MEDICATION DIALOG
    // =====================================================
    private void showAddMedicationDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_add_medication, null);

        EditText etName = view.findViewById(R.id.etMedicationName);
        EditText etDose = view.findViewById(R.id.etDose);
        EditText etPeriod = view.findViewById(R.id.etPeriod);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Add", null)
                .create();

        dialog.setOnShowListener(d -> {
            Button btnAdd = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btnAdd.setOnClickListener(v -> {
                String name = etName.getText().toString().trim();
                String dose = etDose.getText().toString().trim();
                String period = etPeriod.getText().toString().trim();

                if (name.isEmpty()) {
                    etName.setError("Enter medication name");
                    return;
                }

                addMedicationToServer(name, dose, period);
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    // =====================================================
    // ADD MEDICATION (REAL SERVER RESPONSE)
    // =====================================================
    private void addMedicationToServer(String name, String dose, String period) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(ADD_MEDICATION_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                JSONObject body = new JSONObject();
                body.put("patient_id", patientId);
                body.put("name", name);
                body.put("dose", dose);
                body.put("period", period);

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes("UTF-8"));
                os.close();

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                                conn.getResponseCode() < 400
                                        ? conn.getInputStream()
                                        : conn.getErrorStream()
                        )
                );

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();

                JSONObject res = new JSONObject(sb.toString());
                boolean success = res.optBoolean("success", false);
                String message = res.optString("message");

                runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(this, "Medication added", Toast.LENGTH_SHORT).show();
                        loadMedicationsFromServer();
                    } else {
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Add medication error", e);
                runOnUiThread(() ->
                        Toast.makeText(this, "Server error", Toast.LENGTH_LONG).show()
                );
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    // =====================================================
    // LOAD MEDICATIONS
    // =====================================================
    private void loadMedicationsFromServer() {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(GET_MEDICATIONS_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                JSONObject body = new JSONObject();
                body.put("patient_id", patientId);

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes("UTF-8"));
                os.close();

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream())
                );

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();

                JSONObject json = new JSONObject(sb.toString());
                JSONArray arr = json.getJSONArray("medications");

                items.clear();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject m = arr.getJSONObject(i);
                    items.add(new MedicationHistoryItem(
                            m.getString("name"),
                            m.getString("dose"),
                            m.getString("period")
                    ));
                }

                runOnUiThread(() -> adapter.notifyDataSetChanged());

            } catch (Exception e) {
                Log.e(TAG, "Load medications error", e);
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    // =====================================================
    // AI SUGGESTION
    // =====================================================
    private void loadLatestScoresAndRunAI() {
        new Thread(() -> {
            try {
                URL url = new URL(GET_GRAPH_URL + "?patient_id=" + patientId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream())
                );

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();

                JSONObject json = new JSONObject(sb.toString());
                JSONArray arr = json.getJSONArray("data");
                if (arr.length() == 0) return;

                JSONObject last = arr.getJSONObject(arr.length() - 1);
                double sdai = last.getDouble("sdai");
                double das28 = last.getDouble("das28_crp");

                String suggestion =
                        AiMedicationAdvisor.getSuggestion(sdai, das28);

                runOnUiThread(() ->
                        tvAiSuggestion.setText(
                                "🧠 AI Assessment\n" +
                                        "SDAI: " + sdai +
                                        " | DAS28: " + das28 +
                                        "\n💡 " + suggestion
                        )
                );

            } catch (Exception e) {
                Log.e(TAG, "AI load error", e);
            }
        }).start();
    }
}
