package com.saveetha.myjoints;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.saveetha.myjoints.databinding.ActivityAssessmentBinding;
import com.saveetha.myjoints.util.Static;
import com.saveetha.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Map;

public class AssessmentActivity extends AppCompatActivity {

    ActivityAssessmentBinding binding;

    float pga = 0f;
    float ea = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAssessmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageView btnBack = findViewById(R.id.btnBack);
        SeekBar seekPatient = findViewById(R.id.seekPatient);
        SeekBar seekEvaluator = findViewById(R.id.seekEvaluator);
        TextView tvPatientValue = findViewById(R.id.tvPatientValue);
        TextView tvEvaluatorValue = findViewById(R.id.tvEvaluatorValue);
        EditText edtCrp = findViewById(R.id.edtCrp);
        Button btnCalculate = findViewById(R.id.btnCalculate);

        btnBack.setOnClickListener(v -> finish());

        float maxValue = 10.0f;

        seekPatient.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pga = (progress / 100f) * maxValue;
                tvPatientValue.setText(String.format("Value: %.1f", pga));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekEvaluator.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ea = (progress / 100f) * maxValue;
                tvEvaluatorValue.setText(String.format("Value: %.1f", ea));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        Intent intent = getIntent();
        int tjc = intent.getIntExtra("TJC", 0);
        int sjc = intent.getIntExtra("SJC", 0);
        String patientId = intent.getStringExtra("patient_id");

        btnCalculate.setOnClickListener(v -> {

            String crpStr = edtCrp.getText().toString().trim();

            if (crpStr.isEmpty()) {
                Static.toast(AssessmentActivity.this, "Enter CRP value");
                return;
            }

            float crp = Float.parseFloat(crpStr);

            saveValue(patientId, tjc, sjc, pga, ea, crp);
        });

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // ================= FIXED API CALL =================
    private void saveValue(String patientId, int tjc, int sjc, float pga, float ea, float crp) {

        AlertDialog progress = Static.showProgress(this);
        progress.show();

        RetrofitClient.getService()
                .insertDiseaseScore(patientId, tjc, sjc, pga, ea, crp)
                .enqueue(new Callback<Map<String, Object>>() {

                    @Override
                    public void onResponse(Call<Map<String, Object>> call,
                                           Response<Map<String, Object>> response) {
                        progress.dismiss();

                        if (response.isSuccessful() && response.body() != null) {

                            new AlertDialog.Builder(AssessmentActivity.this)
                                    .setTitle("Success")
                                    .setMessage(response.body().get("message").toString())
                                    .setCancelable(false)
                                    .setPositiveButton("OK", (dialog, which) -> {
                                        Intent i = new Intent(
                                                AssessmentActivity.this,
                                                MedicalRecordsActivity.class
                                        );
                                        i.putExtra("patient_id", patientId);
                                        startActivity(i);
                                        finish();
                                    })
                                    .show();

                        } else {
                            Static.showErrorResponse(
                                    AssessmentActivity.this,
                                    response.errorBody()
                            );
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        progress.dismiss();
                        Static.showError(AssessmentActivity.this, t.getMessage());
                    }
                });
    }
}