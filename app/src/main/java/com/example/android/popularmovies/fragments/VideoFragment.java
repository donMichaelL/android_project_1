package com.example.android.popularmovies.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.Activities.MainActivity;
import com.example.android.popularmovies.Adapters.VideoAdapter;
import com.example.android.popularmovies.NetworkUtils.NetworkUtils;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.api.ApiClient;
import com.example.android.popularmovies.api.ApiInterface;
import com.example.android.popularmovies.models.Video;
import com.example.android.popularmovies.models.VideoResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by michalis on 3/1/2017.
 */

public class VideoFragment extends Fragment implements VideoAdapter.ListItemClickListener {
    private static final String TAG = VideoFragment.class.getName();

    public static final String MOVIE_ID = "movie_id";
    private static final String VIDEO_ARRAY_LIST = "video_list";

    private ArrayList<Video> videoArrayList;
    private VideoAdapter videoAdapter;

    private RecyclerView videoRecyclerView;
    private TextView tvErrorMsg;
    private TextView tvNoVideo;
    private ProgressBar pgLoading;

    private BroadcastReceiver internetChangeReceiver;

    private String movieId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoAdapter = new VideoAdapter(this);
        movieId = getArguments().getString(MOVIE_ID);
        internetChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (NetworkUtils.isOnline(context)){
                    Log.d(TAG, "JELLO");
                    createAnyncTasForVideoMovieData(movieId);
                }
            }
        };
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments()== null || !getArguments().containsKey(MOVIE_ID) ) {
           getActivity().finish();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_video_detail, container, false);
        videoRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_video);
        tvErrorMsg = (TextView) rootView.findViewById(R.id.tv_error_msg_video);
        pgLoading = (ProgressBar) rootView.findViewById(R.id.pg_loading_video);
        tvNoVideo = (TextView) rootView.findViewById(R.id.tv_no_videos);
        videoRecyclerView.setAdapter(videoAdapter);
        videoRecyclerView.setHasFixedSize(true);
        videoRecyclerView.setLayoutManager(
                new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL, false));
        if (savedInstanceState!=null && savedInstanceState.containsKey(VIDEO_ARRAY_LIST)) {
            videoArrayList = savedInstanceState.getParcelableArrayList(VIDEO_ARRAY_LIST);
            videoAdapter.setArrayAdapter(videoArrayList);
            Log.d(TAG, "RETAIN");
        } else {
            createAnyncTasForVideoMovieData(movieId);
            Log.d(TAG, "NOT RETAIN");
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (internetChangeReceiver != null) {
            IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            getActivity().registerReceiver(internetChangeReceiver, intentFilter);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (internetChangeReceiver != null) {
            getActivity().unregisterReceiver(internetChangeReceiver);
        }
    }

    private void createAnyncTasForVideoMovieData(String id) {
        if (NetworkUtils.isOnline(getActivity())){
            ApiInterface apiInterface = ApiClient.retrofitVideoBuilder().create(ApiInterface.class);
            Call<VideoResponse> call = apiInterface.getVideoFromId(id, MainActivity.API_KEY);
            Log.d(TAG, call.request().url().toString());
            showLoading();
            call.enqueue(new Callback<VideoResponse>() {
                @Override
                public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                    if (response.isSuccessful()) {
                        videoArrayList = response.body().getResults();
                        videoAdapter.setArrayAdapter(videoArrayList);
                        if (videoArrayList.size() == 0) {
                            showNoVideo();
                        } else {
                            showVideoData();
                        }
                    } else {
                        showErrorMsg();
                    }
                }
                @Override
                public void onFailure(Call<VideoResponse> call, Throwable t) {
                    showErrorMsg();
                }
            });

        }else {
            pgLoading.setVisibility(View.INVISIBLE);
            showErrorMsg();
        }
    }

    private void showErrorMsg(){
        videoRecyclerView.setVisibility(View.INVISIBLE);
        tvErrorMsg.setVisibility(View.VISIBLE);
        pgLoading.setVisibility(View.INVISIBLE);
        tvNoVideo.setVisibility(View.INVISIBLE);
    }

    private void showNoVideo() {
        videoRecyclerView.setVisibility(View.INVISIBLE);
        tvErrorMsg.setVisibility(View.INVISIBLE);
        pgLoading.setVisibility(View.INVISIBLE);
        tvNoVideo.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        videoRecyclerView.setVisibility(View.INVISIBLE);
        tvErrorMsg.setVisibility(View.INVISIBLE);
        pgLoading.setVisibility(View.VISIBLE);
        tvNoVideo.setVisibility(View.INVISIBLE);
    }

    private void showVideoData(){
        videoRecyclerView.setVisibility(View.VISIBLE);
        tvErrorMsg.setVisibility(View.INVISIBLE);
        pgLoading.setVisibility(View.INVISIBLE);
        tvNoVideo.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Video video = videoArrayList.get(clickedItemIndex);
        Uri sendingUri = NetworkUtils.buildUrlForVideo(video.getSite(), video.getKey());
        if (sendingUri != null){
            Intent intent = new Intent(Intent.ACTION_VIEW, sendingUri);
            if (intent.resolveActivity(getActivity().getPackageManager()) != null){
                startActivity(intent);
            }else {
                Toast.makeText(getActivity(), R.string.download_app, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), R.string.only_videos_youtube, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(VIDEO_ARRAY_LIST, videoArrayList);
        super.onSaveInstanceState(outState);
    }
}
