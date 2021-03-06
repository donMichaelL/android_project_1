package com.example.android.popularmovies.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.Adapters.MovieAdapter;
import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.api.ApiClient;
import com.example.android.popularmovies.api.ApiInterface;
import com.example.android.popularmovies.models.ApiError;
import com.example.android.popularmovies.models.Movie;
import com.example.android.popularmovies.NetworkUtils.NetworkUtils;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.models.MovieResponse;
import com.facebook.stetho.Stetho;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener, MovieAdapter.OnLoadMoreListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getName();

    public static String API_KEY = BuildConfig.TMDB_API_KEY; ;
    private static final int CURSOR_LOADER_ID = 0;


    public static final String MOVIE_TAG = "movieObj";
    private static final String MOVIE_ARRAY_LIST = "movie_list";
    private static final String CHOICE = "choice";

    private ArrayList<Movie> movieArrayList;
    private int totalPages;
    private int currentPage = 0;
    private MovieAdapter movieAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView movieRecyclerView;
    private TextView tvErrorMsg;
    private ProgressBar pgLoading;

    private static final String FAVOURITE_CHOICE = "favorite";
    private String choice;

    private BroadcastReceiver internetChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Stetho.initializeWithDefaults(this);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        movieRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movie);
        tvErrorMsg = (TextView) findViewById(R.id.tv_error_msg);
        pgLoading = (ProgressBar) findViewById(R.id.pg_loading);

        movieRecyclerView.setLayoutManager(new GridLayoutManager(getBaseContext(), getResources().getInteger(R.integer.movies_per_row), GridLayoutManager.VERTICAL, false));
        movieAdapter = new MovieAdapter(this, this, this);
        movieRecyclerView.setHasFixedSize(true);
        movieRecyclerView.setAdapter(movieAdapter);
        movieArrayList = new ArrayList<>();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (choice.equals(FAVOURITE_CHOICE)) {
                    movieAdapter.setMovieArrayList(null, true);
                    getSupportLoaderManager().restartLoader(CURSOR_LOADER_ID, null, MainActivity.this);
                }else {
                    clearBeforeReloadData();
                    createTaskForRetrievingMovieData(choice);
                }

            }
        });

        if(savedInstanceState == null || !savedInstanceState.containsKey(MOVIE_ARRAY_LIST)){
            choice = NetworkUtils.ORDERING_POPULARITY;
            createTaskForRetrievingMovieData(choice);
        } else {
            movieArrayList = savedInstanceState.getParcelableArrayList(MOVIE_ARRAY_LIST);
            choice = savedInstanceState.getString(CHOICE);
            if (choice.equals(FAVOURITE_CHOICE))
                movieAdapter.setMovieArrayList(movieArrayList, true);
            else
                movieAdapter.setMovieArrayList(movieArrayList, false);
        }

        internetChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!choice.equals(FAVOURITE_CHOICE) && NetworkUtils.isOnline(context)) {
                   // makeOrderingQueryInMovieDb(choice);
                }
            }
        };
    }

    private void createTaskForRetrievingMovieData(String orderingParam){
        if(NetworkUtils.isOnline(this)){
            showLoading();
            ApiInterface apiInterface = ApiClient.retrofitVideoBuilder().create(ApiInterface.class);
            Call<MovieResponse> call = apiInterface.getMoviesWithFilter(orderingParam, MainActivity.API_KEY, Integer.toString(currentPage+1));
            Log.d(TAG, call.request().url().toString());
            call.enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    if (response.isSuccessful()){
                        totalPages = Integer.parseInt(response.body().getTotalPages());
                        currentPage = Integer.parseInt(response.body().getPage());
                        movieArrayList.addAll(response.body().getResults());
                        if (currentPage == totalPages) {
                            movieAdapter.setMovieArrayList(movieArrayList, true);
                        }else {
                            movieAdapter.setMovieArrayList(movieArrayList, false);
                        }
                        showMovieData();
                    } else {
                        ApiError apiError = NetworkUtils.parseError(response);
                        showErrorMsg(apiError.getMessage());
                    }
                }
                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {
                    showErrorMsg(getResources().getString(R.string.error_msg));
                }
            });

        } else{
            showErrorMsg(getResources().getString(R.string.error_msg));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (internetChangeReceiver != null) {
            this.unregisterReceiver(internetChangeReceiver);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIE_ARRAY_LIST, movieArrayList);
        outState.putString(CHOICE, choice);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_by_popularity:
                clearBeforeReloadData();
                choice = NetworkUtils.ORDERING_POPULARITY;
                createTaskForRetrievingMovieData(choice);
                break;
            case R.id.sort_by_votes:
                clearBeforeReloadData();
                choice = NetworkUtils.ORDERING_VOTES;
                createTaskForRetrievingMovieData(choice);
                break;
            case R.id.sort_by_favourite:
                clearBeforeReloadData();
                choice = FAVOURITE_CHOICE;
                getSupportLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
                break;
        }
        return true;
    }

    private void clearBeforeReloadData(){
        movieAdapter.setMovieArrayList(null, false);
        movieArrayList = new ArrayList<>();
        currentPage = 0;
    }

    private void showErrorMsg(String message){
        movieRecyclerView.setVisibility(View.INVISIBLE);
        tvErrorMsg.setText(message);
        tvErrorMsg.setVisibility(View.VISIBLE);
        pgLoading.setVisibility(View.INVISIBLE);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void showMovieData(){
        movieRecyclerView.setVisibility(View.VISIBLE);
        tvErrorMsg.setVisibility(View.INVISIBLE);
        pgLoading.setVisibility(View.INVISIBLE);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void showLoading() {
        if (currentPage == 0)
            movieRecyclerView.setVisibility(View.INVISIBLE);
        tvErrorMsg.setVisibility(View.INVISIBLE);
        pgLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Movie clickedMovie = movieArrayList.get(clickedItemIndex);
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(MOVIE_TAG, clickedMovie);
        startActivity(intent);
    }

    @Override
    public void onLoadMore() {
        createTaskForRetrievingMovieData(choice);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (internetChangeReceiver != null) {
            IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            this.registerReceiver(internetChangeReceiver, intentFilter);
        }
        if (choice.equals(FAVOURITE_CHOICE)) {
            getSupportLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        pgLoading.setVisibility(View.VISIBLE);

        String[] projection = {
                MovieContract.MovieEntry._ID,
                MovieContract.MovieEntry.COLUMN_NAME_POSTER_PATH,
                MovieContract.MovieEntry.COLUMN_NAME_BACKDROP_PATH,
                MovieContract.MovieEntry.COLUMN_NAME_ORIGINAL_TITLE,
                MovieContract.MovieEntry.COLUMN_NAME_TITLE,
                MovieContract.MovieEntry.COLUMN_NAME_ORIGINAL_LANGUAGE,
                MovieContract.MovieEntry.COLUMN_NAME_OVERVIEW,
                MovieContract.MovieEntry.COLUMN_NAME_RELEASE_DATE,
                MovieContract.MovieEntry.COLUMN_NAME_POPULARITY,
                MovieContract.MovieEntry.COLUMN_NAME_VOTE_COUNT,
                MovieContract.MovieEntry.COLUMN_NAME_VOTE_AVERAGE,
                MovieContract.MovieEntry.COLUMN_NAME_ID
        };

        return new CursorLoader(MainActivity.this, MovieContract.MovieEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(TAG, "Calling Loader.");
        pgLoading.setVisibility(View.INVISIBLE);
        swipeRefreshLayout.setRefreshing(false);
        if (cursor != null && cursor.getCount() > 0) {
            movieArrayList = new ArrayList<>();
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                Movie movie = new Movie(
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_ID)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_POSTER_PATH)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_BACKDROP_PATH)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_TITLE)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_ORIGINAL_TITLE)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_ORIGINAL_LANGUAGE)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_OVERVIEW)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_POPULARITY)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_VOTE_COUNT)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_VOTE_AVERAGE)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_RELEASE_DATE))
                );
                movieArrayList.add(movie);
            }
            Log.d(TAG, Integer.toString(movieArrayList.size()));
            movieAdapter.setMovieArrayList(movieArrayList, true);
            showMovieData();
        } else {
            movieAdapter.setMovieArrayList(null, true);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieAdapter.setMovieArrayList(null, true);
    }

}
