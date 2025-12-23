package com.saveetha.myjoints;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class PatientSignupActivity extends AppCompatActivity {

    private EditText etPatientId, etName, etEmail, etAge, etSex, etOccupation,
            etAddress, etMobile, etPassword, etRePassword;
    private ImageView backBtn;
    private MaterialButton btnSubmit;

    private static final String PREFS_NAME        = "patient_prefs";
    private static final String KEY_LAST_ID_NUM   = "last_id_number"; // for local auto generation
    private static final String KEY_PATIENT_ID    = "patient_id";
    private static final String KEY_PATIENT_NAME  = "patient_name";
    private static final String KEY_PATIENT_EMAIL = "patient_email";

    private static final String DOCTOR_PREFS_NAME = "doctor_prefs";
    private static final String KEY_DOCTOR_ID     = "doctor_id";

    private static final String SIGNUP_URL = "http://10.131.6.180/jointcare/patient_signup.php";
    private static final String TAG = "PatientSignup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_signup);
        initViews();
        setupListeners();

        // Auto-generate Patient ID
        String generatedId = generatePatientId();
        etPatientId.setText(generatedId);
        etPatientId.setEnabled(false);
        etPatientId.setFocusable(false);
    }

    private void initViews() {
        backBtn      = findViewById(R.id.back_btn);
        etPatientId  = findViewById(R.id.etPatientId);
        etName       = findViewById(R.id.etName);
        etEmail      = findViewById(R.id.etEmail);
        etAge        = findViewById(R.id.etAge);
        etSex        = findViewById(R.id.etSex);
        etOccupation = findViewById(R.id.etOccupation);
        etAddress    = findViewById(R.id.etAddress);
        etMobile     = findViewById(R.id.etMobile);
        etPassword   = findViewById(R.id.etPassword);
        etRePassword = findViewById(R.id.etRePassword);
        btnSubmit    = findViewById(R.id.btnSubmit);
    }

    private void setupListeners() {
        backBtn.setOnClickListener(v -> onBackPressed());
        btnSubmit.setOnClickListener(v -> submitForm());
    }

    // ---------------- AUTO GENERATE PID ----------------
    private String generatePatientId() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int lastNumber = prefs.getInt(KEY_LAST_ID_NUM, 0);
        int newNumber = lastNumber + 1;
        prefs.edit().putInt(KEY_LAST_ID_NUM, newNumber).apply();
        return String.format(Locale.getDefault(), "P%04d", newNumber);
    }

    private void submitForm() {
        String patientId  = etPatientId.getText().toString().trim();
        String name       = etName.getText().toString().trim();
        String email      = etEmail.getText().toString().trim();
        String ageStr     = etAge.getText().toString().trim();
        String sex        = etSex.getText().toString().trim();
        String occupation = etOccupation.getText().toString().trim();
        String address    = etAddress.getText().toString().trim();
        String mobile     = etMobile.getText().toString().trim();
        String password   = etPassword.getText().toString();
        String rePassword = etRePassword.getText().toString();

        // VALIDATIONS
        if (TextUtils.isEmpty(name)) { etName.setError("Enter Name"); return; }
        if (TextUtils.isEmpty(email) || !email.contains("@")) { etEmail.setError("Enter valid Email"); return; }
        if (TextUtils.isEmpty(ageStr)) { etAge.setError("Enter Age"); return; }

        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age <= 0) { etAge.setError("Invalid Age"); return; }
        } catch (Exception e) {
            etAge.setError("Invalid Age"); return;
        }

        if (sex.isEmpty()) { etSex.setError("Enter Sex"); return; }
        if (occupation.isEmpty()) { etOccupation.setError("Enter Occupation"); return; }
        if (address.isEmpty()) { etAddress.setError("Enter Address"); return; }
        if (mobile.isEmpty() || mobile.length() < 10) { etMobile.setError("Invalid Mobile Number"); return; }
        if (password.isEmpty()) { etPassword.setError("Enter Password"); return; }
        if (password.length() < 6) { etPassword.setError("Minimum 6 characters"); return; }
        if (!password.equals(rePassword)) { etRePassword.setError("Passwords do not match"); return; }

        registerPatientOnServer(patientId, name, email, age, sex,
                occupation, address, mobile, password);
    }

    private void registerPatientOnServer(String patientId, String name, String email, int age,
                                         String sex, String occupation, String address,
                                         String mobile, String password) {
        SharedPreferences doctorPrefs = getSharedPreferences(DOCTOR_PREFS_NAME, MODE_PRIVATE);
        String doctorId = doctorPrefs.getString(KEY_DOCTOR_ID, "");

        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(SIGNUP_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(15000);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                JSONObject body = new JSONObject();
                body.put("patient_id", patientId);
                body.put("name", name);
                body.put("email", email);
                body.put("age", String.valueOf(age));
                body.put("sex", sex);
                body.put("occupation", occupation);
                body.put("address", address);
                body.put("mobile", mobile);
                body.put("password", password);
                body.put("doctor_id", doctorId);

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes("UTF-8"));
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                InputStream is = (responseCode >= 200 && responseCode < 400)
                        ? conn.getInputStream()
                        : conn.getErrorStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();

                String response = sb.toString();
                Log.e(TAG, "Server response: " + response);

                runOnUiThread(() -> handleSignupResponse(response, patientId));

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(PatientSignupActivity.this,
                        "Server error", Toast.LENGTH_LONG).show());
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    private void handleSignupResponse(String response, String patientId) {
        try {
            JSONObject json = new JSONObject(response);
            boolean success = json.getBoolean("success");
            if (success) {
                String name  = etName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();

                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                prefs.edit()
                        .putString(KEY_PATIENT_ID, patientId)
                        .putString(KEY_PATIENT_NAME, name)
                        .putString(KEY_PATIENT_EMAIL, email)
                        .apply();

                Toast.makeText(this, "Registered! ID: " + patientId, Toast.LENGTH_LONG).show();

                // ✅ Pass patient_id to login via Intent extras
                Intent intent = new Intent(this, PatientLoginActivity.class);
                intent.putExtra("patient_id", patientId);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(this, json.optString("message", "Failed"), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Invalid response", Toast.LENGTH_LONG).show();
        }
    }
}
