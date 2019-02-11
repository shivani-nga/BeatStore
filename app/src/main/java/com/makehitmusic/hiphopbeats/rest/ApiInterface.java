package com.makehitmusic.hiphopbeats.rest;

import com.makehitmusic.hiphopbeats.model.CategoryResponse;
import com.makehitmusic.hiphopbeats.presenter.JsonResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("Dante/MHMBeats/categories.php")
    Call<CategoryResponse> getCategory();

    @GET("video/youTube.json")
    Call<JsonResponse> getYoutubeLink();

}
