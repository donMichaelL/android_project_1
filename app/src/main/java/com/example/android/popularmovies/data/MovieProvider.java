package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Michalis on 2/6/2017.
 */

public class MovieProvider extends ContentProvider {
    private static final String TAG = MovieProvider.class.getName();

    private MovieDBHelper movieDBHelper;
    private static final UriMatcher uriMatcher = buildUriMatcher();

    private static final int MOVIE_GET_ALL = 1;
    private static final int MOVIE_GET_ONE = 2;


    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.MovieEntry.TABLE_NAME, MOVIE_GET_ALL);
        matcher.addURI(authority, MovieContract.MovieEntry.TABLE_NAME + "/#", MOVIE_GET_ONE);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        movieDBHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor returnedCursor;
        switch (uriMatcher.match(uri)){
            case MOVIE_GET_ALL:
                returnedCursor = movieDBHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                return returnedCursor;
            case MOVIE_GET_ONE:
                returnedCursor = movieDBHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder
                );
                return returnedCursor;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case MOVIE_GET_ALL:
                return MovieContract.MovieEntry.CONTENT_DIR_TYPE;
            case MOVIE_GET_ONE:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE ;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (uriMatcher.match(uri)){
            case MOVIE_GET_ALL:
                long newRowId = movieDBHelper.getWritableDatabase()
                        .insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                Log.d(TAG, "New element with id" + Long.toString(newRowId));
                if (newRowId > 0){
                    //TODO check this line
                    getContext().getContentResolver().notifyChange(uri, null);
                    return MovieContract.MovieEntry.buildFlavorsUri(newRowId);
                }else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case MOVIE_GET_ALL:
                int deleteResult = movieDBHelper.getWritableDatabase()
                        .delete(MovieContract.MovieEntry.TABLE_NAME,
                                MovieContract.MovieEntry._ID + " = ?",
                                new String[] {String.valueOf(ContentUris.parseId(uri))});
                //TODO ask for reset _ID
                return deleteResult;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
