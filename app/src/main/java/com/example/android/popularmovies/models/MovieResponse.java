package com.example.android.popularmovies.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by michalis on 3/6/2017.
 */

public class MovieResponse {
    @SerializedName("page")
    private String page;
    @SerializedName("results")
    private ArrayList<Movie> results;
    @SerializedName("total_pages")
    private String totalPages;
    @SerializedName("total_results")
    private String totalResults;

    public String getPage() {
        return page;
    }

    public ArrayList<Movie> getResults() {
        return results;
    }

    public String getTotalPages() {
        return totalPages;
    }

    public String getTotalResults() {
        return totalResults;
    }
}
