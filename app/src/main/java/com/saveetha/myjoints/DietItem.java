package com.saveetha.myjoints;

import java.util.List;

public class DietItem {
    private final String emoji;
    private final String title;
    private final List<String> bullets;

    public DietItem(String emoji, String title, List<String> bullets) {
        this.emoji = emoji;
        this.title = title;
        this.bullets = bullets;
    }

    public String getEmoji() { return emoji; }
    public String getTitle() { return title; }
    public List<String> getBullets() { return bullets; }
}
