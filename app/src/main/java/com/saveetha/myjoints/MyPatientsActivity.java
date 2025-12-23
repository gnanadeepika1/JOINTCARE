package com.saveetha.myjoints;

import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyPatientsActivity extends AppCompatActivity {

    private LinearLayout containerCards;
    private ImageView btnBack, btnSettings, btnLogout;
    private TextView tvDoctorHeaderName;   // title in header
    private TextView tvDoctorCardName;     // doctor card name
    private TextView tvDoctorCardEmail;    // doctor card email
    private TextView tvPatientsTitle;      // "My Patients (N)"

    private static final String PREFS_NAME    = "doctor_prefs";
    private static final String KEY_DOCTOR_ID = "doctor_id";

    // PHP URLs
    private static final String GET_DOCTOR_URL =
            "http://10.131.6.180/jointcare/get_doctor.php";
    private static final String GET_PATIENTS_URL =
            "http://10.131.6.180/jointcare/get_patients.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_patients);

        // Views
        containerCards      = findViewById(R.id.containerCards);
        btnBack             = findViewById(R.id.btnBack);
        btnSettings         = findViewById(R.id.btnSettings);
        btnLogout           = findViewById(R.id.btnLogout);
        tvDoctorHeaderName  = findViewById(R.id.tvDoctorHeaderName);
        tvDoctorCardName    = findViewById(R.id.tvDoctorCardName);
        tvDoctorCardEmail   = findViewById(R.id.tvDoctorCardEmail);
        tvPatientsTitle     = findViewById(R.id.tvPatientsTitle);

        btnBack.setOnClickListener(v -> onBackPressed());

        btnSettings.setOnClickListener(v ->
                Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
        );

        btnLogout.setOnClickListener(v -> {
            // Simple logout: clear prefs and go back to login
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            prefs.edit().clear().apply();

            Intent i = new Intent(this, DoctorLoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });

        // Fetch doctor + patients from backend
        loadDoctorNameFromServer();
        loadPatientsFromServer();
    }

    // ------------------------------------------------------------
    //  FETCH DOCTOR NAME + EMAIL FROM BACKEND
    // ------------------------------------------------------------
    private void loadDoctorNameFromServer() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String doctorId = prefs.getString(KEY_DOCTOR_ID, "");
        if (doctorId.isEmpty()) {
            Toast.makeText(this, "No doctor ID saved", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(GET_DOCTOR_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                JSONObject body = new JSONObject();
                body.put("doctor_id", doctorId);

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes("UTF-8"));
                os.flush();
                os.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        conn.getResponseCode() >= 200 && conn.getResponseCode() < 300
                                ? conn.getInputStream()
                                : conn.getErrorStream()
                ));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();

                String respStr = sb.toString().trim();
                Log.d("API_DEBUG", "GET_DOCTOR response: " + respStr);

                JSONObject response;
                try {
                    response = new JSONObject(respStr);
                } catch (JSONException je) {
                    String msg = respStr.length() > 60 ? respStr.substring(0, 60) + "..." : respStr;
                    runOnUiThread(() ->
                            Toast.makeText(this,
                                    "Doctor fetch error (not JSON): " + msg,
                                    Toast.LENGTH_LONG).show());
                    return;
                }

                if (response.optBoolean("success")) {
                    JSONObject doc = response.optJSONObject("doctor");
                    if (doc != null) {
                        String fullName = doc.optString("full_name", "");
                        String email    = doc.optString("email", "");

                        runOnUiThread(() -> {
                            String drName = "DR " + fullName;
                            tvDoctorHeaderName.setText(drName);   // header
                            tvDoctorCardName.setText(drName);     // card
                            tvDoctorCardEmail.setText(email);     // card email
                        });
                    }
                } else {
                    String err = response.optString("message", "Unknown error");
                    runOnUiThread(() ->
                            Toast.makeText(this,
                                    "Doctor fetch failed: " + err,
                                    Toast.LENGTH_LONG).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this,
                                "Doctor fetch error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show());
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    // ------------------------------------------------------------
    //  FETCH PATIENT LIST (FOR THIS DOCTOR)
    // ------------------------------------------------------------
    private void loadPatientsFromServer() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String doctorId = prefs.getString(KEY_DOCTOR_ID, "");
        if (doctorId.isEmpty()) {
            Toast.makeText(this, "No doctor ID saved", Toast.LENGTH_SHORT).show();
            tvPatientsTitle.setText("My Patients (0)");
            return;
        }

        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(GET_PATIENTS_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                // Send doctor_id so backend can return only this doctor's patients
                JSONObject body = new JSONObject();
                body.put("doctor_id", doctorId);

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes("UTF-8"));
                os.flush();
                os.close();

                int code = conn.getResponseCode();
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        code >= 200 && code < 300
                                ? conn.getInputStream()
                                : conn.getErrorStream()
                ));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();

                String respStr = sb.toString().trim();
                Log.d("API_DEBUG", "GET_PATIENTS response: " + respStr);

                JSONObject respJson;
                try {
                    respJson = new JSONObject(respStr);
                } catch (JSONException je) {
                    String msg = respStr.length() > 60 ? respStr.substring(0, 60) + "..." : respStr;
                    runOnUiThread(() ->
                            Toast.makeText(this,
                                    "Patients fetch error (not JSON): " + msg,
                                    Toast.LENGTH_LONG).show());
                    return;
                }

                boolean success       = respJson.optBoolean("success", false);
                JSONArray patientsArr = respJson.optJSONArray("patients");

                runOnUiThread(() -> {
                    if (!success || patientsArr == null) {
                        tvPatientsTitle.setText("My Patients (0)");
                        Toast.makeText(this,
                                "Failed to load patients",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int count = patientsArr.length();
                    tvPatientsTitle.setText("My Patients (" + count + ")");

                    containerCards.removeAllViews();
                    LayoutInflater inflater = LayoutInflater.from(this);

                    for (int i = 0; i < patientsArr.length(); i++) {
                        JSONObject p = patientsArr.optJSONObject(i);
                        if (p == null) continue;

                        String patientId = p.optString("patient_id", "");
                        String fullName  = p.optString("name", "Unknown");
                        String email     = p.optString("email", "No email");

                        View card = inflater.inflate(R.layout.item_patient_card, containerCards, false);

                        TextView tvPatientName  = card.findViewById(R.id.tvPatientName);
                        TextView tvPatientEmail = card.findViewById(R.id.tvPatientEmail);
                        // ivMessage & ivAction removed from layout, so no findViewById here

                        tvPatientName.setText(fullName);
                        tvPatientEmail.setText(email);

                        card.setOnClickListener(v -> {
                            Intent intent = new Intent(MyPatientsActivity.this, MedicalRecordsActivity.class);
                            intent.putExtra("patient_id",   patientId);
                            intent.putExtra("patient_name", fullName);
                            intent.putExtra("patient_email", email);
                            startActivity(intent);
                        });

                        containerCards.addView(card);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this,
                                "Error: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }
}
