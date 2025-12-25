package com.saveetha.myjoints;

import java.util.List;

public class PainResponse {

    private boolean status;
    private String user_id;
    private int count;

    List<PainEntry> data;

    public boolean isStatus() {
        return status;
    }

    public String getUser_id() {
        return user_id;
    }

    public int getCount() {
        return count;
    }

    public List<PainEntry> getData() {
        return data;
    }
}
