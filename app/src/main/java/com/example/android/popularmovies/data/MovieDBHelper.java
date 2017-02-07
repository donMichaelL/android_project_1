package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Michalis on 2/6/2017.
 */

public class MovieDBHelper extends SQLiteOpenHelper {
    public static final String TAG = MovieDBHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 4;

    private static final String DROP_TABLE_FAVORITE_MOVIE =  "DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME;
    private static final String CREATE_FAVORITE_MOVIE_ENTRY =
            "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
            MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY," +
            MovieContract.MovieEntry.COLUMN_NAME_ORIGINAL_TITLE + " TEXT," +
            MovieContract.MovieEntry.COLUMN_NAME_ID + " TEXT," +
            MovieContract.MovieEntry.COLUMN_NAME_TITLE + " TEXT, " +
            MovieContract.MovieEntry.COLUMN_NAME_POSTER_PATH + " TEXT," +
            MovieContract.MovieEntry.COLUMN_NAME_BACKDROP_PATH + " TEXT," +
            MovieContract.MovieEntry.COLUMN_NAME_RELEASE_DATE + " TEXT, "+
            MovieContract.MovieEntry.COLUMN_NAME_OVERVIEW + " TEXT, " +
            MovieContract.MovieEntry.COLUMN_NAME_ORIGINAL_LANGUAGE + " TEXT, " +
            MovieContract.MovieEntry.COLUMN_NAME_POPULARITY + " TEXT, " +
            MovieContract.MovieEntry.COLUMN_NAME_VOTE_COUNT + " TEXT, " +
            MovieContract.MovieEntry.COLUMN_NAME_VOTE_AVERAGE + " TEXT " +
            ")";


    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "Database just created.");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FAVORITE_MOVIE_ENTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Database just created.");
        db.execSQL(DROP_TABLE_FAVORITE_MOVIE);
        onCreate(db);
    }
}
