package com.example.android.popularmovies.fragments;

import android.content.Intent;
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
    private ProgressBar pgLoading;

    private String movieId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        videoAdapter = new VideoAdapter(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /* TOCHECK It seems that these should go to onCreate
        but i cannot call getActivity() */
        if (getArguments()== null || !getArguments().containsKey(MOVIE_ID) ) {
           getActivity().finish();
        }

        movieId = getArguments().getString(MOVIE_ID);
        videoRecyclerView.setLayoutManager(
                new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        /* TOCHECK  It seems that these code must go to onCreate
        * the problem is that views are not instantiated yet */
        if (savedInstanceState!=null && savedInstanceState.containsKey(VIDEO_ARRAY_LIST)) {
            videoArrayList = savedInstanceState.getParcelableArrayList(VIDEO_ARRAY_LIST);
            videoAdapter.setArrayAdapter(videoArrayList);
        } else {
            createAnyncTasForVideoMovieData(movieId);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_video_detail, container, false);
        videoRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_video);
        tvErrorMsg = (TextView) rootView.findViewById(R.id.tv_error_msg_video);
        pgLoading = (ProgressBar) rootView.findViewById(R.id.pg_loading_video);
        videoRecyclerView.setAdapter(videoAdapter);
        videoRecyclerView.setHasFixedSize(true);
        return rootView;
    }

    private void createAnyncTasForVideoMovieData(String id) {
        if (NetworkUtils.isOnline(getActivity())){
            ApiInterface apiInterface = ApiClient.retrofitVideoBuilder().create(ApiInterface.class);
            Call<VideoResponse> call = apiInterface.getVideoFromId(id, MainActivity.API_KEY);
            Log.d(TAG, call.request().url().toString());
            pgLoading.setVisibility(View.VISIBLE);
            call.enqueue(new Callback<VideoResponse>() {
                @Override
                public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                    pgLoading.setVisibility(View.INVISIBLE);
                    if (response.isSuccessful()) {
                        videoArrayList = response.body().getResults();
                        videoAdapter.setArrayAdapter(videoArrayList);
                        showVideoData();
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
    }

    private void showVideoData(){
        videoRecyclerView.setVisibility(View.VISIBLE);
        tvErrorMsg.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Log.d(TAG, videoArrayList.get(clickedItemIndex).getName());
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
