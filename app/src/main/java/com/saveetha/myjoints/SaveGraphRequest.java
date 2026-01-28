package com.saveetha.myjoints;
public class SaveGraphRequest {

    private String patient_id;
    private int tjc;
    private int sjc;
    private float pga;
    private float ea;
    private float crp;

    public SaveGraphRequest(String patient_id, int tjc, int sjc,
                            float pga, float ea, float crp) {
        this.patient_id = patient_id;
        this.tjc = tjc;
        this.sjc = sjc;
        this.pga = pga;
        this.ea = ea;
        this.crp = crp;
    }
}
