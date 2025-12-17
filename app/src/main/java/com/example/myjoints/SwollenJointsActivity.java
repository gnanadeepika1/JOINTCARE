package com.example.myjoints;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SwollenJointsActivity extends AppCompatActivity {

    private TextView tvSelectedCount;
    private int selectedCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swollen_joints);

        ImageView btnBack = findViewById(R.id.btnBack);
        Button btnNext = findViewById(R.id.btnNext);
        tvSelectedCount = findViewById(R.id.tvSelectedCount);

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Next button → Assessment page
        btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(
                    SwollenJointsActivity.this,
                    AssessmentActivity.class
            );
            startActivity(intent);
        });

        updateSelectedCount();
    }

    private void updateSelectedCount() {
        tvSelectedCount.setText("Selected: " + selectedCount);
    }
}
