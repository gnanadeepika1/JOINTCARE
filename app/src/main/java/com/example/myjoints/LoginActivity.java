package com.example.myjoints;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

public class LoginActivity extends AppCompatActivity {

    Button btnDoctor, btnPatient;
    ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnBack = findViewById(R.id.btnBack);
        btnDoctor = findViewById(R.id.btnDoctor);
        btnPatient = findViewById(R.id.btnPatient);

        // Back → MainActivity
        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        });

        // Doctor Login
        btnDoctor.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, DoctorLoginActivity.class))
        );

        // ⭐ Patient Login
        btnPatient.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, PatientLoginActivity.class))
        );
    }
}
