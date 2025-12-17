package com.saveetha.myjoints;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * PatientDashboardActivity
 * Uses activity_patient_dashboard.xml and item_card.xml.
 */
public class PatientDashboardActivity extends AppCompatActivity {

    private LinearLayout containerCards;
    private ImageView btnBack;
    private ImageView btnSettings;
    private ImageView btnLogout;

    // Header + profile views
    private TextView tvGreeting;
    private TextView tvSubtitle;
    private TextView tvProfileName;
    private TextView tvProfileEmail;

    private final String[] labels = {
            "Complaints",
            "Comorbidities",
            "Daily Assessment",
            "Medications",
            "Investigations",
            "Treatments",
            "Diet",
            "Exercises",
            "Consult Now"
    };

    private final int[] icons = {
            R.drawable.ic_complaints,
            R.drawable.ic_comorbidities,
            R.drawable.ic_daily_assessment,
            R.drawable.ic_medications,
            R.drawable.ic_investigations,
            R.drawable.ic_treatments,
            R.drawable.ic_diet,
            R.drawable.ic_exercises,
            R.drawable.ic_consult_now
    };

    private static final String PREFS_NAME        = "patient_prefs";
    private static final String KEY_PATIENT_ID    = "patient_id";
    private static final String KEY_PATIENT_NAME  = "patient_name";
    private static final String KEY_PATIENT_EMAIL = "patient_email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_dashboard);

        containerCards = findViewById(R.id.containerCards);
        btnBack        = findViewById(R.id.btnBack);
        btnSettings    = findViewById(R.id.btnSettings);
        btnLogout      = findViewById(R.id.btnLogout);

        // Header/profile views
        tvGreeting     = findViewById(R.id.tvGreeting);
        tvSubtitle     = findViewById(R.id.tvSubtitle);
        tvProfileName  = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);

        // Load patient info from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String patientId    = prefs.getString(KEY_PATIENT_ID, "");
        String patientName  = prefs.getString(KEY_PATIENT_NAME, "");
        String patientEmail = prefs.getString(KEY_PATIENT_EMAIL, "");

        // Optional override from Intent
        Intent intent = getIntent();
        if (intent != null) {
            String extraName  = intent.getStringExtra("patient_name");
            String extraEmail = intent.getStringExtra("patient_email");
            if (!TextUtils.isEmpty(extraName)) {
                patientName = extraName;
            }
            if (!TextUtils.isEmpty(extraEmail)) {
                patientEmail = extraEmail;
            }
        }

        // ✅ Fallbacks: ONLY if nothing is stored
        if (TextUtils.isEmpty(patientName)) {
            patientName = "Patient";           // no more using patientId as name
        }
        if (TextUtils.isEmpty(patientEmail)) {
            patientEmail = "patient@example.com";
        }

        // Header text
        tvGreeting.setText("Hi, " + patientName);
        tvSubtitle.setText(patientEmail);

        // Profile row (icon + name + email)
        tvProfileName.setText(patientName);
        tvProfileEmail.setText(patientEmail);

        btnBack.setOnClickListener(v -> onBackPressed());

        btnSettings.setOnClickListener(v -> {
            try {
                Intent i = new Intent(PatientDashboardActivity.this, SettingsActivity.class);
                startActivity(i);
            } catch (Exception e) {
                Toast.makeText(
                        PatientDashboardActivity.this,
                        "Settings clicked (implement SettingsActivity)",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Intent intentLogout =
                    new Intent(PatientDashboardActivity.this, PatientLoginActivity.class);
            intentLogout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentLogout);
            finish();
        });

        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < labels.length; i++) {
            final String label = labels[i];
            final int iconRes = icons[i];

            View card = inflater.inflate(R.layout.item_card, containerCards, false);
            ImageView cardImage = card.findViewById(R.id.cardImage);
            TextView cardText   = card.findViewById(R.id.cardText);
            cardText.setText(label);

            try {
                cardImage.setImageResource(iconRes);
            } catch (Exception e) {
                cardImage.setImageResource(R.drawable.ic_placeholder);
            }

            // ⭐ CLICK EVENTS ⭐
            card.setOnClickListener(v -> {
                if ("Complaints".equalsIgnoreCase(label)) {
                    startActivity(new Intent(this, ComplaintsActivity.class));
                } else if ("Comorbidities".equalsIgnoreCase(label)) {
                    startActivity(new Intent(this, ComorbiditiesActivity.class));
                } else if ("Daily Assessment".equalsIgnoreCase(label)) {
                    startActivity(new Intent(this, DailySelfAssessmentActivity.class));
                } else if ("Medications".equalsIgnoreCase(label)) {
                    startActivity(new Intent(this, MedicationsActivity.class));
                } else if ("Investigations".equalsIgnoreCase(label)) {
                    startActivity(new Intent(this, InvestigationsActivity.class));
                } else if ("Treatments".equalsIgnoreCase(label)) {
                    startActivity(new Intent(this, TreatmentsActivity.class));
                } else if ("Diet".equalsIgnoreCase(label)) {
                    startActivity(new Intent(this, DietRecommendationsActivity.class));
                } else if ("Exercises".equalsIgnoreCase(label)) {
                    startActivity(new Intent(this, ExerciseRecommendationsActivity.class));
                } else if ("Consult Now".equalsIgnoreCase(label)) {
                    startActivity(new Intent(this, ConsultNowActivity.class));
                } else {
                    Toast.makeText(this, "Clicked: " + label, Toast.LENGTH_SHORT).show();
                }
            });

            containerCards.addView(card);
        }
    }
}
