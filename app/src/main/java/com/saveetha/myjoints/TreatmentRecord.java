package com.saveetha.myjoints;

public class TreatmentRecord {

    private String medicationName;
    private String dose;
    private String route;
    private String frequencyNumber;
    private String frequencyText;
    private String duration;
    private String patientId;

    public TreatmentRecord(
            String medicationName,
            String dose,
            String route,
            String frequencyNumber,
            String frequencyText,
            String duration,
            String patientId
    ) {
        this.medicationName = medicationName;
        this.dose = dose;
        this.route = route;
        this.frequencyNumber = frequencyNumber;
        this.frequencyText = frequencyText;
        this.duration = duration;
        this.patientId = patientId;
    }

    public String getMedicationName() { return medicationName; }
    public String getDose() { return dose; }
    public String getRoute() { return route; }
    public String getFrequencyNumber() { return frequencyNumber; }
    public String getFrequencyText() { return frequencyText; }
    public String getDuration() { return duration; }
    public String getPatientId() { return patientId; }
}
