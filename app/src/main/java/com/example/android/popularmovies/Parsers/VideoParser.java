package com.example.android.popularmovies.Parsers;

import android.util.Log;

import com.example.android.popularmovies.Pojo.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Michalis on 2/3/2017.
 */

public final class VideoParser {
    private final static String TAG = VideoParser.class.getName();

    public static ArrayList<Video> createVideoArrayFromStringJson(String videoJsonStr) throws JSONException {
        Log.d(TAG, "Starting Parsing Data");

        final String OWM_SUCCESS = "success";
        final String OWM_RESULTS = "results";

        JSONObject videoJson = new JSONObject(videoJsonStr);

        /* Is there an error? p.x. api */
        if(videoJson.has(OWM_SUCCESS)){
            boolean result = videoJson.getBoolean(OWM_SUCCESS);
            if(!result){
                return null;
            }
        }

        JSONArray resultsArray = videoJson.getJSONArray(OWM_RESULTS);

        ArrayList<Video> videoArrayList = new ArrayList<>();

        for(int i=0; i< resultsArray.length();i++){
            JSONObject movie = resultsArray.getJSONObject(i);
            videoArrayList.add(returnVideoObjFromJsonObj(movie));
        }

        return videoArrayList;
    }

    private static Video returnVideoObjFromJsonObj(JSONObject movie) throws JSONException {
        final String OWM_ID = "id";
        final String OWM_ISO_FIRST = "iso_639_1";
        final String OWM_ISO_SEC = "iso_3166_1";
        final String OWM_KEY = "key";
        final String OWM_NAME = "name";
        final String OWM_SITE = "site";
        final String OWM_SIZE = "size";
        final String OWM_TYPE = "type";

        Video returnedVideo = new Video(
                movie.getString(OWM_ID),
                movie.getString(OWM_ISO_FIRST),
                movie.getString(OWM_ISO_SEC),
                movie.getString(OWM_KEY),
                movie.getString(OWM_NAME),
                movie.getString(OWM_SITE),
                movie.getInt(OWM_SIZE),
                movie.getString(OWM_TYPE)
        );

        return returnedVideo;
    }

}
