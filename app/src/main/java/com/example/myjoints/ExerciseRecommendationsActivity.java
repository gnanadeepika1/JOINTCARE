package com.example.myjoints;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

/**
 * Exercise recommendations screen — simple inflater-based list.
 */
public class ExerciseRecommendationsActivity extends AppCompatActivity {

    private LinearLayout llExercises;
    private ImageView backBtn;

    private static class Exercise {
        final String emoji;
        final String title;
        final String subtitle;
        Exercise(String emoji, String title, String subtitle) {
            this.emoji = emoji;
            this.title = title;
            this.subtitle = subtitle;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_recommendations);

        llExercises = findViewById(R.id.llExercises);
        backBtn = findViewById(R.id.back_btn);

        backBtn.setOnClickListener(v -> onBackPressed());

        List<Exercise> data = new ArrayList<>();
        data.add(new Exercise("🚶", "Walking", "Gentle, low-impact daily walks"));
        data.add(new Exercise("🏊", "Swimming", "Water aerobics or swimming laps"));
        data.add(new Exercise("🧘", "Yoga", "Gentle stretching and flexibility"));
        data.add(new Exercise("🏋️‍♂️", "Strength Training", "Light weights or resistance bands"));
        data.add(new Exercise("🚴", "Cycling", "Stationary or outdoor cycling"));

        for (Exercise e : data) {
            View item = getLayoutInflater().inflate(R.layout.item_exercise, llExercises, false);
            TextView tvEmoji = item.findViewById(R.id.tvEmoji);
            TextView tvTitle = item.findViewById(R.id.tvTitle);
            TextView tvSubtitle = item.findViewById(R.id.tvSubtitle);

            tvEmoji.setText(e.emoji);
            tvTitle.setText(e.title);
            tvSubtitle.setText(e.subtitle);

            llExercises.addView(item);
        }
    }
}
