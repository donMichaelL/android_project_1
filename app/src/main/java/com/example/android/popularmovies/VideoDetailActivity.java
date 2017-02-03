package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.android.popularmovies.Movies.MoviesParser;
import com.example.android.popularmovies.NetworkUtils.NetworkUtils;
import com.example.android.popularmovies.Video.Video;
import com.example.android.popularmovies.Video.VideoParser;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class VideoDetailActivity extends AppCompatActivity {
    private static final String TAG = VideoDetailActivity.class.getName();

    private static final String VIDEO_ARRAY_LIST = "video_list";


    private ArrayList<Video> videoArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        Intent intent = getIntent();
        if (intent == null){
            finish();
        }
        Integer id = getIntent().getIntExtra(MovieDetailActivity.MOVIE_ID, 0);

        //TODO check if saved Instances
        if(savedInstanceState == null || !savedInstanceState.containsKey(VIDEO_ARRAY_LIST)) {
            createAnyncTasForVideoMovieData(id);
        } else {
            videoArrayList = savedInstanceState.getParcelableArrayList(VIDEO_ARRAY_LIST);
        }
    }

    private void createAnyncTasForVideoMovieData(Integer id) {
        if (isOnline()){
            URL requestUrl = NetworkUtils.buildUrlForMovieVideoMovieDB(id);
            new MovieDBVideoQueyTask().execute(requestUrl);
        }else {
            //TODO showErrorMsg()
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private class MovieDBVideoQueyTask extends AsyncTask<URL, Void, ArrayList<Video>>{

        @Override
        protected ArrayList<Video> doInBackground(URL... params) {
            Log.d(TAG, "Asynchronous Task is stating retrieving data");
            URL requestUrl = params[0];
            try {
                String responseString = NetworkUtils.getResponseFromHttpUrl(requestUrl);
                videoArrayList = VideoParser.createVideoArrayFromStringJson(responseString);
                Log.d(TAG, videoArrayList.get(0).getName());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(VIDEO_ARRAY_LIST, videoArrayList);
        super.onSaveInstanceState(outState);
    }
}
