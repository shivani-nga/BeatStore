package com.makehitmusic.hiphopbeats.rest;

import com.makehitmusic.hiphopbeats.model.CategoryResponse;
import com.makehitmusic.hiphopbeats.model.LoginRequest;
import com.makehitmusic.hiphopbeats.model.LoginResponse;
import com.makehitmusic.hiphopbeats.presenter.JsonResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("mhmbeats/categories.php")
    Call<CategoryResponse> getCategory(@Query("android") String trueAndroid);

    @GET("mhmbeats/products.php")
    Call<CategoryResponse> getBeatsDetails(@Query("category_id") int categoryId,
                                           @Query("userid") int userId,
                                           @Query("latest") String latestBeats,
                                           @Query("android") String trueAndroid);

    @GET("mhmbeats/products.php")
    Call<CategoryResponse> getBeatsDetails(@Query("userid") int userId,
                                           @Query("latest") String latestBeats,
                                           @Query("android") String trueAndroid);

    @GET("mhmbeats/products.php")
    Call<CategoryResponse> getBeatsDetails(@Query("purchase") String purchasedBeats,
                                           @Query("android") String trueAndroid);

    @GET("mhmbeats/producers.php")
    Call<CategoryResponse> getProducers(@Query("android") String trueAndroid);

    @GET("mhmbeats/products.php")
    Call<CategoryResponse> getProducersDetails(@Query("producer_id") int producerId,
                                               @Query("userid") int userId,
                                               @Query("latest") String latestBeats,
                                               @Query("android") String trueAndroid);

    @GET("video/youTube.json")
    Call<JsonResponse> getYoutubeLink();

    @POST("mhmbeats/social_login.php")
    Call<LoginResponse> postUserLogin(@Body LoginRequest loginRequest);

}
