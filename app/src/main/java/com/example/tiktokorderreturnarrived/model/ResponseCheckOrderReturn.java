package com.example.tiktokorderreturn.model;
import com.google.gson.annotations.SerializedName;

public class ResponseCheckOrderReturn {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("order_id")
    private String orderId;

    @SerializedName("tracking_number")
    private String tracking_number;

    @SerializedName("platform")
    private String platform;

    public boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getTracking_number() {
        return tracking_number;
    }

    public String getPlatform() {
        return platform;
    }

}
