package com.example.tiktokorderreturnarrived.data;

import com.example.tiktokorderreturnarrived.model.ResponseCheckOrderReturn;
import com.example.tiktokorderreturnarrived.model.ResponseDetailOrder;
import com.example.tiktokorderreturnarrived.model.ResponseOrderReturn;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RestApi {

    @GET("order/checkOrderReturn")
    Call<ResponseCheckOrderReturn> checkOrderReturn(
            @Query("tracking_number") String tracking_number
    );

    @GET("order/getOrderReturn")
    Call<ResponseDetailOrder> getDetailOrder(
            @Query("ids") String order_id,
            @Query("tracking_number") String tracking_number,
            @Query("platform") String order_platform
    );

    @FormUrlEncoded
    @POST("order/setOrderReturn")
    Call<ResponseOrderReturn> updateReturn(
            @Field("orderId") String order_id,
            @Field("itemList") String item_list,
            @Field("platform") String platform
    );



}