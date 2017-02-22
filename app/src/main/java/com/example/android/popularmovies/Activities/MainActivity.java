package com.example.android.popularmovies.Activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import com.example.android.popularmovies.Pojo.Movie;
import com.example.android.popularmovies.Parsers.MoviesParser;
import com.example.android.popularmovies.NetworkUtils.NetworkUtils;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.MovieContract;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener, MovieAdapter.OnLoadMoreListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getName();

    public static String API_KEY ;
    private static final int CURSOR_LOADER_ID = 0;


    public static final String MOVIE_TAG = "movieObj";
    private static final String MOVIE_ARRAY_LIST = "movie_list";
    private static final String CHOICE = "choice";


    private static final int NUM_COLS = 4;
    private ArrayList<Movie> movieArrayList;
    private int totalPages;
    private int currentPage = 1;
    private MovieAdapter movieAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView movieRecyclerView;
    private TextView tvErrorMsg;
    private ProgressBar pgLoading;

    private static final String FAVOURITE_CHOICE = "favorite";
    private String choice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        API_KEY = BuildConfig.TMDB_API_KEY;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        movieRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movie);
        tvErrorMsg = (TextView) findViewById(R.id.tv_error_msg);
        pgLoading = (ProgressBar) findViewById(R.id.pg_loading);

        movieRecyclerView.setLayoutManager(new GridLayoutManager(getBaseContext(), NUM_COLS, GridLayoutManager.VERTICAL, false));
        movieAdapter = new MovieAdapter(this, this, this);
        movieRecyclerView.setAdapter(movieAdapter);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (choice == FAVOURITE_CHOICE) {
                    movieAdapter.setMovieArrayList(null, true);
                    getSupportLoaderManager().restartLoader(CURSOR_LOADER_ID, null, MainActivity.this);
                }else {
                    movieArrayList = null;
                    currentPage = 1 ;
                    movieAdapter.setMovieArrayList(null, false);
                    createAsyncTaskForMovieData(choice);
                }

            }
        });

        if(savedInstanceState == null || !savedInstanceState.containsKey(MOVIE_ARRAY_LIST)){
            choice = NetworkUtils.ORDERING_POPULARITY;
            createAsyncTaskForMovieData(choice);
        } else {
            movieArrayList = savedInstanceState.getParcelableArrayList(MOVIE_ARRAY_LIST);
            choice = savedInstanceState.getString(CHOICE);
            if (choice == FAVOURITE_CHOICE)
                movieAdapter.setMovieArrayList(movieArrayList, true);
            else
                movieAdapter.setMovieArrayList(movieArrayList, false);
        }
    }

    private void createAsyncTaskForMovieData(String orderingParam){
        if(isOnline()){
            makeOrderingQueryInMovieDb(orderingParam);
        } else{
            showErrorMsg();
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIE_ARRAY_LIST, movieArrayList);
        outState.putString(CHOICE, choice);
        super.onSaveInstanceState(outState);
    }

    private void makeOrderingQueryInMovieDb(String orderingParam){
        URL requestURL = NetworkUtils.buildUrlForQueryOrderingMovieDB(orderingParam, currentPage);
        new MovieDBApiQueryTask().execute(requestURL);
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
                createAsyncTaskForMovieData(choice);
                break;
            case R.id.sort_by_votes:
                clearBeforeReloadData();
                choice = NetworkUtils.ORDERING_VOTES;
                createAsyncTaskForMovieData(choice);
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
        currentPage = 1;
        movieArrayList = null;
    }

    private void showErrorMsg(){
        movieRecyclerView.setVisibility(View.INVISIBLE);
        tvErrorMsg.setVisibility(View.VISIBLE);
    }

    private void showMovieData(){
        movieRecyclerView.setVisibility(View.VISIBLE);
        tvErrorMsg.setVisibility(View.INVISIBLE);
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
        if (currentPage < totalPages) {
            currentPage++;
            createAsyncTaskForMovieData(choice);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (choice == FAVOURITE_CHOICE) {
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




    private class MovieDBApiQueryTask extends AsyncTask<URL, Void, ArrayList<Movie>>{

        @Override
        protected void onPreExecute() {
            pgLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList doInBackground(URL... params) {
            Log.d(TAG, "Asynchronous Task is stating retrieving data.");
            URL requestUrl = params[0];
            try {
                String responseString = NetworkUtils.getResponseFromHttpUrl(requestUrl);
                totalPages = MoviesParser.getTotalPages(responseString);
                return MoviesParser.getSimpleStringFromJson(responseString);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(ArrayList<Movie> returnedMovieArrayList) {
            pgLoading.setVisibility(View.INVISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            if(returnedMovieArrayList != null ) {
                Log.d(TAG, "Data received with success");
                if (movieArrayList == null) {
                    movieArrayList = returnedMovieArrayList;
                }else {
                    movieArrayList.addAll(returnedMovieArrayList);
                }
                showMovieData();
                movieAdapter.setMovieArrayList(movieArrayList, false);
            }else {
                showErrorMsg();
            }

        }
    }

}
