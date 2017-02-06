package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.example.android.popularmovies.Movies.Movie;
import com.example.android.popularmovies.NetworkUtils.NetworkUtils;
import com.example.android.popularmovies.Reviews.Review;
import com.example.android.popularmovies.Reviews.ReviewParser;
import com.example.android.popularmovies.ReviewsAdapter.ReviewAdapter;
import com.example.android.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

public class MovieDetailActivity extends AppCompatActivity {
    private static final String TAG = MovieDetailActivity.class.getName();

    public static final String MOVIE_ID = "movie_id";
    private static final String REVIEW_ARRAY_LIST_LABEL ="review_array_list";
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

    private ArrayList<Review> reviewArrayList;
    private ReviewAdapter reviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Intent intent = getIntent();
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
        ratingBar.setRating(returnRatingBase5(selectedMovie.getVoteAverage()));
        tvRating.setText(returnRatingString(selectedMovie.getVoteAverage()));
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
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
            int rowCount = getContentResolver().delete(
                    MovieContract.MovieEntry.CONTENT_URI,
                    MovieContract.MovieEntry.COLUMN_NAME_ID + "= ?",
                    new String[]{Integer.toString(selectedMovie.getId())});
            Log.d(TAG, Integer.toString(rowCount));
            }
        });

        checkUserLikesTheMovie(selectedMovie.getId());

        if (savedInstanceState != null) {
            reviewArrayList = savedInstanceState.getParcelableArrayList(REVIEW_ARRAY_LIST_LABEL);
            reviewAdapter.setArrayListReview(reviewArrayList);
        } else {
            startAsyncTaskRetrieveReviews(selectedMovie.getId());
        }

    }

    private void checkUserLikesTheMovie(Integer id) {
        Cursor cursor = getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[] {MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_NAME_TITLE, MovieContract.MovieEntry.COLUMN_NAME_ID},
                MovieContract.MovieEntry.COLUMN_NAME_ID + "= ?",
                new String[]{Integer.toString(id)},
                null
        );
        if (cursor.getCount()< 1) {
            btnLike.setText("Like");
        } else {
            btnLike.setText("UnLike");
        }
    }

    private void likeAMovie(Movie selectedMovie) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_NAME_TITLE, selectedMovie.getOriginalTitle());
        contentValues.put(MovieContract.MovieEntry.COLUMN_NAME_ID, selectedMovie.getId());
        Uri resultUri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
        Log.d(TAG, resultUri.toString());
    }

    private void startAsyncTaskRetrieveReviews(Integer id) {
        if (isOnline()){
            new MovieDBReviewQueryTask().execute(NetworkUtils.buildUrlForReviewVideoMovieDB(id));
        } else {
            showErrorMsg();
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private String returnRatingString(Integer voteAverage){
        return Integer.toString(voteAverage) + "/10";
    }

    private Float returnRatingBase5(Integer voteAverage){
        return (float) voteAverage/2;
    }


    private class MovieDBReviewQueryTask extends AsyncTask<URL, Void, ArrayList<Review>>{
        @Override
        protected void onPreExecute() {

            pgLoadingReview.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Review> doInBackground(URL... params) {
            Log.d(TAG, "Asynchronous Task is stating retrieving review data.");
            URL  requestUrl = params[0];
            try {
                String result = NetworkUtils.getResponseFromHttpUrl(requestUrl);
                reviewArrayList = ReviewParser.getSimpleStringFromJson(result);
                return reviewArrayList;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Review> reviews) {
            pgLoadingReview.setVisibility(View.INVISIBLE);
            if (reviews != null){
                if (reviews.size() == NO_COMMENT) {
                    showNoComments();
                } else {
                    showReviews();
                }
                reviewAdapter.setArrayListReview(reviews);
            }else{
                showErrorMsg();
            }
        }
    }

    private void showReviews() {
        tvNoComments.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        tvErrorMsgReview.setVisibility(View.INVISIBLE);
    }

    private void showErrorMsg() {
        tvNoComments.setVisibility(View.INVISIBLE);
        tvErrorMsgReview.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    private void showNoComments(){
        recyclerView.setVisibility(View.INVISIBLE);
        tvNoComments.setVisibility(View.VISIBLE);
        tvErrorMsgReview.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(REVIEW_ARRAY_LIST_LABEL, reviewArrayList);
        super.onSaveInstanceState(outState);
    }
}
