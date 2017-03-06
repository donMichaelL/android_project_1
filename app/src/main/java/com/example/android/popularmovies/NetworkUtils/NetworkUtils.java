package com.example.android.popularmovies.NetworkUtils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovies.Activities.MainActivity;
import com.example.android.popularmovies.api.ApiClient;
import com.example.android.popularmovies.models.ApiError;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

/**
 * Created by Michalis on 1/27/2017.
 */

public final class NetworkUtils {

    private final static String TAG = NetworkUtils.class.getName();

    private final static String BASE_URL = "https://api.themoviedb.org/3/movie/";
    private final static String IMAGE_URL = "http://image.tmdb.org/t/p/w185//";


    private final static String API_PARAM = "api_key";
    private final static String PAGE_PARAM = "page";

    public final static String ORDERING_POPULARITY = "popularity";
    public final static String ORDERING_VOTES = "vote_average";

    public final static String YOUTUBE = "https://www.youtube.com/watch";
    public final static String YOUTUBE_WATCH = "v";



    public static URL buildUrlForQueryOrderingMovieDB(String orderingParam, int pageNumber) {
        String baseUrl;
        if( orderingParam.equals(ORDERING_POPULARITY)){
            baseUrl = BASE_URL.concat("popular");
        }else {
            baseUrl = BASE_URL.concat("top_rated");
        }
        Uri buildUri = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter(API_PARAM, MainActivity.API_KEY)
                .appendQueryParameter(PAGE_PARAM, Integer.toString(pageNumber))
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
        URL imageUrl = null;
        try {
            imageUrl = new URL(IMAGE_URL.concat(imageExtention));
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        return imageUrl;
    }


    public static Uri buildUrlForVideo(String site, String key){
        if (! site.equals("YouTube")) {
            return null;
        }

        return Uri.parse(YOUTUBE).buildUpon()
                .appendQueryParameter(YOUTUBE_WATCH, key)
                .build();
    }

    public static boolean isOnline(Context context){
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static ApiError parseError(Response<?> response) {
        ApiError apiError;
        Converter<ResponseBody, ApiError> converter = ApiClient
                .retrofitVideoBuilder().responseBodyConverter(ApiError.class, new Annotation[0]);
        try{
            apiError = converter.convert(response.errorBody());
        } catch (IOException e){
            return new ApiError();
        }
        return apiError;
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
