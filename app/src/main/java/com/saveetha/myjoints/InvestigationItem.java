package com.saveetha.myjoints;

import java.util.List;

public class InvestigationItem {
    private final String title;
    private final List<String> details;

    public InvestigationItem(String title, List<String> details) {
        this.title = title;
        this.details = details;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getDetails() {
        return details;
    }
}
