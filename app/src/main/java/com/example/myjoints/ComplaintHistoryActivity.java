package com.example.myjoints;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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

public class ComplaintHistoryActivity extends AppCompatActivity {

    private RecyclerView rvComplaintHistory;
    private ImageView backBtn;
    private FloatingActionButton fabAddComplaintHistory;
    private TextView tvPatientId;

    private final List<ComplaintHistoryItem> complaints = new ArrayList<>();
    private ComplaintHistoryAdapter adapter;

    private static final String GET_COMPLAINTS_URL =
            "http://10.217.226.180/jointcare/get_complaints.php";
    private static final String ADD_COMPLAINT_URL =
            "http://10.217.226.180/jointcare/add_complaint.php";
    private static final String TAG = "ComplaintHistory";

    private static final String PREFS_NAME_DOCTOR = "doctor_prefs";
    private static final String KEY_DOCTOR_ID     = "doctor_id";

    private String patientId;
    private String doctorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_history);

        rvComplaintHistory     = findViewById(R.id.rvComplaintHistory);
        backBtn                = findViewById(R.id.back_btn);
        fabAddComplaintHistory = findViewById(R.id.fabAddComplaintHistory);
        tvPatientId            = findViewById(R.id.tvPatientId);

        // patient id passed from MyPatientsActivity
        patientId = getIntent().getStringExtra("patient_id");
        if (TextUtils.isEmpty(patientId)) {
            Toast.makeText(this,
                    "No patient id provided to ComplaintHistoryActivity",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // show it under the title
        tvPatientId.setText("Patient ID: " + patientId);

        // get doctor_id from SharedPreferences (doctor login)
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

        fabAddComplaintHistory.setOnClickListener(v -> showAddComplaintDialog());

        rvComplaintHistory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ComplaintHistoryAdapter(complaints);
        rvComplaintHistory.setAdapter(adapter);

        loadComplaintsFromServer();
    }

    // ---------------- Add complaint (doctor) ----------------
    private void showAddComplaintDialog() {
        final EditText input = new EditText(this);
        input.setHint("Enter complaint (title)");

        new AlertDialog.Builder(this)
                .setTitle("Add Complaint")
                .setView(input)
                .setPositiveButton("Submit", (dialog, which) -> {
                    String text = input.getText().toString().trim();
                    if (text.isEmpty()) {
                        Toast.makeText(this,
                                "Complaint cannot be empty",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        addComplaintToServer(text);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addComplaintToServer(String title) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(ADD_COMPLAINT_URL);
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
                body.put("title", title);
                body.put("description", "");

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
                Log.d(TAG, "ADD_COMPLAINT response: " + respStr);

                JSONObject json = new JSONObject(respStr);
                boolean success = json.optBoolean("success", false);
                String msg = json.optString("message",
                        success ? "Complaint added" : "Failed to add complaint");

                runOnUiThread(() -> {
                    Toast.makeText(ComplaintHistoryActivity.this,
                            msg,
                            Toast.LENGTH_LONG).show();
                    if (success) {
                        loadComplaintsFromServer();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(ComplaintHistoryActivity.this,
                                "Error adding complaint: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    // ---------------- Load complaints ----------------
    private void loadComplaintsFromServer() {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(GET_COMPLAINTS_URL);
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
                Log.d(TAG, "GET_COMPLAINTS response: " + respStr);

                JSONObject json = new JSONObject(respStr);
                boolean success = json.getBoolean("success");

                if (success) {
                    JSONArray arr = json.optJSONArray("complaints");
                    List<ComplaintHistoryItem> tempList = new ArrayList<>();

                    if (arr != null) {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject c = arr.getJSONObject(i);
                            String title     = c.optString("title", "");
                            String createdAt = c.optString("created_at", "");
                            tempList.add(new ComplaintHistoryItem(title, createdAt));
                        }
                    }

                    runOnUiThread(() -> {
                        complaints.clear();
                        complaints.addAll(tempList);
                        adapter.notifyDataSetChanged();

                        if (complaints.isEmpty()) {
                            Toast.makeText(this,
                                    "No complaints found for this patient",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    String msg = json.optString("message", "Failed to load complaints");
                    runOnUiThread(() ->
                            Toast.makeText(this, msg, Toast.LENGTH_LONG).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this,
                                "Error loading complaints: " + e.getMessage(),
                                Toast.LENGTH_LONG).show());
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    // ---------------- Model ----------------
    private static class ComplaintHistoryItem {
        final String title;
        final String date;

        ComplaintHistoryItem(String title, String date) {
            this.title = title;
            this.date  = date;
        }
    }

    // ---------------- Adapter ----------------
    private static class ComplaintHistoryAdapter
            extends RecyclerView.Adapter<ComplaintHistoryAdapter.ViewHolder> {

        private final List<ComplaintHistoryItem> items;

        ComplaintHistoryAdapter(List<ComplaintHistoryItem> items) {
            this.items = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_complaint_history, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ComplaintHistoryItem item = items.get(position);
            holder.tvComplaintTitle.setText(item.title);
            holder.tvComplaintDate.setText("Date: " + item.date);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvComplaintTitle, tvComplaintDate;

            ViewHolder(View itemView) {
                super(itemView);
                tvComplaintTitle = itemView.findViewById(R.id.tvComplaintTitle);
                tvComplaintDate  = itemView.findViewById(R.id.tvComplaintDate);
            }
        }
    }
}
