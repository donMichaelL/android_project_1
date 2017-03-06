package com.example.android.popularmovies.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by michalis on 3/6/2017.
 */

public class ApiError {
    @SerializedName("status_code")
    private int statusCode;
    @SerializedName("status_message")
    private String message;

    public String getMessage() {
        return message;
    }
}
