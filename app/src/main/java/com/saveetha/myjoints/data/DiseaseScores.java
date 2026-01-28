package com.saveetha.myjoints.data;

import java.util.List;

public class DiseaseScores {

    private String status;
    private int count;
    private List<Data> data;

    public String getStatus() {
        return status;
    }

    public int getCount() {
        return count;
    }

    public List<Data> getData() {
        return data;
    }

    public static class Data {

        private int id;
        private Integer user_id;
        private String patient_id;

        // ✅ RAW VALUES (ADDED – REQUIRED)
        private int tjc;
        private int sjc;
        private float ea;

        private float pga;
        private float crp;

        // ✅ CALCULATED VALUES (ADDED – REQUIRED)
        private float sdai;
        private double das28_crp;

        private String created_at;

        public int getId() {
            return id;
        }

        public Integer getUser_id() {
            return user_id;
        }

        public String getPatient_id() {
            return patient_id;
        }

        // ✅ NEW GETTERS
        public int getTjc() {
            return tjc;
        }

        public int getSjc() {
            return sjc;
        }

        public float getEa() {
            return ea;
        }

        public float getSdai() {
            return sdai;
        }

        public double getDas28_crp() {
            return das28_crp;
        }

        // EXISTING GETTERS (UNCHANGED)
        public float getPga() {
            return pga;
        }

        public float getCrp() {
            return crp;
        }

        public String getCreated_at() {
            return created_at;
        }
    }
}
