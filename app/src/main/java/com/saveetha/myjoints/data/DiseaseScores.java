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
        private float pga;
        private float crp;
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
