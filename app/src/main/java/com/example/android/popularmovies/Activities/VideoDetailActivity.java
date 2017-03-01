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
        if (intent == null || !intent.hasExtra(MovieDetailActivity.MOVIE_ID)) {
            finish();
        }
        final String id = getIntent().getStringExtra(MovieDetailActivity.MOVIE_ID);

        VideoFragment videoFragment;
        FragmentManager fragmentManager = getFragmentManager();
        videoFragment = (VideoFragment) fragmentManager.findFragmentByTag(FRAGMENT_TAG);

        if (videoFragment == null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            videoFragment = new VideoFragment();
            Bundle a = new Bundle();
            a.putString(VideoFragment.MOVIE_ID, id);
            videoFragment.setArguments(a);
            fragmentTransaction.add(R.id.activity_lay, videoFragment, FRAGMENT_TAG);
            fragmentTransaction.commit();
        }
    }
}
