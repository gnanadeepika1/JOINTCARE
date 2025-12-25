package com.saveetha.network;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("insert_disease_score.php")
    Call<Map<String, Object>> insertDiseaseScore(@Body Map<String, Object> request);

}
