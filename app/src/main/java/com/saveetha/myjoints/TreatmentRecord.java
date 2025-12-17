package com.saveetha.myjoints;

public class TreatmentRecord {
    private String dose;
    private String route;
    private String frequency;
    private String duration;
    private String patientId;

    public TreatmentRecord(String dose, String route, String frequency,
                           String duration, String patientId) {
        this.dose = dose;
        this.route = route;
        this.frequency = frequency;
        this.duration = duration;
        this.patientId = patientId;
    }

    public String getDose() {
        return dose;
    }

    public String getRoute() {
        return route;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getDuration() {
        return duration;
    }

    public String getPatientId() {
        return patientId;
    }
}
