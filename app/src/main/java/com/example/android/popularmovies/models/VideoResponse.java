package com.example.android.popularmovies.models;

import android.provider.MediaStore;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Michalis on 2/22/2017.
 */

public class VideoResponse {
    @SerializedName("id")
    private String id;
    @SerializedName("results")
    private ArrayList<Video> results;

    public ArrayList<Video> getResults() {
        return results;
    }
}
