package com.example.myjoints;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

/**
 * Diet recommendations screen
 */
public class DietRecommendationsActivity extends AppCompatActivity {

    private LinearLayout llDietContainer;
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_recommendations);

        llDietContainer = findViewById(R.id.llDietContainer);
        backBtn = findViewById(R.id.back_btn);

        backBtn.setOnClickListener(v -> onBackPressed());

        // Build sample data (emoji included in code)
        List<DietItem> data = new ArrayList<>();
        data.add(new DietItem("🐟", "Fatty Fish",
                Arrays.asList("Salmon, Mackerel, Sardines")));
        data.add(new DietItem("🥜", "Nuts & Seeds",
                Arrays.asList("Walnuts, Almonds, Flaxseeds, Chia Seeds")));
        data.add(new DietItem("🫐", "Berries",
                Arrays.asList("Blueberries, Strawberries, Raspberries")));
        data.add(new DietItem("🥬", "Leafy Greens",
                Arrays.asList("Spinach, Kale, Collard Greens")));
        data.add(new DietItem("🫒", "Olive Oil",
                Arrays.asList("Extra Virgin Olive Oil")));
        data.add(new DietItem("🌾", "Whole Grains",
                Arrays.asList("Oats, Brown Rice, Quinoa")));

        // Inflate each card into llDietContainer
        for (DietItem item : data) {
            View card = getLayoutInflater().inflate(R.layout.item_diet, llDietContainer, false);

            TextView tvEmoji = card.findViewById(R.id.tvEmoji);
            TextView tvTitle = card.findViewById(R.id.tvTitle);
            LinearLayout llBullets = card.findViewById(R.id.llBullets);

            tvEmoji.setText(item.getEmoji());
            tvTitle.setText(item.getTitle());

            llBullets.removeAllViews();
            for (String b : item.getBullets()) {
                // create bullet row
                LinearLayout row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);

                LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                rowParams.topMargin = dpToPx(6);
                row.setLayoutParams(rowParams);

                View dot = new View(this);
                LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(
                        dpToPx(8),
                        dpToPx(8));
                dotParams.leftMargin = dpToPx(2);
                dotParams.topMargin = dpToPx(4);
                dot.setLayoutParams(dotParams);
                // circle_blue should be a drawable resource (shape xml) that draws a blue circle
                dot.setBackgroundResource(R.drawable.circle_blue);

                TextView tv = new TextView(this);
                LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                tvParams.leftMargin = dpToPx(10);
                tv.setLayoutParams(tvParams);
                tv.setText(b);
                tv.setTextSize(15f);
                tv.setTextColor(0xFF000000);

                row.addView(dot);
                row.addView(tv);

                llBullets.addView(row);
            }

            llDietContainer.addView(card);
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    // Simple model class used in this Activity (could be moved to its own file)
    public static class DietItem {
        private final String emoji;
        private final String title;
        private final List<String> bullets;

        public DietItem(String emoji, String title, List<String> bullets) {
            this.emoji = emoji;
            this.title = title;
            this.bullets = bullets;
        }

        public String getEmoji() {
            return emoji;
        }

        public String getTitle() {
            return title;
        }

        public List<String> getBullets() {
            return bullets;
        }
    }
}
