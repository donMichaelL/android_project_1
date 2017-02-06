package com.example.android.popularmovies.data;

import android.provider.BaseColumns;

/**
 * Created by Michalis on 2/6/2017.
 */

public class MovieContract {

    public final class MovieEntry implements BaseColumns{
        public static final String TABLE_NAME = "favoriteMovie";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_ID = "movie_id";
    }

}
