package com.example.android.popularmovies.api;

import com.example.android.popularmovies.models.MovieResponse;
import com.example.android.popularmovies.models.ReviewResponse;
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

    @GET("{id}/reviews")
    Call<ReviewResponse> getReviewFromId(@Path("id") String id, @Query("api_key") String api_key);

    @GET("{filter}")
    Call<MovieResponse> getMoviesWithFilter(@Path("filter") String filter, @Query("api_key") String api_key, @Query("page") String page);
}
