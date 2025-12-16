package com.example.myjoints;

import java.util.List;

public class Investigation {
    private final String title;
    private final List<String> details;

    public Investigation(String title, List<String> details) {
        this.title = title;
        this.details = details;
    }

    public String getTitle() { return title; }
    public List<String> getDetails() { return details; }
}
