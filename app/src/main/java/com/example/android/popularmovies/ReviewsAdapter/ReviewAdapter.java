package com.example.android.popularmovies.ReviewsAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.Reviews.Review;

import java.util.ArrayList;

/**
 * Created by Michalis on 2/6/2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {
    ArrayList<Review> arrayListReview;



    @Override
    public ReviewAdapter.ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.review_item, parent, false);
        return new ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ReviewAdapterViewHolder holder, int position) {
        holder.tvAuthor.setText(arrayListReview.get(position).getAuthor());
        holder.tvComment.setText(arrayListReview.get(position).getContent());
    }

    public void setArrayListReview(ArrayList<Review> arrayListReview) {
        this.arrayListReview = arrayListReview;
    }

    @Override
    public int getItemCount() {
        if (arrayListReview == null) return 0;
        return arrayListReview.size();
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder{
        TextView tvAuthor;
        TextView tvComment;

        public ReviewAdapterViewHolder(View itemView) {
            super(itemView);
            tvAuthor = (TextView) itemView.findViewById(R.id.tv_author);
            tvComment = (TextView) itemView.findViewById(R.id.tv_comment);
        }
    }
}
