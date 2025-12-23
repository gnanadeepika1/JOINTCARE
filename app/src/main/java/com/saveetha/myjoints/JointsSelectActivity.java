package com.saveetha.myjoints;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.saveetha.myjoints.databinding.ActivityJointsSelectBinding;
import com.saveetha.myjoints.joint.JointPoint;
import com.saveetha.myjoints.joint.JointPosition;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JointsSelectActivity extends AppCompatActivity {

    ActivityJointsSelectBinding binding;
    private int selectedCount = 0;
    private TextView tvCount;

    private final Set<String> selected = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJointsSelectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tvCount = binding.tvCount;

        EdgeToEdge.enable(this);

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

        binding.root.post(() -> drawDots());

    }

    List<JointPoint> bodyJoints = Arrays.asList(
            new JointPoint(JointPosition.HEAD,"Head", 0.456f, 0.13f),
            new JointPoint(JointPosition.NECK, "Neck", 0.456f, 0.21f),

            new JointPoint(JointPosition.LEFT_SHOULDER, "Left_Shoulder", 0.301f, 0.245f),
            new JointPoint(JointPosition.RIGHT_SHOULDER, "Right Shoulder", 0.612f, 0.245f),

            new JointPoint(JointPosition.LEFT_ELBOW, "Left Elbow", 0.30f, 0.35f),
            new JointPoint(JointPosition.RIGHT_ELBOW, "Right Elbow", 0.70f, 0.35f),

            new JointPoint(JointPosition.LEFT_WRIST, "Left Wrist", 0.25f, 0.48f),
            new JointPoint(JointPosition.RIGHT_WRIST, "Right Wrist", 0.75f, 0.48f),

            new JointPoint(JointPosition.LEFT_HIP, "Left Hip", 0.47f, 0.52f),
            new JointPoint(JointPosition.RIGHT_HIP, "Right Hip", 0.53f, 0.52f),

            new JointPoint(JointPosition.LEFT_KNEE, "Left Knee", 0.47f, 0.68f),
            new JointPoint(JointPosition.RIGHT_KNEE, "Right Knee", 0.53f, 0.68f),

            new JointPoint(JointPosition.LEFT_ANKLE, "Left Ankle", 0.48f, 0.82f),
            new JointPoint(JointPosition.RIGHT_ANKLE , "Right Ankle", 0.52f, 0.82f)
    );

    private void drawDots() {
        int w = binding.root.getWidth();
        int h = binding.root.getHeight();

        for (JointPoint joint : bodyJoints) {
            ImageView dot = new ImageView(this);
            dot.setBackgroundResource(R.drawable.circle_red);
            dot.setLayoutParams(new FrameLayout.LayoutParams(joint.position.getWidth(), joint.position.getHeight()));

            dot.setX(joint.x * w - 12);
            dot.setY(joint.y * h - 12);

            dot.setOnClickListener(v -> toggle(dot, joint));

            binding.dotContainer.addView(dot);
        }
    }

    private void toggle(ImageView dot, JointPoint joint) {

        if (selected.contains(joint.name)) {
            selected.remove(joint.name);
            dot.setBackgroundResource(R.drawable.circle_red);
            dot.setImageDrawable(null);
        } else {
            selected.add(joint.name);
            dot.setBackgroundResource(R.drawable.circle_green);
//            dot.setImageResource(R.drawable.ic_tick);
        }

        binding.tvCount.setText("Selected: " + selected.size());
    }




}