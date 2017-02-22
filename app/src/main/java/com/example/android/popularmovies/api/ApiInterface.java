package com.example.android.popularmovies.api;

import com.example.android.popularmovies.models.VideoResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Michalis on 2/22/2017.
 */

public interface ApiInterface {

    @GET("{id}/videos")
    Call<VideoResponse> getVideoFromId(@Path("id") String id, @Query("api_key") String api_key);
}
