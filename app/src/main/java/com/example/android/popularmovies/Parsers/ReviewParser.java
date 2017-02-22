package com.example.android.popularmovies.Parsers;

import android.util.Log;

import com.example.android.popularmovies.models.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Michalis on 2/6/2017.
 */

public class ReviewParser {
    private final static String TAG = ReviewParser.class.getName();


    public static ArrayList<Review> getSimpleStringFromJson(String reviewsJsonStr) throws JSONException {
        Log.d(TAG, "Starting Parsing Data");
        final String OWM_SUCCESS = "success";
        final String OWM_RESULTS = "results";

        ArrayList<Review> reviewArrayList= new ArrayList<>();

        JSONObject reviewsJson = new JSONObject(reviewsJsonStr);

        /* Is there an error? p.x. api */
        if(reviewsJson.has(OWM_SUCCESS)){
            boolean result = reviewsJson.getBoolean(OWM_SUCCESS);
            if(!result){
                return null;
            }
        }

        JSONArray resultsArray = reviewsJson.getJSONArray(OWM_RESULTS);

        for (int i=0; i < resultsArray.length(); i++){
            reviewArrayList.add(getReviewObjFromJsonObj(resultsArray.getJSONObject(i)));
        }
        return reviewArrayList;
    }

    private static Review getReviewObjFromJsonObj(JSONObject jsonObject) throws JSONException {
        final String OWM_ID = "id";
        final String OWM_AUTHOR = "author";
        final String OWM_CONTENT = "content";
        final String OWM_URL = "url";

        Review review = new Review(
                jsonObject.getString(OWM_ID),
                jsonObject.getString(OWM_AUTHOR),
                jsonObject.getString(OWM_CONTENT),
                jsonObject.getString(OWM_URL)
        );
        return review;
    }
}
