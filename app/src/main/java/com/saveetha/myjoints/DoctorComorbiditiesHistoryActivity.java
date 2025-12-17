package com.saveetha.myjoints;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

public class DoctorComorbiditiesHistoryActivity extends AppCompatActivity {

    private LinearLayout llComorbidityList;   // matches activity_doctor_comorbidities_history.xml
    private ImageView backBtn;
    private FloatingActionButton fabAdd;
    private TextView tvPatientId;

    private static final String GET_COMORBIDITIES_URL =
            "http://172.25.109.58/jointcare/get_comorbidities.php";
    private static final String ADD_COMORBIDITY_URL =
            "http://172.25.109.58/jointcare/add_comorbidity.php";

    private static final String PREFS_NAME_DOCTOR = "doctor_prefs";
    private static final String KEY_DOCTOR_ID     = "doctor_id";
    private static final String TAG               = "DocComorbidHistory";

    private String patientId;
    private String doctorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_comorbidities_history);

        llComorbidityList = findViewById(R.id.llComorbidityList);
        backBtn           = findViewById(R.id.back_btn);
        fabAdd            = findViewById(R.id.fabAdd);
        tvPatientId       = findViewById(R.id.tvPatientId);

        // patient id from MedicalRecordsActivity
        patientId = getIntent().getStringExtra("patient_id");
        if (TextUtils.isEmpty(patientId)) {
            Toast.makeText(this,
                    "No patient id provided to Comorbidities screen",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        tvPatientId.setText("Patient ID: " + patientId);

        // doctor id from prefs
        SharedPreferences prefs =
                getSharedPreferences(PREFS_NAME_DOCTOR, MODE_PRIVATE);
        doctorId = prefs.getString(KEY_DOCTOR_ID, null);
        if (TextUtils.isEmpty(doctorId)) {
            Toast.makeText(this,
                    "No doctor id found. Please login again.",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        backBtn.setOnClickListener(v -> onBackPressed());
        fabAdd.setOnClickListener(v -> showAddComorbidityDialog());

        loadComorbiditiesFromServer();
    }

    // ---------------- Dialog to add comorbidity ----------------
    private void showAddComorbidityDialog() {
        final EditText input = new EditText(this);
        input.setHint("Enter comorbidity (e.g. Diabetes)");

        new AlertDialog.Builder(this)
                .setTitle("Add Comorbidity")
                .setView(input)
                .setPositiveButton("Submit", (dialog, which) -> {
                    String text = input.getText().toString().trim();
                    if (text.isEmpty()) {
                        Toast.makeText(this,
                                "Comorbidity cannot be empty",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        addComorbidityToServer(text);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addComorbidityToServer(String text) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(ADD_COMORBIDITY_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(15000);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                JSONObject body = new JSONObject();
                body.put("patient_id", patientId);
                body.put("doctor_id", doctorId);
                body.put("text", text);             // main comorbidity text
                body.put("title", "Comorbidity");   // heading shown in pink

                String jsonBody = body.toString();
                OutputStream os = conn.getOutputStream();
                os.write(jsonBody.getBytes("UTF-8"));
                os.flush();
                os.close();

                int code = conn.getResponseCode();
                InputStream is = (code >= 200 && code < 400)
                        ? conn.getInputStream()
                        : conn.getErrorStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();

                String respStr = sb.toString();
                Log.d(TAG, "ADD_COMORBIDITY response: " + respStr);

                JSONObject json = new JSONObject(respStr);
                boolean success = json.optBoolean("success", false);
                String msg = json.optString("message",
                        success ? "Comorbidity added" : "Failed to add comorbidity");

                runOnUiThread(() -> {
                    Toast.makeText(DoctorComorbiditiesHistoryActivity.this,
                            msg,
                            Toast.LENGTH_LONG).show();
                    if (success) {
                        loadComorbiditiesFromServer(); // refresh list
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(DoctorComorbiditiesHistoryActivity.this,
                                "Error adding comorbidity: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    // ---------------- Load comorbidities ----------------
    private void loadComorbiditiesFromServer() {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(GET_COMORBIDITIES_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(15000);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                JSONObject body = new JSONObject();
                body.put("patient_id", patientId);
                String jsonBody = body.toString();

                OutputStream os = conn.getOutputStream();
                os.write(jsonBody.getBytes("UTF-8"));
                os.flush();
                os.close();

                InputStream is = (conn.getResponseCode() >= 200 && conn.getResponseCode() < 400)
                        ? conn.getInputStream()
                        : conn.getErrorStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null)
                    sb.append(line);

                String respStr = sb.toString();
                Log.d(TAG, "GET_COMORBIDITIES response: " + respStr);

                JSONObject json = new JSONObject(respStr);
                boolean success = json.getBoolean("success");

                List<ComorbidityItem> temp = new ArrayList<>();

                if (success) {
                    JSONArray arr = json.optJSONArray("comorbidities");
                    if (arr != null) {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject c = arr.getJSONObject(i);
                            String title = c.optString("title", "Comorbidity");
                            String text  = c.optString("text", "");
                            temp.add(new ComorbidityItem(title, text));
                        }
                    }
                } else {
                    String msg = json.optString("message", "Failed to load comorbidities");
                    runOnUiThread(() ->
                            Toast.makeText(this, msg, Toast.LENGTH_LONG).show());
                }

                runOnUiThread(() -> {
                    llComorbidityList.removeAllViews();
                    LayoutInflater inflater = LayoutInflater.from(this);

                    for (ComorbidityItem item : temp) {
                        View card = inflater.inflate(
                                R.layout.item_comorbidity_history,
                                llComorbidityList,
                                false
                        );
                        TextView tvTitle = card.findViewById(R.id.tvTitle);
                        TextView tvText  = card.findViewById(R.id.tvText);

                        tvTitle.setText(item.title);
                        tvText.setText(item.text);

                        llComorbidityList.addView(card);
                    }

                    if (temp.isEmpty()) {
                        Toast.makeText(this,
                                "No comorbidities found for this patient",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this,
                                "Error loading comorbidities: " + e.getMessage(),
                                Toast.LENGTH_LONG).show());
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    // ---------------- Model ----------------
    private static class ComorbidityItem {
        final String title;
        final String text;

        ComorbidityItem(String title, String text) {
            this.title = title;
            this.text  = text;
        }
    }
}
