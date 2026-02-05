package com.example.tiktokorderreturn.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class orders implements Serializable {

    private static final long serialVersionUID = 1L;

    @SerializedName("id")
    String id;

    @SerializedName("tracking_number")
    String tracking_number;

    @SerializedName("line_items")
    ArrayList<itemListOrder> itemList;


    public String getId() {
        return this.id;
    }

    public String getTracking_number() {
        return this.tracking_number;
    }

    public ArrayList<itemListOrder> getItemList() {
        return this.itemList;
    }

}
