package com.saveetha.myjoints;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.saveetha.myjoints.databinding.ActivityAssessmentBinding;
import com.saveetha.myjoints.util.Static;
import com.saveetha.network.ApiService;
import com.saveetha.network.RetrofitClient;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class AssessmentActivity extends AppCompatActivity {

    ActivityAssessmentBinding binding;
    private static final String PREFS_NAME    = "doctor_prefs";
    private static final String KEY_DOCTOR_ID = "doctor_id";
    private SharedPreferences prefs;

    float seek1;
    float seek2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAssessmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

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
                float value = (progress / 100f) * maxValue;
                seek1 = value;
                tvPatientValue.setText(String.format("Value: %.1f", value));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekEvaluator.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float value = (progress / 100f) * maxValue;
                seek2 = value;
                tvEvaluatorValue.setText(String.format("Value: %.1f", value));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        ViewCompat.setOnApplyWindowInsetsListener(binding.root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
            );
            return insets;
        });

        Intent tenderJointValue = getIntent();
        int tenderJoint = tenderJointValue.getIntExtra("tenderJointSelectionCount", 0);
        int swollenJoint = tenderJointValue.getIntExtra("swollenJointSelectionCount", 0);
        AtomicReference<String> crp = new AtomicReference<>("");
        btnCalculate.setOnClickListener(v -> {
             crp.set(edtCrp.getText().toString().trim());
            // Calculation logic can be added here later
        });

        String savedDoctorId = prefs.getString(KEY_DOCTOR_ID, "");

        saveValue(savedDoctorId, tenderJoint, crp.get());
    }

    private void saveValue(String id, float value1, String value2) {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Map<String, Object> request = Map.of("patient_id", id, "pga", value1, "crp", value2);

        AlertDialog progress = Static.showProgress(this);
        try {
            progress.show();
            Response<Map<String, Object>> res =  apiService.insertDiseaseScore(request);
            if(res.isSuccessful()) {
                Static.showResponse(this, "Data inserted successfully");
            } else {
                Static.showErrorResponse(this, res.errorBody());
            }
        } catch (Exception e) {
            Static.showError(this, e.getMessage());
        }
        progress.dismiss();
    }



}
