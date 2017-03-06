package com.example.android.popularmovies.Activities;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.api.ApiClient;
import com.example.android.popularmovies.api.ApiInterface;
import com.example.android.popularmovies.models.Movie;
import com.example.android.popularmovies.NetworkUtils.NetworkUtils;
import com.example.android.popularmovies.models.Review;
import com.example.android.popularmovies.Adapters.ReviewAdapter;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.models.ReviewResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MovieDetailActivity extends AppCompatActivity {
    private static final String TAG = MovieDetailActivity.class.getName();

    public static final String MOVIE_ID = "movie_id";
    private static final String REVIEW_ARRAY_LIST_LABEL ="review_array_list";
    private static final String USER_LIKES_MOVIE = "user_likes_movie";
    private static final int NO_COMMENT = 0;

    private ImageView headerImage;
    private ImageView thumbnail;
    private TextView originalTitle;
    private TextView tvOverview;
    private RatingBar ratingBar;
    private TextView tvRating;
    private TextView tvReleaseDate;
    private Button btnVideoDetailActivity;
    private RecyclerView recyclerView;
    private TextView tvNoComments;
    private TextView tvErrorMsgReview;
    private ProgressBar pgLoadingReview;
    private Button btnLike;
    private boolean userLikesMovie;

    private ArrayList<Review> reviewArrayList;
    private ReviewAdapter reviewAdapter;

    private BroadcastReceiver internetChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Intent intent = getIntent();

        if (intent == null  || !intent.hasExtra(MainActivity.MOVIE_TAG)) {
            finish();
        }

        final Movie selectedMovie = intent.getParcelableExtra(MainActivity.MOVIE_TAG);
        Log.d(TAG, "DetailActivity started with Movie " + selectedMovie.getId());

        headerImage = (ImageView) findViewById(R.id.header_image);
        thumbnail = (ImageView) findViewById(R.id.thumbnail);
        originalTitle = (TextView) findViewById(R.id.tv_original_title);
        tvOverview = (TextView) findViewById(R.id.tv_overview);
        tvReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        tvRating = (TextView) findViewById(R.id.tv_rating);
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        btnVideoDetailActivity = (Button) findViewById(R.id.btn_video_detail_activity);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_reviews);
        tvNoComments = (TextView) findViewById(R.id.tv_no_comments);
        tvErrorMsgReview = (TextView) findViewById(R.id.tv_error_msg_review);
        pgLoadingReview = (ProgressBar) findViewById(R.id.pg_loading_review);
        btnLike = (Button) findViewById(R.id.btn_like_movie);

        reviewArrayList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter();


        Picasso.with(this).load(NetworkUtils.buildUrlForImages(selectedMovie.getBackdropPath()).toString()).into(headerImage);
        Picasso.with(this).load(NetworkUtils.buildUrlForImages(selectedMovie.getPosterPath()).toString()).into(thumbnail);
        originalTitle.setText(selectedMovie.getOriginalTitle());
        tvOverview.setText(selectedMovie.getOverview());
        tvReleaseDate.setText(selectedMovie.getReleaseDate());
        ratingBar.setRating(returnRatingBase5(Float.parseFloat(selectedMovie.getVoteAverage())));
        tvRating.setText(selectedMovie.getVoteAverage() + "/10");
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(reviewAdapter);

        btnVideoDetailActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MovieDetailActivity.this, VideoDetailActivity.class);
                intent1.putExtra(MOVIE_ID, selectedMovie.getId());
                startActivity(intent1);
            }
        });


        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            boolean result;
            if (userLikesMovie) {
                result = unlikeAMovie(selectedMovie);
            }else {
                result = likeAMovie(selectedMovie);
            }

            if (result){
                Toast.makeText(MovieDetailActivity.this, "Ok !", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(MovieDetailActivity.this, "Problem. Try Again", Toast.LENGTH_SHORT).show();
            }
            }
        });


        if (savedInstanceState != null) {
            reviewArrayList = savedInstanceState.getParcelableArrayList(REVIEW_ARRAY_LIST_LABEL);
            reviewAdapter.setArrayListReview(reviewArrayList);
            userLikesMovie = savedInstanceState.getBoolean(USER_LIKES_MOVIE);
        } else {
            startRetrieveReviews(selectedMovie.getId());
            userLikesMovie = checkUserLikesTheMovie(selectedMovie.getId());
        }

        if (userLikesMovie) {
            btnLike.setText("Unlike");
        }
        else {
            btnLike.setText("Like");
        }
        internetChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (NetworkUtils.isOnline(context)) {
                    Log.d(TAG, "HELLO");
                    startRetrieveReviews(selectedMovie.getId());
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (internetChangeReceiver != null) {
            IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            this.registerReceiver(internetChangeReceiver, intentFilter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (internetChangeReceiver != null) {
            this.unregisterReceiver(internetChangeReceiver);
        }
    }

    private boolean checkUserLikesTheMovie(String id) {
        Cursor cursor = getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[] {MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_NAME_ORIGINAL_TITLE, MovieContract.MovieEntry.COLUMN_NAME_ID},
                MovieContract.MovieEntry.COLUMN_NAME_ID + "= ?",
                new String[]{id},
                null
        );
        if (cursor.getCount() < 1) return false;
        cursor.close();
        return true;
    }

    private boolean likeAMovie(Movie selectedMovie) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_NAME_ORIGINAL_TITLE, selectedMovie.getOriginalTitle());
        contentValues.put(MovieContract.MovieEntry.COLUMN_NAME_ID, selectedMovie.getId());
        contentValues.put(MovieContract.MovieEntry.COLUMN_NAME_TITLE, selectedMovie.getTitle());
        contentValues.put(MovieContract.MovieEntry.COLUMN_NAME_POSTER_PATH, selectedMovie.getPosterPath());
        contentValues.put(MovieContract.MovieEntry.COLUMN_NAME_BACKDROP_PATH, selectedMovie.getBackdropPath());
        contentValues.put(MovieContract.MovieEntry.COLUMN_NAME_OVERVIEW, selectedMovie.getOverview());
        contentValues.put(MovieContract.MovieEntry.COLUMN_NAME_RELEASE_DATE, selectedMovie.getReleaseDate());
        contentValues.put(MovieContract.MovieEntry.COLUMN_NAME_ORIGINAL_LANGUAGE, selectedMovie.getOriginalLanguage());
        contentValues.put(MovieContract.MovieEntry.COLUMN_NAME_POPULARITY, selectedMovie.getPopularity());
        contentValues.put(MovieContract.MovieEntry.COLUMN_NAME_VOTE_COUNT, selectedMovie.getVoteCount());
        contentValues.put(MovieContract.MovieEntry.COLUMN_NAME_VOTE_AVERAGE, selectedMovie.getVoteAverage());
        Uri resultUri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
        if (resultUri == null) return false;
        btnLike.setText("Unlike");
        userLikesMovie = true;
        return true;
    }

    private boolean unlikeAMovie(Movie selectedMovie){
        int rowCount = getContentResolver().delete(
                MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.COLUMN_NAME_ID + " = ?",
                new String[]{selectedMovie.getId()});
        if (rowCount < 1) return false;
        btnLike.setText("Like");
        userLikesMovie = false;
        return true;
    }

    private void startRetrieveReviews(String id) {
        if (NetworkUtils.isOnline(this)){
            showLoading();
            Retrofit retrofit = ApiClient.retrofitVideoBuilder();
            ApiInterface retrofitInterface = retrofit.create(ApiInterface.class);
            Call<ReviewResponse> call = retrofitInterface.getReviewFromId(id, MainActivity.API_KEY);
            Log.d(TAG, call.request().url().toString());
            call.enqueue(new Callback<ReviewResponse>() {
                @Override
                public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                    ArrayList<Review> results = response.body().getResults();
                    if (results != null) {
                        if (results.size() == NO_COMMENT){
                            showNoComments();
                        } else {
                            showReviews();
                        }
                        reviewAdapter.setArrayListReview(results);
                    } else {
                        showErrorMsg();
                    }
                }

                @Override
                public void onFailure(Call<ReviewResponse> call, Throwable t) {
                    showErrorMsg();
                }
            });
        } else {
            showErrorMsg();
        }
    }

    private Float returnRatingBase5(Float voteAverage){
        return voteAverage/2;
    }


    private void showReviews() {
        tvNoComments.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        tvErrorMsgReview.setVisibility(View.INVISIBLE);
        pgLoadingReview.setVisibility(View.INVISIBLE);
    }

    private void showLoading(){
        tvNoComments.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        tvErrorMsgReview.setVisibility(View.INVISIBLE);
        pgLoadingReview.setVisibility(View.VISIBLE);
    }

    private void showErrorMsg() {
        tvNoComments.setVisibility(View.INVISIBLE);
        tvErrorMsgReview.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        pgLoadingReview.setVisibility(View.INVISIBLE);
    }

    private void showNoComments(){
        recyclerView.setVisibility(View.INVISIBLE);
        tvNoComments.setVisibility(View.VISIBLE);
        tvErrorMsgReview.setVisibility(View.INVISIBLE);
        pgLoadingReview.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(REVIEW_ARRAY_LIST_LABEL, reviewArrayList);
        outState.putBoolean(USER_LIKES_MOVIE, userLikesMovie);
        super.onSaveInstanceState(outState);
    }
}
