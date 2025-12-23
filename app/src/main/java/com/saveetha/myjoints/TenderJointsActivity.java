package com.saveetha.myjoints;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TenderJointsActivity extends AppCompatActivity {

    private int selectedCount = 0;
    private TextView tvSelected;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tender_joints);

        ImageView backBtn = findViewById(R.id.back_btn);
        tvSelected = findViewById(R.id.tvSelectedCount);
        btnNext = findViewById(R.id.btnNext);
        ImageView bodyImage = findViewById(R.id.imgBody);

        // ✅ NORMAL BACK
        backBtn.setOnClickListener(v -> finish());

        bodyImage.setOnClickListener(v -> {
            selectedCount++;
            tvSelected.setText("Selected: " + selectedCount);
            btnNext.setEnabled(selectedCount > 0);
        });

        btnNext.setOnClickListener(v -> {
            startActivity(new Intent(this, SwollenJointsActivity.class));
        });
    }
}
