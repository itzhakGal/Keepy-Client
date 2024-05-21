package com.example.keepy;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("hello")
    Call<String> getHello();
}

