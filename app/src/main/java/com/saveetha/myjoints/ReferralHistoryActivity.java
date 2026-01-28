package com.saveetha.myjoints;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ReferralHistoryActivity extends AppCompatActivity {

    private static final String TAG = "ReferralHistory";
    private static final String PREFS_NAME     = "patient_prefs";
    private static final String KEY_PATIENT_ID = "patient_id";

    // Use same server style as TreatmentsHistoryActivity
    private static final String BASE_URL            = "https://3cxr1p7f-80.inc1.devtunnels.ms/jointcare/";
    private static final String ADD_REFERRAL_URL    = BASE_URL + "referralhistory_add.php";
    private static final String GET_REFERRALS_URL   = BASE_URL + "referralhistory_get.php";

    private ImageView backBtn;
    private TextView tvTitle, tvPatientId;
    private RecyclerView rvReferrals;
    private FloatingActionButton fabAddReferral;

    private final List<ReferralItem> referralList = new ArrayList<>();
    private ReferralAdapter adapter;
    private String patientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referrals_history);

        backBtn      = findViewById(R.id.back_btn);
        tvTitle      = findViewById(R.id.tvTitle);
        tvPatientId  = findViewById(R.id.tvPatientId);
        rvReferrals  = findViewById(R.id.rvReferrals);
        fabAddReferral = findViewById(R.id.fabAddReferral);

        backBtn.setOnClickListener(v -> onBackPressed());

        // 1) Try to get from Intent (same as TreatmentsHistoryActivity)
        Intent intent = getIntent();
        if (intent != null) {
            patientId = intent.getStringExtra("patient_id"); // note: lower case key
        }

        // 2) Fallback: SharedPreferences
        if (TextUtils.isEmpty(patientId)) {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            patientId = prefs.getString(KEY_PATIENT_ID, null);
        }

        // 3) Last resort: hard-coded for testing
        if (TextUtils.isEmpty(patientId)) {
            patientId = "P0001";
            Log.w(TAG, "No patient_id found; using P0001 for testing");
            Toast.makeText(this,
                    "No patient id passed, using P0001 (test)",
                    Toast.LENGTH_SHORT).show();
        }

        tvPatientId.setText("Patient ID: " + patientId);
        Log.d(TAG, "Using patientId = " + patientId);

        // RecyclerView setup
        rvReferrals.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReferralAdapter(referralList);
        rvReferrals.setAdapter(adapter);

        // Load existing referrals from server
        loadReferralsFromServer();

        fabAddReferral.setOnClickListener(v -> showAddReferralDialog());
    }

    /** Dialog for doctor to add new referral. */
    private void showAddReferralDialog() {
        EditText input = new EditText(this);
        input.setHint("Enter referral text");
        input.setMinLines(2);
        input.setMaxLines(4);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        new AlertDialog.Builder(this)
                .setTitle("Add Referral")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String referralText = input.getText().toString().trim();
                    if (!TextUtils.isEmpty(referralText)) {
                        // We now always use real patientId
                        ReferralItem newItem = new ReferralItem(referralText, patientId);
                        referralList.add(0, newItem);
                        adapter.notifyItemInserted(0);
                        rvReferrals.smoothScrollToPosition(0);

                        // Send to PHP/MySQL
                        sendReferralToServer(referralText);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /** POST JSON to referralhistory_add.php */
    private void sendReferralToServer(String message) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(ADD_REFERRAL_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(15000);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                JSONObject json = new JSONObject();
                json.put("patient_id", patientId);
                json.put("message", message);

                String jsonBody = json.toString();
                Log.d(TAG, "addReferral request: " + jsonBody);

                OutputStream os = conn.getOutputStream();
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "addReferral HTTP code: " + responseCode);

                InputStream is = (responseCode < 400)
                        ? conn.getInputStream()
                        : conn.getErrorStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();

                String respStr = sb.toString();
                Log.d(TAG, "addReferral response: " + respStr);

                JSONObject response = new JSONObject(respStr);
                runOnUiThread(() -> {
                    if (response.optBoolean("success")) {
                        Toast.makeText(this, "Referral added", Toast.LENGTH_SHORT).show();
                        loadReferralsFromServer();
                    } else {
                        Toast.makeText(this,
                                response.optString("message", "Failed to add referral"),
                                Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error adding referral", e);
                runOnUiThread(() ->
                        Toast.makeText(this,
                                "Error adding referral",
                                Toast.LENGTH_LONG).show());
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    /** POST JSON to referralhistory_get.php and load list */
    private void loadReferralsFromServer() {
        new Thread(() -> {
            HttpURLConnection conn = null;
            List<ReferralItem> loaded = new ArrayList<>();
            try {
                URL url = new URL(GET_REFERRALS_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(15000);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                JSONObject body = new JSONObject();
                body.put("patient_id", patientId);
                String jsonBody = body.toString();
                Log.d(TAG, "loadReferrals request: " + jsonBody);

                OutputStream os = conn.getOutputStream();
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "loadReferrals HTTP code: " + responseCode);

                InputStream is = (responseCode < 400)
                        ? conn.getInputStream()
                        : conn.getErrorStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();

                String respStr = sb.toString();
                Log.d(TAG, "loadReferrals response: " + respStr);

                JSONObject response = new JSONObject(respStr);
                // PHP: { "success": true, "data": [ ... ] }
                if (response.optBoolean("success")) {
                    JSONArray arr = response.optJSONArray("data");
                    if (arr != null) {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            String msg = obj.optString("message");
                            String pid = obj.optString("patient_id");
                            loaded.add(new ReferralItem(msg, pid));
                        }
                    }
                } else {
                    String msg = response.optString("message",
                            "Failed to load referrals");
                    runOnUiThread(() ->
                            Toast.makeText(this, msg, Toast.LENGTH_LONG).show());
                }

            } catch (Exception e) {
                Log.e(TAG, "Error loading referrals", e);
                runOnUiThread(() ->
                        Toast.makeText(this,
                                "Error loading referrals",
                                Toast.LENGTH_LONG).show());
            } finally {
                if (conn != null) conn.disconnect();
            }

            runOnUiThread(() -> {
                referralList.clear();
                referralList.addAll(loaded);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }
}
