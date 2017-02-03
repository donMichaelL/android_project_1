package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.MovieAdapter.MovieAdapter;
import com.example.android.popularmovies.NetworkUtils.NetworkUtils;
import com.example.android.popularmovies.Video.Video;
import com.example.android.popularmovies.Video.VideoParser;
import com.example.android.popularmovies.VideoAdapter.VideoAdapter;

import java.net.URL;
import java.util.ArrayList;

public class VideoDetailActivity extends AppCompatActivity implements VideoAdapter.ListItemClickListener {
    private static final String TAG = VideoDetailActivity.class.getName();

    private static final String VIDEO_ARRAY_LIST = "video_list";

    private ArrayList<Video> videoArrayList;
    private VideoAdapter videoAdapter;

    private RecyclerView videoRecyclerView;
    private TextView tvErrorMsg;
    private ProgressBar pgLoading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        Intent intent = getIntent();
        if (intent == null){
            finish();
        }
        Integer id = getIntent().getIntExtra(MovieDetailActivity.MOVIE_ID, 0);

        videoRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_video);
        tvErrorMsg = (TextView) findViewById(R.id.tv_error_msg_video);
        pgLoading = (ProgressBar) findViewById(R.id.pg_loading_video);

        videoAdapter = new VideoAdapter(this);
        videoRecyclerView.setLayoutManager(
                new LinearLayoutManager(getBaseContext(),LinearLayoutManager.VERTICAL, false));

        videoRecyclerView.setAdapter(videoAdapter);

        if(savedInstanceState == null || !savedInstanceState.containsKey(VIDEO_ARRAY_LIST)) {
            createAnyncTasForVideoMovieData(id);
        } else {
            videoArrayList = savedInstanceState.getParcelableArrayList(VIDEO_ARRAY_LIST);
            videoAdapter.setArrayAdapter(videoArrayList);
        }
    }

    private void createAnyncTasForVideoMovieData(Integer id) {
        if (isOnline()){
            URL requestUrl = NetworkUtils.buildUrlForMovieVideoMovieDB(id);
            new MovieDBVideoQueryTask().execute(requestUrl);
        }else {
            showErrorMsg();
        }
    }

    private void showErrorMsg(){
        videoRecyclerView.setVisibility(View.INVISIBLE);
        tvErrorMsg.setVisibility(View.VISIBLE);
    }

    private void showVideoData(){
        videoRecyclerView.setVisibility(View.VISIBLE);
        tvErrorMsg.setVisibility(View.INVISIBLE);
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Log.d(TAG, videoArrayList.get(clickedItemIndex).getName());
    }

    private class MovieDBVideoQueryTask extends AsyncTask<URL, Void, ArrayList<Video>>{

        @Override
        protected ArrayList<Video> doInBackground(URL... params) {
            Log.d(TAG, "Asynchronous Task is stating retrieving data");
            URL requestUrl = params[0];
            try {
                String responseString = NetworkUtils.getResponseFromHttpUrl(requestUrl);
                videoArrayList = VideoParser.createVideoArrayFromStringJson(responseString);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return videoArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<Video> videoArrayList) {
            pgLoading.setVisibility(View.INVISIBLE);
            if (videoArrayList != null){
                Log.d(TAG, "Data received with success");
                videoAdapter.setArrayAdapter(videoArrayList);
                showVideoData();
            } else {
                showErrorMsg();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(VIDEO_ARRAY_LIST, videoArrayList);
        super.onSaveInstanceState(outState);
    }
}
