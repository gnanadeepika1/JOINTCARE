package com.saveetha.myjoints;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.saveetha.myjoints.databinding.ActivityTenderJointsBinding;
import com.saveetha.myjoints.joint.JointPoint;
import com.saveetha.myjoints.joint.JointPosition;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TenderJointsActivity extends AppCompatActivity {

    private ActivityTenderJointsBinding binding;
    private TextView tvSelected;
    private Button btnNext;

    private final Set<String> selected = new HashSet<>();

    List<JointPoint> bodyJoints = Arrays.asList(
            new JointPoint(JointPosition.HEAD,"Head", 0.456f, 0.152f),
            new JointPoint(JointPosition.NECK, "Neck", 0.456f, 0.248f),
            new JointPoint(JointPosition.LEFT_SHOULDER, "Left_Shoulder", 0.301f, 0.275f),
            new JointPoint(JointPosition.RIGHT_SHOULDER, "Right Shoulder", 0.612f, 0.275f),
            new JointPoint(JointPosition.LEFT_ELBOW, "Left Elbow", 0.265f, 0.370f),
            new JointPoint(JointPosition.RIGHT_ELBOW, "Right Elbow", 0.66f, 0.370f),
            new JointPoint(JointPosition.LEFT_WRIST, "Left Wrist", 0.185f, 0.438f),
            new JointPoint(JointPosition.RIGHT_WRIST, "Right Wrist", 0.73f, 0.438f),

            new JointPoint(JointPosition.LEFT_HAND_thumb_1, "Left hand THUMB finger 1", 0.256f, 0.525f),
            new JointPoint(JointPosition.LEFT_HAND_thumb_2, "Left hand THUMB finger 2", 0.251f, 0.555f),
            new JointPoint(JointPosition.RIGHT_HAND_thumb_1, "RIGHT hand THUMB finger 1", 0.70f, 0.525f),
            new JointPoint(JointPosition.RIGHT_HAND_thumb_2, "RIGHT hand THUMB FINGER 2", 0.71f, 0.557f),

            new JointPoint(JointPosition.LEFT_HAND_INDEX_1, "Left hand INDEX finger 1", 0.18f, 0.537f),
            new JointPoint(JointPosition.LEFT_HAND_INDEX_2, "Left hand INDEX finger 2", 0.152f, 0.572f),
            new JointPoint(JointPosition.LEFT_HAND_INDEX_3, "Left hand INDEX finger 3", 0.14f, 0.592f),

            new JointPoint(JointPosition.LEFT_HAND_MIDDLE_1, "Left hand MIDDLE finger 1", 0.131f, 0.526f),
            new JointPoint(JointPosition.LEFT_HAND_MIDDLE_2, "Left hand MIDDLE finger 2", 0.10f, 0.559f),
            new JointPoint(JointPosition.LEFT_HAND_MIDDLE_3, "Left hand MIDDLE finger 3", 0.07f, 0.582f),

            new JointPoint(JointPosition.LEFT_HAND_RING_1, "Left hand RING finger 1", 0.1045f, 0.51f),
            new JointPoint(JointPosition.LEFT_HAND_RING_2, "Left hand RING finger 2", 0.07f, 0.536f),
            new JointPoint(JointPosition.LEFT_HAND_RING_3, "Left hand RING finger 3", 0.045f, 0.553f),

            new JointPoint(JointPosition.LEFT_HAND_LITTLE_1, "Left hand LITTLE finger 1", 0.082f, 0.486f),
            new JointPoint(JointPosition.LEFT_HAND_LITTLE_2, "Left hand LITTLE finger 2", 0.045f, 0.504f),
            new JointPoint(JointPosition.LEFT_HAND_LITTLE_3, "Left hand LITTLE finger 3", 0.018f, 0.52f),

            new JointPoint(JointPosition.RIGHT_HAND_INDEX_1, "RIGHT hand INDEX finger 1", 0.78f, 0.536f),
            new JointPoint(JointPosition.RIGHT_HAND_INDEX_2, "RIGHT hand INDEX finger 2", 0.808f, 0.571f),
            new JointPoint(JointPosition.RIGHT_HAND_INDEX_3, "RIGHT hand INDEX finger 3", 0.835f, 0.595f),

            new JointPoint(JointPosition.RIGHT_HAND_MIDDLE_1, "RIGHT hand MIDDLE finger 1", 0.822f, 0.528f),
            new JointPoint(JointPosition.RIGHT_HAND_MIDDLE_2, "RIGHT hand MIDDLE finger 2", 0.860f, 0.553f),
            new JointPoint(JointPosition.RIGHT_HAND_MIDDLE_3, "RIGHT hand MIDDLE finger 3", 0.895f, 0.582f),

            new JointPoint(JointPosition.RIGHT_HAND_RING_1, "RIGHT hand RING finger 1", 0.848f, 0.509f),
            new JointPoint(JointPosition.RIGHT_HAND_RING_2, "RIGHT hand RING finger 2", 0.888f, 0.535f),
            new JointPoint(JointPosition.RIGHT_HAND_RING_3, "RIGHT hand RING finger 3", 0.917f, 0.555f),

            new JointPoint(JointPosition.RIGHT_HAND_LITTLE_1, "RIGHT hand LITTLE finger 1", 0.873f, 0.486f),
            new JointPoint(JointPosition.RIGHT_HAND_LITTLE_2, "RIGHT hand LITTLE finger 2", 0.916f, 0.503f),
            new JointPoint(JointPosition.RIGHT_HAND_LITTLE_3, "RIGHT hand LITTLE finger 3", 0.948f, 0.522f),

            new JointPoint(JointPosition.LEFT_LEG_INDEX_1, "Left LEG INDEX FINGER", 0.385f, 0.805f),
            new JointPoint(JointPosition.LEFT_LEG_THUMB_1, "Left LEG THUMB FINGER 1", 0.425f, 0.804f),
            new JointPoint(JointPosition.LEFT_LEG_THUMB_2, "Left LEG THUMB FINGER 2", 0.422f, 0.824f),
            new JointPoint(JointPosition.LEFT_LEG_MIDDLE_1, "Left LEG MIDDLE FINGER", 0.349f, 0.800f),
            new JointPoint(JointPosition.LEFT_LEG_RING_1, "Left LEG RING FINGER", 0.319f, 0.792f),
            new JointPoint(JointPosition.LEFT_LEG_LITTLE_1, "Left LEG LITTLE FINGER", 0.295f, 0.784f),

            new JointPoint(JointPosition.RIGHT_LEG_THUMB_1, "Right LEG THUMB FINGER1", 0.545f, 0.806f),
            new JointPoint(JointPosition.RIGHT_LEG_THUMB_2, "Right LEG THUMB FINGER2", 0.547f, 0.823f),
            new JointPoint(JointPosition.RIGHT_LEG_INDEX_1, "Right LEG INDEX FINGER", 0.583f, 0.803f),
            new JointPoint(JointPosition.RIGHT_LEG_MIDDLE_1, "Right LEG MIDDLE FINGER", 0.615f, 0.797f),
            new JointPoint(JointPosition.RIGHT_LEG_RING_1, "Right LEG RING FINGER", 0.644f, 0.793f),
            new JointPoint(JointPosition.RIGHT_LEG_LITTLE_1, "Right LEG LITTLE FINGER ", 0.674f, 0.784f),

            new JointPoint(JointPosition.LEFT_HIP, "Left Hip", 0.37f, 0.452f),
            new JointPoint(JointPosition.RIGHT_HIP, "Right Hip", 0.50f, 0.452f),
            new JointPoint(JointPosition.LEFT_KNEE, "Left Knee", 0.38f, 0.578f),
            new JointPoint(JointPosition.RIGHT_KNEE, "Right Knee", 0.54f, 0.578f),
            new JointPoint(JointPosition.LEFT_ANKLE, "Left Ankle", 0.40f, 0.675f),
            new JointPoint(JointPosition.RIGHT_ANKLE , "Right Ankle", 0.54f, 0.675f)
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTenderJointsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tvSelected = binding.tvSelectedCount;
        btnNext = binding.btnNext;

        String patientId = getIntent().getStringExtra("patient_id");

        btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(this, SwollenJointsActivity.class);
            intent.putExtra("tenderJointSelectionCount", selected.size());
            intent.putExtra("patient_id", patientId);
            startActivity(intent);
        });

        binding.root.post(this::drawDots);

        ViewCompat.setOnApplyWindowInsetsListener(binding.root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void drawDots() {
        int w = binding.dotContainer.getWidth();
        int h = binding.dotContainer.getHeight();

        for (JointPoint joint : bodyJoints) {
            ImageView dot = new ImageView(this);
            dot.setBackgroundResource(R.drawable.circle_red);
            dot.setLayoutParams(new FrameLayout.LayoutParams(
                    joint.position.getWidth(),
                    joint.position.getHeight()
            ));
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
        } else {
            selected.add(joint.name);
            dot.setBackgroundResource(R.drawable.circle_green);
        }
        tvSelected.setText("Selected: " + selected.size());
    }
}
