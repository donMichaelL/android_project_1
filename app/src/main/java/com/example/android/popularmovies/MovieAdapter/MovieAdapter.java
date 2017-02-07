package com.example.android.popularmovies.MovieAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.Movies.Movie;
import com.example.android.popularmovies.NetworkUtils.NetworkUtils;
import com.example.android.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Michalis on 1/27/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {
    private static final String TAG = MovieAdapter.class.getName();

    private ArrayList<Movie> movieArrayList;
    private Context context;
    private final ListItemClickListener mOnClickListener;

    public MovieAdapter(Context context, ListItemClickListener mOnClickListener) {
        this.context = context;
        this.mOnClickListener = mOnClickListener;
    }

    public interface ListItemClickListener{
        void onListItemClick(int clickedItemIndex);
    }

    public void setMovieArrayList(ArrayList<Movie> movieArrayList) {
        Log.d(TAG, "REFRESH");
        this.movieArrayList = movieArrayList;
        notifyDataSetChanged();
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.grid_movie_item, parent, false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        Movie bindMovie = movieArrayList.get(position);
        Log.d(TAG, "bindMovie.getPosterPath()");
        Picasso.with(context).load(NetworkUtils.buildUrlForImages(bindMovie.getPosterPath()).toString()).into(holder.imageViewPoster);
    }

    @Override
    public int getItemCount() {
        if(movieArrayList == null) return 0;
        return movieArrayList.size();
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageViewPoster ;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            this.imageViewPoster = (ImageView) itemView.findViewById(R.id.img_movie_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
