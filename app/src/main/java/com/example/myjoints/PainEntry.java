package com.example.myjoints;

public class PainEntry {
    private final int value;
    private final String isoDate;

    public PainEntry(int value, String isoDate) {
        this.value = value;
        this.isoDate = isoDate;
    }

    public int getValue() { return value; }
    public String getIsoDate() { return isoDate; }
}
