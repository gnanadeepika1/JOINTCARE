package com.saveetha.myjoints;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TenderJointsActivity extends AppCompatActivity {

    private int selectedCount = 0;
    private TextView tvSelected;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tender_joints);

        // ✅ SAFE FINDING (NO CRASH)
        ImageView backBtn = findViewById(R.id.back_btn);
        tvSelected = findViewById(R.id.tvSelectedCount);
        btnNext = findViewById(R.id.btnNext);
        ImageView bodyImage = findViewById(R.id.imgBody);

        // 🔴 CRASH GUARD
        if (backBtn == null || tvSelected == null || btnNext == null || bodyImage == null) {
            finish(); // prevents crash loop
            return;
        }

        backBtn.setOnClickListener(v -> finish());

        bodyImage.setOnClickListener(v -> {
            selectedCount++;
            tvSelected.setText("Selected: " + selectedCount);
            btnNext.setEnabled(selectedCount > 0);
        });

        btnNext.setOnClickListener(v -> {
            // Next screen later
//            startActivity(new Intent(this, ));
        });
    }
}
