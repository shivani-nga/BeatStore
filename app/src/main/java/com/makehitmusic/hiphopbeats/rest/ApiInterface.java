package com.makehitmusic.hiphopbeats.rest;

import com.makehitmusic.hiphopbeats.model.CategoryResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("categories.php")
    Call<CategoryResponse> getCategory();

}
