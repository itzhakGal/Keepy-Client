package com.example.keepy;

import com.example.keepy.app.TokenRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("/process-token")
    Call<Void> sendToken(@Body TokenRequest tokenRequest);
}
