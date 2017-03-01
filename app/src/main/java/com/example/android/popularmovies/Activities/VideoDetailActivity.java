package com.example.android.popularmovies.Activities;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.fragments.VideoFragment;
import com.example.android.popularmovies.models.Video;
import com.example.android.popularmovies.Adapters.VideoAdapter;
import com.example.android.popularmovies.R;

import java.util.ArrayList;

public class VideoDetailActivity extends AppCompatActivity {
    private static final String TAG = VideoDetailActivity.class.getName();

    private static final String VIDEO_ARRAY_LIST = "video_list";
    private static final String FRAGMENT_TAG = "video_fragment";


    private ArrayList<Video> videoArrayList;
    private VideoAdapter videoAdapter;

    private RecyclerView videoRecyclerView;
    private TextView tvErrorMsg;
    private ProgressBar pgLoading;

    private BroadcastReceiver internetChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_detail);
        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra(MovieDetailActivity.MOVIE_ID)){
            finish();
        }
        final String id = getIntent().getStringExtra(MovieDetailActivity.MOVIE_ID);

        VideoFragment videoFragment;
        FragmentManager fragmentManager = getFragmentManager();
        videoFragment = (VideoFragment) fragmentManager.findFragmentByTag(FRAGMENT_TAG);

        if (videoFragment==null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            videoFragment = new VideoFragment();
            Bundle a = new Bundle();
            a.putString(VideoFragment.MOVIE_ID, id);
            videoFragment.setArguments(a);
            fragmentTransaction.add(R.id.activity_lay, videoFragment, FRAGMENT_TAG);
            fragmentTransaction.commit();
        }




//        videoRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_video);
//        tvErrorMsg = (TextView) findViewById(R.id.tv_error_msg_video);
//        pgLoading = (ProgressBar) findViewById(R.id.pg_loading_video);
//
//        videoAdapter = new VideoAdapter(this);
//        videoRecyclerView.setLayoutManager(
//                new LinearLayoutManager(getBaseContext(),LinearLayoutManager.VERTICAL, false));
//
//        videoRecyclerView.setHasFixedSize(true);
//        videoRecyclerView.setAdapter(videoAdapter);
//
//        if(savedInstanceState == null || !savedInstanceState.containsKey(VIDEO_ARRAY_LIST)) {
//            createAnyncTasForVideoMovieData(id);
//        } else {
//            videoArrayList = savedInstanceState.getParcelableArrayList(VIDEO_ARRAY_LIST);
//            videoAdapter.setArrayAdapter(videoArrayList);
//        }
//
//        internetChangeReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                if (NetworkUtils.isOnline(context)){
//                    createAnyncTasForVideoMovieData(id);                }
//            }
//        };
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (internetChangeReceiver != null) {
//            this.unregisterReceiver(internetChangeReceiver);
//        }
//    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (internetChangeReceiver != null) {
//            IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
//            this.registerReceiver(internetChangeReceiver, intentFilter);
//        }
//    }
//
//    private void createAnyncTasForVideoMovieData(String id) {
//        if (NetworkUtils.isOnline(this)){
//            ApiInterface apiInterface = ApiClient.retrofitVideoBuilder().create(ApiInterface.class);
//            Call<VideoResponse> call = apiInterface.getVideoFromId(id, MainActivity.API_KEY);
//            Log.d(TAG, call.request().url().toString());
//            pgLoading.setVisibility(View.VISIBLE);
//            call.enqueue(new Callback<VideoResponse>() {
//                @Override
//                public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
//                    pgLoading.setVisibility(View.INVISIBLE);
//                    if (response.isSuccessful()) {
//                        videoArrayList = response.body().getResults();
//                        videoAdapter.setArrayAdapter(videoArrayList);
//                        showVideoData();
//                    } else {
//                        showErrorMsg();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<VideoResponse> call, Throwable t) {
//                    showErrorMsg();
//                }
//            });
//
//        }else {
//            pgLoading.setVisibility(View.INVISIBLE);
//            showErrorMsg();
//        }
//    }
//
//    private void showErrorMsg(){
//        videoRecyclerView.setVisibility(View.INVISIBLE);
//        tvErrorMsg.setVisibility(View.VISIBLE);
//    }
//
//    private void showVideoData(){
//        videoRecyclerView.setVisibility(View.VISIBLE);
//        tvErrorMsg.setVisibility(View.INVISIBLE);
//    }

//    @Override
//    public void onListItemClick(int clickedItemIndex) {
//        Log.d(TAG, videoArrayList.get(clickedItemIndex).getName());
//        Video video = videoArrayList.get(clickedItemIndex);
//        Uri sendingUri = NetworkUtils.buildUrlForVideo(video.getSite(), video.getKey());
//        if (sendingUri != null){
//            Intent intent = new Intent(Intent.ACTION_VIEW, sendingUri);
//            if (intent.resolveActivity(getPackageManager()) != null){
//                startActivity(intent);
//            }else {
//                Toast.makeText(this, R.string.download_app, Toast.LENGTH_SHORT).show();
//
//            }
//        } else {
//            Toast.makeText(this, R.string.only_videos_youtube, Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        outState.putParcelableArrayList(VIDEO_ARRAY_LIST, videoArrayList);
//        super.onSaveInstanceState(outState);
//    }
}
