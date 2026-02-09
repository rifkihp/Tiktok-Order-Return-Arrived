package com.example.tiktokorderreturnarrived.model;
import com.google.gson.annotations.SerializedName;

public class ResponseSetOrderReturnArrived {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;


    public boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

}
