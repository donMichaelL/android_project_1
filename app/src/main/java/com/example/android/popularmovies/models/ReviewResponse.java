package com.example.android.popularmovies.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by michalis on 3/6/2017.
 */

public class ReviewResponse {
    @SerializedName("id")
    private String id;
    @SerializedName("page")
    private String page;
    @SerializedName("results")
    private ArrayList<Review> results;
    @SerializedName("total_pages")
    private String total_pages;
    @SerializedName("total_results")
    private String total_results;

    public ArrayList<Review> getResults() {
        return results;
    }

    public String getTotal_results() {
        return total_results;
    }
}
