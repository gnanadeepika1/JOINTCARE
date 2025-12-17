package com.saveetha.myjoints;

public class MedicationHistoryItem {

    private final String name;
    private final String dose;
    private final String period;

    public MedicationHistoryItem(String name, String dose, String period) {
        this.name = name;
        this.dose = dose;
        this.period = period;
    }

    public String getName() {
        return name;
    }

    public String getDose() {
        return dose;
    }

    public String getPeriod() {
        return period;
    }
}
