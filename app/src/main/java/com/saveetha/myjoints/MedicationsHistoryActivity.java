package com.saveetha.myjoints;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class MedicationsHistoryActivity extends AppCompatActivity {

    private ImageView backBtn;
    private RecyclerView rvMedications;
    private TextView tvPatientId;
    private FloatingActionButton fabAddMedication;

    private final List<MedicationHistoryItem> items = new ArrayList<>();
    private MedicationHistoryAdapter adapter;
    private String patientId;

    // Doctor uses SAME server & folder as patient
    private static final String BASE_URL = "http://10.217.226.180/jointcare/";
    private static final String GET_MEDICATIONS_URL = BASE_URL + "get_medications.php";
    private static final String ADD_MEDICATION_URL  = BASE_URL + "add_medication.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medications_history);

        backBtn = findViewById(R.id.back_btn);
        rvMedications = findViewById(R.id.rvMedications);
        tvPatientId = findViewById(R.id.tvPatientId);
        fabAddMedication = findViewById(R.id.fabAddMedication);

        backBtn.setOnClickListener(v -> onBackPressed());

        // Receive patient ID from MedicalRecordsActivity
        Intent intent = getIntent();
        patientId = (intent != null) ? intent.getStringExtra("patient_id") : null;

        if (!TextUtils.isEmpty(patientId)) {
            tvPatientId.setText("Patient ID: " + patientId);
        } else {
            tvPatientId.setText("Patient ID: -");
            Toast.makeText(this, "No patient ID provided", Toast.LENGTH_LONG).show();
        }

        adapter = new MedicationHistoryAdapter(items);
        rvMedications.setLayoutManager(new LinearLayoutManager(this));
        rvMedications.setAdapter(adapter);

        // Load meds from server
        loadMedicationsFromServer();

        // Add (+) button opens dialog
        fabAddMedication.setOnClickListener(v -> showAddMedicationDialog());
    }

    // ================================================================
    // ADD MEDICATION DIALOG
    // ================================================================
    private void showAddMedicationDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_medication, null);

        EditText etName   = dialogView.findViewById(R.id.etMedicationName);
        EditText etDose   = dialogView.findViewById(R.id.etDose);
        EditText etPeriod = dialogView.findViewById(R.id.etPeriod);
        TextView tvAddAnother = dialogView.findViewById(R.id.tvAddAnotherMedication);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Add", null)   // we override click below
                .create();

        tvAddAnother.setOnClickListener(v ->
                Toast.makeText(this,
                        "Add-another feature coming soon!",
                        Toast.LENGTH_SHORT).show()
        );

        dialog.setOnShowListener(d -> {
            Button btnAdd = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btnAdd.setOnClickListener(v -> {
                String name   = etName.getText().toString().trim();
                String dose   = etDose.getText().toString().trim();
                String period = etPeriod.getText().toString().trim();

                if (name.isEmpty()) {
                    etName.setError("Enter name");
                    etName.requestFocus();
                    return;
                }

                if (dose.isEmpty())   dose = "-";
                if (period.isEmpty()) period = "-";

                addMedicationToServer(name, dose, period);
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    // ================================================================
    // SEND MEDICATION TO SERVER
    // ================================================================
    private void addMedicationToServer(String name, String dose, String period) {
        if (TextUtils.isEmpty(patientId)) {
            Toast.makeText(this, "Missing patient ID", Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(ADD_MEDICATION_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                JSONObject body = new JSONObject();
                body.put("patient_id", patientId);
                body.put("name", name);
                body.put("dose", dose);
                body.put("period", period);

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes("UTF-8"));
                os.flush();
                os.close();

                InputStream is = (conn.getResponseCode() < 400)
                        ? conn.getInputStream()
                        : conn.getErrorStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);

                JSONObject response = new JSONObject(sb.toString());

                runOnUiThread(() -> {
                    if (response.optBoolean("success")) {
                        Toast.makeText(this, "Medication added", Toast.LENGTH_SHORT).show();
                        loadMedicationsFromServer();
                    } else {
                        Toast.makeText(this,
                                response.optString("message", "Failed to add medication"),
                                Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this,
                                "Error adding medication",
                                Toast.LENGTH_LONG).show());
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    // ================================================================
    // LOAD MEDICATIONS FROM SERVER
    // ================================================================
    private void loadMedicationsFromServer() {
        if (TextUtils.isEmpty(patientId)) return;

        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(GET_MEDICATIONS_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                JSONObject body = new JSONObject();
                body.put("patient_id", patientId);

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes("UTF-8"));
                os.flush();
                os.close();

                InputStream is = (conn.getResponseCode() < 400)
                        ? conn.getInputStream()
                        : conn.getErrorStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);

                JSONObject response = new JSONObject(sb.toString());

                if (response.optBoolean("success")) {
                    JSONArray arr = response.getJSONArray("medications");
                    items.clear();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject m = arr.getJSONObject(i);
                        String name   = m.optString("name");
                        String dose   = m.optString("dose");
                        String period = m.optString("period");
                        items.add(new MedicationHistoryItem(name, dose, period));
                    }
                    runOnUiThread(() -> adapter.notifyDataSetChanged());
                } else {
                    String msg = response.optString("message", "Failed to load medications");
                    runOnUiThread(() ->
                            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                    );
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this,
                                "Error loading medications",
                                Toast.LENGTH_LONG).show());
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }
}
