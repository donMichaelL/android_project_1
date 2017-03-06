package com.example.android.popularmovies.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Michalis on 2/22/2017.
 */

public class ApiClient {
    private final static String BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static Retrofit retrofitVideo = null;

    public static Retrofit retrofitVideoBuilder(){
        if (retrofitVideo == null) {
            retrofitVideo = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitVideo;
    }
}
