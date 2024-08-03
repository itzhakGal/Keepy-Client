package com.example.keepy.app.network;

import com.example.keepy.app.network.TokenRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("/process-token")
    Call<Void> sendToken(@Body TokenRequest tokenRequest);
}
