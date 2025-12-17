package com.saveetha.myjoints.adapters.activity_disease_scores;

public class DiseaseScore {

    private int sdai;
    private double das28Crp;
    private String date;

    public DiseaseScore(int sdai, double das28Crp, String date) {
        this.sdai = sdai;
        this.das28Crp = das28Crp;
        this.date = date;
    }

    public int getSdai() {
        return sdai;
    }

    public double getDas28Crp() {
        return das28Crp;
    }

    public String getDate() {
        return date;
    }
}

