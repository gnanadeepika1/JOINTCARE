package com.example.myjoints;

public class Complaint {
    private final String title;
    private final String dateIso;

    public Complaint(String title, String dateIso) {
        this.title = title;
        this.dateIso = dateIso;
    }

    public String getTitle() { return title; }
    public String getDateIso() { return dateIso; }
}
