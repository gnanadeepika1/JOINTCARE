package com.saveetha.myjoints;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MedicalRecordsActivity extends AppCompatActivity {

    private static final String TAG = "MedicalRecords";

    private ImageView btnBack, btnLogout;

    private CardView cardComplaints, cardComorbidities, cardDiseaseScores,
            cardMedications, cardInvestigations, cardTreatments, cardReferrals;

    private String patientId;
    private String patientName;
    private String patientEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_records);

        // header icons
        btnBack   = findViewById(R.id.btnBack);
        btnLogout = findViewById(R.id.btnLogout);

        // cards
        cardComplaints     = findViewById(R.id.cardComplaints);
        cardComorbidities  = findViewById(R.id.cardComorbidities);
        cardDiseaseScores  = findViewById(R.id.cardDiseaseScores);
        cardMedications    = findViewById(R.id.cardMedications);
        cardInvestigations = findViewById(R.id.cardInvestigations);
        cardTreatments     = findViewById(R.id.cardTreatments);
        cardReferrals      = findViewById(R.id.cardReferrals);

        // extras
        Intent intent = getIntent();
        patientId    = intent.getStringExtra("patient_id");
        patientName  = intent.getStringExtra("patient_name");
        patientEmail = intent.getStringExtra("patient_email");

        Log.d(TAG, "patientId=" + patientId +
                ", name=" + patientName +
                ", email=" + patientEmail);

        if (TextUtils.isEmpty(patientId)) {
            Toast.makeText(
                    this,
                    "Warning: no patient_id passed to MedicalRecordsActivity",
                    Toast.LENGTH_LONG
            ).show();
        }

        btnBack.setOnClickListener(v -> onBackPressed());

        // ✅ REAL LOGOUT
        btnLogout.setOnClickListener(v -> {
            SharedPreferences prefs =
                    getSharedPreferences("doctor_prefs", MODE_PRIVATE);
            prefs.edit().clear().apply();

            Intent logoutIntent =
                    new Intent(this, DoctorLoginActivity.class);
            logoutIntent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );
            startActivity(logoutIntent);
            finish();
        });

        setCardListeners();
    }

    private boolean checkPatientId() {
        if (TextUtils.isEmpty(patientId)) {
            Toast.makeText(
                    this,
                    "No patient ID found – some data may not load",
                    Toast.LENGTH_LONG
            ).show();
            return false;
        }
        return true;
    }

    private void setCardListeners() {

        cardComplaints.setOnClickListener(v -> {
            checkPatientId();
            Intent intent = new Intent(this, ComplaintHistoryActivity.class);
            intent.putExtra("patient_id", patientId);
            startActivity(intent);
        });

        cardComorbidities.setOnClickListener(v -> {
            checkPatientId();
            Intent intent =
                    new Intent(this, DoctorComorbiditiesHistoryActivity.class);
            intent.putExtra("patient_id", patientId);
            startActivity(intent);
        });

        cardDiseaseScores.setOnClickListener(v -> {
            checkPatientId();
            Intent intent = new Intent(this, DiseaseScoresActivity.class);
            intent.putExtra("patient_id", patientId);
            startActivity(intent);
        });

        cardMedications.setOnClickListener(v -> {
            checkPatientId();
            Intent intent = new Intent(this, MedicationsHistoryActivity.class);
            intent.putExtra("patient_id", patientId);
            startActivity(intent);
        });

        cardInvestigations.setOnClickListener(v -> {
            checkPatientId();
            Intent intent =
                    new Intent(this, InvestigationHistoryActivity.class);
            intent.putExtra("patient_id", patientId);
            startActivity(intent);
        });

        cardTreatments.setOnClickListener(v -> {
            Intent intent =
                    new Intent(this, TreatmentsHistoryActivity.class);
            intent.putExtra("patient_id", patientId);
            startActivity(intent);
        });

        cardReferrals.setOnClickListener(v -> {
            checkPatientId();
            Intent intent =
                    new Intent(this, ReferralHistoryActivity.class);
            intent.putExtra("patient_id", patientId);
            startActivity(intent);
        });
    }
}
