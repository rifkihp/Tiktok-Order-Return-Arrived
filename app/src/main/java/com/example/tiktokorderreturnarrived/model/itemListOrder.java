package com.example.tiktokorderreturn.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class itemListOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @SerializedName("id")
    String id;

    @SerializedName("product_id")
    String product_id;

    @SerializedName("sku_id")
    String sku_id;

    @SerializedName("product_name")
    String product_name;

    @SerializedName("sku_name")
    String sku_name;

    //@SerializedName("qty")
    //int qty;

    public String getId() {
        return this.id;
    }

    public String getProduct_id() {
        return this.product_id;
    }

    public String getProduct_name() {
        return this.product_name;
    }

    public String getSku_id() {
        return this.sku_id;
    }

    public String getSku_name() {
        return this.sku_name;
    }

    //public int getQty() {
    //    return this.qty;
    //}

}
