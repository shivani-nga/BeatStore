package com.makehitmusic.hiphopbeats.rest;

import com.makehitmusic.hiphopbeats.model.CategoryResponse;
import com.makehitmusic.hiphopbeats.presenter.JsonResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("categories.php")
    Call<CategoryResponse> getCategory();

    @GET("products.php")
    Call<CategoryResponse> getBeatsDetails(@Query("category_id") int categoryId,
                                           @Query("userid") int userId,
                                           @Query("latest") String latestBeats);

    @GET("products.php")
    Call<CategoryResponse> getBeatsDetails(@Query("userid") int userId,
                                           @Query("latest") String latestBeats);

    @GET("products.php")
    Call<CategoryResponse> getBeatsDetails(@Query("purchase") String purchasedBeats);

    @GET("video/youTube.json")
    Call<JsonResponse> getYoutubeLink();

}
