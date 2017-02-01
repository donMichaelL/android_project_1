package com.example.android.popularmovies.NetworkUtils;


import android.net.Uri;
import android.util.Log;
import android.widget.ProgressBar;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Michalis on 1/27/2017.
 */

public final class NetworkUtils {

    private final static String TAG = NetworkUtils.class.getName();

    private final static String API_KEY = "MUST BE YOUR KEY";

    private final static String BASE_URL = "https://api.themoviedb.org/3/movie/";
    private final static String IMAGE_URL = "http://image.tmdb.org/t/p/w185//";


    private final static String API_PARAM = "api_key";

    public final static String ORDERING_POPULARITY = "popularity";
    public final static String ORDERING_VOTES = "vote_average";


    public static URL buildUrlForQueryOrderingMovieDB(String orderingParam) {
        String baseUrl;
        if( orderingParam == ORDERING_POPULARITY){
            baseUrl = BASE_URL.concat("popular");
        }else {
            baseUrl = BASE_URL.concat("top_rated");
        }
        Uri buildUri = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter(API_PARAM, API_KEY)
                .build();

        Log.d(TAG, "The requested url: " + buildUri.toString());

        URL returnedUrl= null;
        try {
            returnedUrl = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return returnedUrl;
    }

    public static URL buildUrlForImages(String imageExtention){
        /* Example http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg  */
        URL imageUrl = null;
        try {
            imageUrl = new URL(IMAGE_URL.concat(imageExtention));
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        return imageUrl;
    }


    public static String getResponseFromHttpUrl(URL url) throws IOException{
        Log.d(TAG, "Trying to connect and get Data from " + url.toString());
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
