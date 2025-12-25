package com.saveetha.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;
    private static ApiService service;

    public static final String BASE_URL = "https://2fk3pt3p-80.inc1.devtunnels.ms/";

    /**
     *
     */
//    public static final String BASE_URL = "https://3cxr1p7f-80.inc1.devtunnels.ms/";

    private RetrofitClient() {
        // no instance
    }

    public static Retrofit getInstance() {

        if (retrofit == null) {

            // Logging Interceptor
            HttpLoggingInterceptor loggingInterceptor =
                    new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }

        return retrofit;
    }

    public static ApiService getService() {
//        if(service==null) {
            service = getInstance().create(ApiService.class);
//        }
        return service;
    }
}

