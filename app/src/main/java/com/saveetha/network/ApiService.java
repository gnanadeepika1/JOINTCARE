package com.saveetha.network;

import com.saveetha.myjoints.PainResponse;
import com.saveetha.myjoints.data.DiseaseScores;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @POST("JOINTCARE/save_graph.php")
    Call<Map<String, Object>> insertDiseaseScore(@Body Map<String, Object> request);

    @GET("jointcare/get_graph.php")
    Call<DiseaseScores> getGraph(@Query("patient_id")String patientId);

    @POST("jointcare/save_daily_pain.php")
    Call<Map<String, Object>> saveDailyPainValue(@Body Map<String, Object> request);

    @GET("jointcare/get_daily_pain_value.php")
    Call<PainResponse> getPainValues(@Query("user_id")String patientId);

}
