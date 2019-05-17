package com.makehitmusic.hiphopbeats.rest;

import com.makehitmusic.hiphopbeats.model.CategoryResponse;
import com.makehitmusic.hiphopbeats.model.LoginResponse;
import com.makehitmusic.hiphopbeats.presenter.JsonResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("Dante/MHMBeats/categories.php")
    Call<CategoryResponse> getCategory(@Query("android") String trueAndroid);

    @GET("Dante/MHMBeats/products.php")
    Call<CategoryResponse> getBeatsDetails(@Query("category_id") int categoryId,
                                           @Query("userid") int userId,
                                           @Query("latest") String latestBeats,
                                           @Query("android") String trueAndroid);

    @GET("Dante/MHMBeats/products.php")
    Call<CategoryResponse> getBeatsDetails(@Query("userid") int userId,
                                           @Query("latest") String latestBeats,
                                           @Query("android") String trueAndroid);

    @GET("Dante/MHMBeats/products.php")
    Call<CategoryResponse> getBeatsDetails(@Query("purchase") String purchasedBeats,
                                           @Query("android") String trueAndroid);

    @GET("Dante/MHMBeats/producers.php")
    Call<CategoryResponse> getProducers(@Query("android") String trueAndroid);

    @GET("Dante/MHMBeats/products.php")
    Call<CategoryResponse> getProducersDetails(@Query("producer_id") int producerId,
                                               @Query("userid") int userId,
                                               @Query("latest") String latestBeats,
                                               @Query("android") String trueAndroid);

    @GET("video/youTube.json")
    Call<JsonResponse> getYoutubeLink();

    @POST("Dante/MHMBeats/social_login.php")
    Call<LoginResponse> postUserLogin(@Query("email") String emailId,
                                      @Query("username") String userName,
                                      @Query("firstname") String firstName,
                                      @Query("lastname") String lastName,
                                      @Query("userID") String userId,
                                      @Query("idToken") String idToken,
                                      @Query("loginType") String loginType,
                                      @Query("photo") String photo);

}
