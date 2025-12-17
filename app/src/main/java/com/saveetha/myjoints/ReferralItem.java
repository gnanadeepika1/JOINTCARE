package com.saveetha.myjoints;

public class ReferralItem {
    private final String message;
    private final String patientId;

    public ReferralItem(String message, String patientId) {
        this.message = message;
        this.patientId = patientId;
    }

    public String getMessage() {
        return message;
    }

    public String getPatientId() {
        return patientId;
    }
}
