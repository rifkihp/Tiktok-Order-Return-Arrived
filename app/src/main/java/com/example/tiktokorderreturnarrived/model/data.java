package com.example.tiktokorderreturn.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class data implements Serializable {

    private static final long serialVersionUID = 1L;

    @SerializedName("orders")
    ArrayList<orders> orders;

    public ArrayList<orders> getOrders() {
        return this.orders;
    }

}
