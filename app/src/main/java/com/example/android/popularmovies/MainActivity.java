package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

import com.example.android.popularmovies.MovieAdapter.MovieAdapter;
import com.example.android.popularmovies.Movies.Movie;
import com.example.android.popularmovies.Movies.MoviesParser;
import com.example.android.popularmovies.NetworkUtils.NetworkUtils;
import com.example.android.popularmovies.data.MovieContract;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getName();

    public static String API_KEY ;
    private static final int CURSOR_LOADER_ID = 0;


    public static final String MOVIE_TAG = "movieObj";
    private static final String MOVIE_ARRAY_LIST = "movie_list";

    private static final int NUM_COLS = 4;
    private ArrayList<Movie> movieArrayList;
    private MovieAdapter movieAdapter;

    private RecyclerView movieRecyclerView;
    private TextView tvErrorMsg;
    private ProgressBar pgLoading;

    private boolean showFavourite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        API_KEY = getResources().getString(R.string.secret_api);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movieRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movie);
        tvErrorMsg = (TextView) findViewById(R.id.tv_error_msg);
        pgLoading = (ProgressBar) findViewById(R.id.pg_loading);

        movieRecyclerView.setLayoutManager(new GridLayoutManager(getBaseContext(), NUM_COLS, GridLayoutManager.VERTICAL, false));
        movieAdapter = new MovieAdapter(this, this);
        movieRecyclerView.setAdapter(movieAdapter);

        if(savedInstanceState == null || !savedInstanceState.containsKey(MOVIE_ARRAY_LIST)){
            createAsyncTaskForMovieData(NetworkUtils.ORDERING_POPULARITY);
        } else {
            movieArrayList = savedInstanceState.getParcelableArrayList(MOVIE_ARRAY_LIST);
            movieAdapter.setMovieArrayList(movieArrayList);
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
        super.onSaveInstanceState(outState);
    }

    private void makeOrderingQueryInMovieDb(String orderingParam){
        URL requestURL = NetworkUtils.buildUrlForQueryOrderingMovieDB(orderingParam);
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
                showFavourite = false;
                movieAdapter.setMovieArrayList(null);
                createAsyncTaskForMovieData(NetworkUtils.ORDERING_POPULARITY);
                break;
            case R.id.sort_by_votes:
                showFavourite = false;
                movieAdapter.setMovieArrayList(null);
                createAsyncTaskForMovieData(NetworkUtils.ORDERING_VOTES);
                break;
            case R.id.sort_by_favourite:
                movieAdapter.setMovieArrayList(null);
                getSupportLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
                showFavourite = true;
                break;
        }
        return true;
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
    public void onResume() {
        super.onResume();
        // TODO check orientation
        if (showFavourite) {
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
        Log.d(TAG, "Calling Loader");
        pgLoading.setVisibility(View.INVISIBLE);
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
            movieAdapter.setMovieArrayList(movieArrayList);
            showMovieData();
        } else {
            movieAdapter.setMovieArrayList(null);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieAdapter.setMovieArrayList(null);

    }


    private class MovieDBApiQueryTask extends AsyncTask<URL, Void, ArrayList<Movie>>{

        @Override
        protected void onPreExecute() {
            pgLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList doInBackground(URL... params) {
            Log.d(TAG, "Asynchronous Task is stating retrieving data");
            URL requestUrl = params[0];
            try {
                String responseString = NetworkUtils.getResponseFromHttpUrl(requestUrl);
                movieArrayList = MoviesParser.getSimpleStringFromJson(responseString);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return movieArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movieArrayList) {
            pgLoading.setVisibility(View.INVISIBLE);
            if(movieArrayList != null ) {
                Log.d(TAG, "Data received with success");
                showMovieData();
                movieAdapter.setMovieArrayList(movieArrayList);
            }else {
                showErrorMsg();
            }

        }
    }

}
