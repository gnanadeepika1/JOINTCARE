package com.saveetha.myjoints;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DoctorLoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;   // Doctor ID
    private Button btnLogin;
    private ImageView backBtn;
    private TextView tvSignUp;

    private static final String PREFS_NAME    = "doctor_prefs";
    private static final String KEY_DOCTOR_ID = "doctor_id";
    private static final String KEY_EMAIL     = "email";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_PHONE     = "phone";
    private static final String KEY_SPECIAL   = "specialization";
    private static final String KEY_PASSWORD  = "password";

    private static final String LOGIN_URL = "http://10.217.226.180/jointcare/doctor_login.php";

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin   = findViewById(R.id.btnLogin);
        backBtn    = findViewById(R.id.back_btn);
        tvSignUp   = findViewById(R.id.tvSignUp);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        backBtn.setOnClickListener(v -> finish());
        tvSignUp.setOnClickListener(v ->
                startActivity(new Intent(this, DoctorSignupActivity.class))
        );
        btnLogin.setOnClickListener(v -> handleLogin());

        // 🔹 Prefill Doctor ID immediately
        prefillDoctorId();
    }

    @Override
    protected void onResume() {
        super.onResume();
        prefillDoctorId();
    }

    private void prefillDoctorId() {
        String fromSignup = getIntent().getStringExtra("doctor_id");
        if (fromSignup != null && !fromSignup.isEmpty()) {
            etUsername.setText(fromSignup);
            etUsername.setSelection(fromSignup.length());
        } else {
            String savedDoctorId = prefs.getString(KEY_DOCTOR_ID, "");
            if (!savedDoctorId.isEmpty()) {
                etUsername.setText(savedDoctorId);
                etUsername.setSelection(savedDoctorId.length());
            }
        }
    }

    private void handleLogin() {
        String doctorId = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (doctorId.isEmpty()) { etUsername.setError("Doctor ID is required"); etUsername.requestFocus(); return; }
        if (!doctorId.matches("(?i)^doc_\\d{4}$")) { etUsername.setError("Doctor ID must be like doc_1001"); etUsername.requestFocus(); return; }
        if (password.isEmpty()) { etPassword.setError("Password is required"); etPassword.requestFocus(); return; }

        sendLoginRequest(doctorId, password);
    }

    private void sendLoginRequest(String doctorId, String password) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                JSONObject body = new JSONObject();
                body.put("doctor_id", doctorId);
                body.put("password", password);

                URL url = new URL(LOGIN_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes("UTF-8"));
                os.flush();
                os.close();

                int code = conn.getResponseCode();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                (code >= 200 && code < 300)
                                        ? conn.getInputStream()
                                        : conn.getErrorStream()
                        )
                );

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) { sb.append(line); }
                reader.close();

                JSONObject respJson = new JSONObject(sb.toString().trim());
                boolean success = respJson.optBoolean("success", false);
                String message  = respJson.optString("message", "");

                runOnUiThread(() -> {
                    if (success) {
                        String serverDoctorId = respJson.optString("doctor_id", doctorId);
                        String fullName       = respJson.optString("full_name", "");
                        String email          = respJson.optString("email", "");
                        String phone          = respJson.optString("phone", "");
                        String specialization = respJson.optString("specialization", "");

                        prefs.edit()
                                .putString(KEY_DOCTOR_ID, serverDoctorId)
                                .putString(KEY_FULL_NAME, fullName)
                                .putString(KEY_EMAIL, email)
                                .putString(KEY_PHONE, phone)
                                .putString(KEY_SPECIAL, specialization)
                                .putString(KEY_PASSWORD, password)
                                .apply();

                        etUsername.setText(serverDoctorId);
                        etUsername.setSelection(serverDoctorId.length());

                        Toast.makeText(DoctorLoginActivity.this,
                                message.isEmpty() ? "Login Successful" : message,
                                Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(DoctorLoginActivity.this, MyPatientsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(DoctorLoginActivity.this,
                                message.isEmpty() ? "Invalid Credentials" : message,
                                Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(DoctorLoginActivity.this,
                                "Network error: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }
}
