package com.saveetha.myjoints;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DoctorSignupActivity extends AppCompatActivity {

    private ImageView backBtn;
    private EditText etDoctorId, etFullName, etEmail, etPhone, etSpecialization, etPassword, etConfirmPassword;
    private MaterialButton btnPatient;

    private static final String PREFS_NAME         = "doctor_prefs";
    private static final String KEY_DOCTOR_ID      = "doctor_id";
    private static final String KEY_FULL_NAME      = "full_name";
    private static final String KEY_EMAIL          = "email";
    private static final String KEY_PHONE          = "phone";
    private static final String KEY_SPECIALIZATION = "specialization";
    private static final String KEY_PASSWORD       = "password";

    private static final String SIGNUP_URL = "http://172.25.109.58/jointcare/doctor_signup.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_signup);

        backBtn          = findViewById(R.id.back_btn);
        etDoctorId       = findViewById(R.id.etDoctorId);
        etFullName       = findViewById(R.id.etFullName);
        etEmail          = findViewById(R.id.etEmail);
        etPhone          = findViewById(R.id.etPhone);
        etSpecialization = findViewById(R.id.etSpecialization);
        etPassword       = findViewById(R.id.etPassword);
        etConfirmPassword= findViewById(R.id.etConfirmPassword);
        btnPatient       = findViewById(R.id.btnPatient);

        backBtn.setOnClickListener(v -> onBackPressed());
        btnPatient.setOnClickListener(v -> handleSubmit());
    }

    private void handleSubmit() {
        String doctorId = etDoctorId.getText().toString().trim();
        String name     = etFullName.getText().toString().trim();
        String email    = etEmail.getText().toString().trim();
        String phone    = etPhone.getText().toString().trim();
        String spec     = etSpecialization.getText().toString().trim();
        String pass     = etPassword.getText().toString();
        String conf     = etConfirmPassword.getText().toString();

        // Validation
        if (TextUtils.isEmpty(doctorId)) { etDoctorId.setError("Doctor ID is required"); etDoctorId.requestFocus(); return; }
        if (!doctorId.matches("(?i)^doc_\\d{4}$")) { etDoctorId.setError("Doctor ID must be like doc_1001"); etDoctorId.requestFocus(); return; }
        if (TextUtils.isEmpty(name)) { etFullName.setError("Full name is required"); etFullName.requestFocus(); return; }
        if (name.length() < 3) { etFullName.setError("Name must be at least 3 characters"); etFullName.requestFocus(); return; }
        if (TextUtils.isEmpty(email)) { etEmail.setError("Email is required"); etEmail.requestFocus(); return; }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { etEmail.setError("Enter a valid email"); etEmail.requestFocus(); return; }
        if (TextUtils.isEmpty(phone)) { etPhone.setError("Phone number is required"); etPhone.requestFocus(); return; }
        if (!phone.matches("\\d+")) { etPhone.setError("Phone number should contain only digits"); etPhone.requestFocus(); return; }
        if (phone.length() < 10) { etPhone.setError("Phone number must be at least 10 digits"); etPhone.requestFocus(); return; }
        if (TextUtils.isEmpty(spec)) { etSpecialization.setError("Specialization is required"); etSpecialization.requestFocus(); return; }
        if (TextUtils.isEmpty(pass)) { etPassword.setError("Password is required"); etPassword.requestFocus(); return; }
        if (pass.length() < 6) { etPassword.setError("Password must be at least 6 characters"); etPassword.requestFocus(); return; }
        if (TextUtils.isEmpty(conf)) { etConfirmPassword.setError("Please confirm your password"); etConfirmPassword.requestFocus(); return; }
        if (!pass.equals(conf)) { etConfirmPassword.setError("Passwords do not match"); etConfirmPassword.requestFocus(); Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show(); return; }

        sendDataToServer(doctorId, name, email, phone, spec, pass);
    }

    private void sendDataToServer(String doctorId, String name, String email,
                                  String phone, String spec, String pass) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                JSONObject body = new JSONObject();
                body.put("doctor_id", doctorId);
                body.put("full_name", name);
                body.put("email", email);
                body.put("phone", phone);
                body.put("specialization", spec);
                body.put("password", pass);

                URL url = new URL(SIGNUP_URL);
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

                int responseCode = conn.getResponseCode();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                responseCode >= 200 && responseCode < 300
                                        ? conn.getInputStream()
                                        : conn.getErrorStream()
                        )
                );
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();

                JSONObject respJson = new JSONObject(sb.toString().trim());
                boolean success     = respJson.optBoolean("success", false);
                String message      = respJson.optString("message", "");
                String serverDocId  = respJson.optString("doctor_id", doctorId);

                runOnUiThread(() -> {
                    if (success) {
                        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(KEY_DOCTOR_ID, serverDocId);
                        editor.putString(KEY_FULL_NAME, name);
                        editor.putString(KEY_EMAIL, email);
                        editor.putString(KEY_PHONE, phone);
                        editor.putString(KEY_SPECIALIZATION, spec);
                        editor.putString(KEY_PASSWORD, pass);
                        editor.apply();

                        Toast.makeText(this,
                                message.isEmpty() ? "Registration Successful!" : message,
                                Toast.LENGTH_SHORT).show();

                        // Navigate to login with doctor_id
                        Intent intent = new Intent(this, DoctorLoginActivity.class);
                        intent.putExtra("doctor_id", serverDocId);

                        // 🔹 Ensure login activity receives the new doctor_id
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this,
                                message.isEmpty() ? "Registration failed" : message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this,
                                "Network/JSON error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }
}
