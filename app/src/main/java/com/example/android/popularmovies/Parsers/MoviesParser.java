package com.example.android.popularmovies.Parsers;

import android.util.Log;

import com.example.android.popularmovies.Pojo.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Michalis on 1/27/2017.
 */

public final class MoviesParser {
    private final static String TAG = MoviesParser.class.getName();

    public static int getTotalPages(String moviesJsonStr) throws JSONException {
        Log.d(TAG, "Starting Parsing Data");
        final String OWM_SUCCESS = "success";
        final String OWM_TOTAL_PAGES = "total_pages";

        JSONObject moviesJson = new JSONObject(moviesJsonStr);

        /* Is there an error? p.x. api */
        if(moviesJson.has(OWM_SUCCESS)){
            boolean result = moviesJson.getBoolean(OWM_SUCCESS);
            if(!result){
                return 0;
            }
        }

        return moviesJson.getInt(OWM_TOTAL_PAGES);
    }


    public static ArrayList<Movie> getSimpleStringFromJson(String moviesJsonStr) throws JSONException {
        Log.d(TAG, "Starting Parsing Data");
        final String OWM_SUCCESS = "success";
        final String OWM_RESULTS = "results";

        ArrayList<Movie> movieArrayList = null;

        JSONObject moviesJson = new JSONObject(moviesJsonStr);

        /* Is there an error? p.x. api */
        if(moviesJson.has(OWM_SUCCESS)){
            boolean result = moviesJson.getBoolean(OWM_SUCCESS);
            if(!result){
                return null;
            }
        }

        JSONArray resultsArray = moviesJson.getJSONArray(OWM_RESULTS);

        movieArrayList = new ArrayList<>();

        for(int i=0; i<resultsArray.length(); i++){
            JSONObject movieJson = resultsArray.getJSONObject(i);
            Movie movieObj = returnMovieObjFromJson(movieJson);
            movieArrayList.add(movieObj);
        }
        return movieArrayList;
    }

    private static Movie returnMovieObjFromJson(JSONObject movieJsonObj) throws JSONException {
        final String OWM_ID = "id";
        final String OWM_POSTER_PATH = "poster_path";
        final String OWM_ADULT = "adult";
        final String OWM_OVERVIEW = "overview";
        final String OWM_RELEASE_DATE = "release_date";
        final String OWM_GENRE_IDS = "genre_ids";
        final String OWM_ORIGINIAL_TITLE ="original_title";
        final String OWM_ORIGINAL_LANGUAGE = "original_language";
        final String OWM_TITLE = "title";
        final String OWM_BACKDROP_PATH = "backdrop_path";
        final String OWM_POPULARITY = "popularity";
        final String OWM_VOTE_COUNT ="vote_count";
        final String OWM_VIDEO = "video";
        final String OWM_VOTE_AVERAGE = "vote_average";

        Movie movie =  new Movie(movieJsonObj.getString(OWM_ID),
                movieJsonObj.getString(OWM_POSTER_PATH),
                movieJsonObj.getString(OWM_BACKDROP_PATH),
                movieJsonObj.getString(OWM_TITLE),
                movieJsonObj.getString(OWM_ORIGINIAL_TITLE),
                movieJsonObj.getString(OWM_ORIGINAL_LANGUAGE),
                movieJsonObj.getBoolean(OWM_ADULT),
                movieJsonObj.getString(OWM_OVERVIEW),
                movieJsonObj.getString(OWM_POPULARITY),
                movieJsonObj.getString(OWM_VOTE_COUNT),
                movieJsonObj.getString(OWM_VOTE_AVERAGE),
                movieJsonObj.getBoolean(OWM_VIDEO),
                movieJsonObj.getString(OWM_RELEASE_DATE)
                );

        JSONArray jsonArrayGenresId = movieJsonObj.getJSONArray(OWM_GENRE_IDS);
        movie.setGenresIds(returnGenresIds(jsonArrayGenresId));

        return movie;

    }

    private static int[] returnGenresIds(JSONArray genresIdsJson){
        int[] genresIds = new int[genresIdsJson.length()];
        for(int i=0; i< genresIdsJson.length(); i++) {
            genresIds[i] = genresIdsJson.optInt(i);
        }
        return genresIds;
    }

}
