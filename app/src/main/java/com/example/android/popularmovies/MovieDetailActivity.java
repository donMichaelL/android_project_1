package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.popularmovies.Movies.Movie;
import com.example.android.popularmovies.NetworkUtils.NetworkUtils;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {
    private static final String TAG = MovieDetailActivity.class.getName();

    private ImageView headerImage;
    private ImageView thumbnail;
    private TextView originalTitle;
    private TextView tvOverview;
    private RatingBar ratingBar;
    private TextView tvRating;
    private TextView tvReleaseDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Intent intent = getIntent();
        Movie selectedMovie = intent.getParcelableExtra(MainActivity.MOVIE_TAG);
        Log.d(TAG, "DetailActivity started with Movie " + selectedMovie.getTitle());

        headerImage = (ImageView) findViewById(R.id.header_image);
        thumbnail = (ImageView) findViewById(R.id.thumbnail);
        originalTitle = (TextView) findViewById(R.id.tv_original_title);
        tvOverview = (TextView) findViewById(R.id.tv_overview);
        tvReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        tvRating = (TextView) findViewById(R.id.tv_rating);
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);

        Picasso.with(this).load(NetworkUtils.buildUrlForImages(selectedMovie.getBackdropPath()).toString()).into(headerImage);
        Picasso.with(this).load(NetworkUtils.buildUrlForImages(selectedMovie.getPosterPath()).toString()).into(thumbnail);
        originalTitle.setText(selectedMovie.getOriginalTitle());
        tvOverview.setText(selectedMovie.getOverview());
        tvReleaseDate.setText(selectedMovie.getReleaseDate());
        ratingBar.setRating(returnRatingBase5(selectedMovie.getVoteAverage()));
        tvRating.setText(returnRatingString(selectedMovie.getVoteAverage()));
        Log.d(TAG, "DetailActivity started with Movie " + selectedMovie.getVoteAverage());
    }

    private String returnRatingString(Integer voteAverage){
        return Integer.toString(voteAverage) + "/10";
    }

    private Float returnRatingBase5(Integer voteAverage){
        return (float) voteAverage/2;
    }
}
