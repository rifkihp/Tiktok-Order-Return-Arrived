package com.example.tiktokorderreturn.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResponseDetailOrder {

	@SerializedName("code")
	int code;

	@SerializedName("message")
	String message;

	@SerializedName("request_id")
	String request_id;

	@SerializedName("data")
	data data;

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public String getRequest_id() {
		return request_id;
	}

	public data getData() {
		return data;
	}

}