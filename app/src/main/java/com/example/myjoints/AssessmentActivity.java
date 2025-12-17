package com.example.myjoints;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AssessmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment);

        ImageView btnBack = findViewById(R.id.btnBack);
        SeekBar seekPatient = findViewById(R.id.seekPatient);
        SeekBar seekEvaluator = findViewById(R.id.seekEvaluator);
        TextView tvPatientValue = findViewById(R.id.tvPatientValue);
        TextView tvEvaluatorValue = findViewById(R.id.tvEvaluatorValue);
        EditText edtCrp = findViewById(R.id.edtCrp);
        Button btnCalculate = findViewById(R.id.btnCalculate);

        btnBack.setOnClickListener(v -> finish());

        seekPatient.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvPatientValue.setText(String.valueOf(progress));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekEvaluator.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvEvaluatorValue.setText(String.valueOf(progress));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnCalculate.setOnClickListener(v -> {
            String crp = edtCrp.getText().toString().trim();
            // Calculation logic can be added here later
        });
    }
}
