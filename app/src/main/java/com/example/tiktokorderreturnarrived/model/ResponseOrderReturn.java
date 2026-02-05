package com.example.tiktokorderreturnarrived.model;
import com.google.gson.annotations.SerializedName;

public class ResponseOrderReturn {


    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;
    @SerializedName("is_return_update_stok")
    private boolean is_return_update_stok;

    public boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public boolean getIsReturnUpdateStok() {
        return is_return_update_stok;
    }

}
