package com.saveetha.myjoints;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PatientLoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private ImageView backBtn;
    private TextView tvSignUp;

    private static final String PREFS_NAME     = "patient_prefs";
    private static final String KEY_PATIENT_ID = "patient_id";
    private static final String KEY_PASSWORD   = "password";
    private static final String KEY_PATIENT_NAME  = "patient_name";
    private static final String KEY_PATIENT_EMAIL = "patient_email";
    private static final String LOGIN_URL = "http://10.131.6.180/jointcare/patient_login_api.php";

    private static final String TAG = "PatientLogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin   = findViewById(R.id.btnLogin);
        backBtn    = findViewById(R.id.back_btn);
        tvSignUp   = findViewById(R.id.tvSignUp);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // ✅ Prefill patient_id: 1) Intent extra, 2) SharedPreferences fallback
        String fromSignup     = getIntent().getStringExtra("patient_id");
        String savedPatientId = prefs.getString(KEY_PATIENT_ID, null);

        if (!TextUtils.isEmpty(fromSignup)) {
            etUsername.setText(fromSignup);
            etUsername.setSelection(fromSignup.length());
        } else if (!TextUtils.isEmpty(savedPatientId)) {
            etUsername.setText(savedPatientId);
            etUsername.setSelection(savedPatientId.length());
        }

        backBtn.setOnClickListener(v -> onBackPressed());

        btnLogin.setOnClickListener(v -> {
            String userInput = etUsername.getText().toString().trim();
            String passInput = etPassword.getText().toString().trim();
            if (TextUtils.isEmpty(userInput)) {
                etUsername.setError("Please enter Patient ID");
                etUsername.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(passInput)) {
                etPassword.setError("Please enter Password");
                etPassword.requestFocus();
                return;
            }
            loginPatientOnServer(userInput, passInput);
        });

        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(PatientLoginActivity.this, PatientSignupActivity.class);
            startActivity(intent);
        });
    }

    private void loginPatientOnServer(String patientId, String password) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(LOGIN_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(15000);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                JSONObject body = new JSONObject();
                body.put("patient_id", patientId);
                body.put("password", password);

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

                runOnUiThread(() -> handleLoginResponse(response, patientId, password));

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(PatientLoginActivity.this,
                        "Server connection failed", Toast.LENGTH_LONG).show());
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    private void handleLoginResponse(String response, String patientId, String password) {
        try {
            JSONObject json = new JSONObject(response);
            boolean success = json.getBoolean("success");
            if (success) {
                String patientIdFromServer = json.optString("patient_id", patientId);
                String nameFromServer = json.optString("name", json.optString("patient_name", ""));
                String emailFromServer = json.optString("email", json.optString("patient_email", ""));

                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit()
                        .putString(KEY_PATIENT_ID, patientIdFromServer)
                        .putString(KEY_PASSWORD, password);

                if (!TextUtils.isEmpty(nameFromServer)) editor.putString(KEY_PATIENT_NAME, nameFromServer);
                if (!TextUtils.isEmpty(emailFromServer)) editor.putString(KEY_PATIENT_EMAIL, emailFromServer);
                editor.apply();

                etUsername.setText(patientIdFromServer);
                etUsername.setSelection(patientIdFromServer.length());

                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(PatientLoginActivity.this, PatientDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                String message = json.optString("message", "Login failed");
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid server response", Toast.LENGTH_LONG).show();
        }
    }
}
