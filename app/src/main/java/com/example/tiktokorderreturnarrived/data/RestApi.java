package com.example.tiktokorderreturn.data;

import com.example.tiktokorderreturn.model.ResponseCheckOrderReturn;
import com.example.tiktokorderreturn.model.ResponseDetailOrder;
import com.example.tiktokorderreturn.model.ResponseSaveVideoPacking;
import com.example.tiktokorderreturn.model.ResponseOrderReturn;
import com.example.tiktokorderreturn.model.ResponseUploadDokumen;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
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