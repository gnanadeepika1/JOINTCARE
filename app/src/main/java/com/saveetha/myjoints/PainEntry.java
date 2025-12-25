package com.saveetha.myjoints;

import com.google.gson.annotations.SerializedName;

public class PainEntry {
    @SerializedName("pain")
    private final int value;
    @SerializedName("date")
    private final String isoDate;

    public PainEntry(int value, String isoDate) {
        this.value = value;
        this.isoDate = isoDate;
    }

    public int getValue() { return value; }
    public String getIsoDate() { return isoDate; }
}
