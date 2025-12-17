package com.saveetha.myjoints;

import java.util.List;

public class Medication {
    private final String title;        // e.g. "Prescription"
    private final List<String> items;  // each bullet line

    public Medication(String title, List<String> items) {
        this.title = title;
        this.items = items;
    }

    public String getTitle() { return title; }
    public List<String> getItems() { return items; }
}
