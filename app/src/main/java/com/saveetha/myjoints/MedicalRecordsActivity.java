package com.saveetha.myjoints;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MedicalRecordsActivity extends AppCompatActivity {

    private static final String TAG = "MedicalRecords";

    private ImageView btnBack, btnSettings, btnLogout;
    private CardView cardComplaints, cardComorbidities, cardDiseaseScores,
            cardMedications, cardInvestigations, cardTreatments, cardReferrals;

    // current patient
    private String patientId;
    private String patientName;
    private String patientEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_records);

        // header icons
        btnBack     = findViewById(R.id.btnBack);
        btnSettings = findViewById(R.id.btnSettings);
        btnLogout   = findViewById(R.id.btnLogout);

        // cards
        cardComplaints     = findViewById(R.id.cardComplaints);
        cardComorbidities  = findViewById(R.id.cardComorbidities);
        cardDiseaseScores  = findViewById(R.id.cardDiseaseScores);
        cardMedications    = findViewById(R.id.cardMedications);
        cardInvestigations = findViewById(R.id.cardInvestigations);
        cardTreatments     = findViewById(R.id.cardTreatments);
        cardReferrals      = findViewById(R.id.cardReferrals);

        // extras from MyPatientsActivity
        Intent intent = getIntent();
        patientId    = intent.getStringExtra("patient_id");
        patientName  = intent.getStringExtra("patient_name");
        patientEmail = intent.getStringExtra("patient_email");

        Log.d(TAG, "onCreate patientId = " + patientId +
                ", name = " + patientName + ", email = " + patientEmail);

        if (TextUtils.isEmpty(patientId)) {
            Toast.makeText(this,
                    "Warning: no patient_id passed to MedicalRecordsActivity",
                    Toast.LENGTH_LONG).show();
        }

        // header actions
        btnBack.setOnClickListener(v -> onBackPressed());
        btnSettings.setOnClickListener(
                v -> Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
        );
        btnLogout.setOnClickListener(
                v -> Toast.makeText(this, "Logout clicked", Toast.LENGTH_SHORT).show()
        );

        setCardListeners();
    }

    /** Just shows a warning toast if patientId is missing. */
    private boolean checkPatientId() {
        if (TextUtils.isEmpty(patientId)) {
            Toast.makeText(this,
                    "No patient ID found – some data may not load",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void setCardListeners() {

        // Complaints
        cardComplaints.setOnClickListener(v -> {
            checkPatientId();
            Intent intent = new Intent(this, ComplaintHistoryActivity.class);
            intent.putExtra("patient_id", patientId);
            startActivity(intent);
        });

        // Comorbidities
        cardComorbidities.setOnClickListener(v -> {
            checkPatientId();
            Intent intent = new Intent(this, DoctorComorbiditiesHistoryActivity.class);
            intent.putExtra("patient_id", patientId);
            startActivity(intent);
        });

        // ✅ Disease Scores → DiseaseScoresActivity
        cardDiseaseScores.setOnClickListener(v -> {
            checkPatientId();
            Intent intent = new Intent(this, DiseaseScoresActivity.class);
            intent.putExtra("patient_id", patientId);
            startActivity(intent);
        });

        // Medications
        cardMedications.setOnClickListener(v -> {
            checkPatientId();
            Intent intent = new Intent(this, MedicationsHistoryActivity.class);
            intent.putExtra("patient_id", patientId);
            startActivity(intent);
        });

        // Investigations
        cardInvestigations.setOnClickListener(v -> {
            checkPatientId();
            Intent intent = new Intent(this, InvestigationHistoryActivity.class);
            intent.putExtra("patient_id", patientId);
            startActivity(intent);
        });

        // Treatments
        cardTreatments.setOnClickListener(v -> {
            Log.d(TAG, "Treatments card clicked, patientId = " + patientId);
            Intent intent = new Intent(this, TreatmentsHistoryActivity.class);
            intent.putExtra("patient_id", patientId);
            startActivity(intent);
        });

        // Referrals
        cardReferrals.setOnClickListener(v -> {
            checkPatientId();
            Intent intent = new Intent(this, ReferralHistoryActivity.class);
            intent.putExtra("patient_id", patientId);
            startActivity(intent);
        });
    }
}
