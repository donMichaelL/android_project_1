package com.example.android.popularmovies.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.Activities.MainActivity;
import com.example.android.popularmovies.Adapters.ReviewAdapter;
import com.example.android.popularmovies.NetworkUtils.NetworkUtils;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.api.ApiClient;
import com.example.android.popularmovies.api.ApiInterface;
import com.example.android.popularmovies.models.ApiError;
import com.example.android.popularmovies.models.Review;
import com.example.android.popularmovies.models.ReviewResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by michalis on 3/6/2017.
 */

public class ReviewFragment extends Fragment {
    private static final String TAG = Review.class.getName();

    public static final String REVIEW_ID = "review_id";

    private static final String REVIEW_ARRAY_LIST_LABEL ="review_array_list";
    private static final int NO_COMMENT = 0;

    private RecyclerView recyclerView;
    private TextView tvNoComments;
    private TextView tvErrorMsgReview;
    private ProgressBar pgLoadingReview;

    private ReviewAdapter reviewAdapter;
    private ArrayList<Review> reviewArrayList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_review, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_reviews);
        tvNoComments = (TextView) rootView.findViewById(R.id.tv_no_comments);
        tvErrorMsgReview = (TextView) rootView.findViewById(R.id.tv_error_msg_review);
        pgLoadingReview = (ProgressBar) rootView.findViewById(R.id.pg_loading_review);
        reviewArrayList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(reviewAdapter);
        if (savedInstanceState != null) {
            reviewArrayList = savedInstanceState.getParcelableArrayList(REVIEW_ARRAY_LIST_LABEL);
            reviewAdapter.setArrayListReview(reviewArrayList);
        } else {
            startRetrieveReviews(getArguments().getString(REVIEW_ID));
        }
        return rootView;
    }

    private void startRetrieveReviews(String id) {
        if (NetworkUtils.isOnline(getActivity())){
            showLoading();
            Retrofit retrofit = ApiClient.retrofitVideoBuilder();
            ApiInterface retrofitInterface = retrofit.create(ApiInterface.class);
            Call<ReviewResponse> call = retrofitInterface.getReviewFromId(id, MainActivity.API_KEY);
            Log.d(TAG, call.request().url().toString());
            call.enqueue(new Callback<ReviewResponse>() {
                @Override
                public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                    if (response.isSuccessful()) {
                        reviewArrayList = response.body().getResults();
                        if (reviewArrayList.size() == NO_COMMENT){
                            showNoComments();
                        } else {
                            showReviews();
                        }
                        reviewAdapter.setArrayListReview(reviewArrayList);
                    } else {
                        ApiError apiError = NetworkUtils.parseError(response);
                        showErrorMsg(apiError.getMessage());
                    }
                }
                @Override
                public void onFailure(Call<ReviewResponse> call, Throwable t) {
                    showErrorMsg(getResources().getString(R.string.error_msg));
                }
            });
        } else {
            showErrorMsg(getResources().getString(R.string.error_msg));
        }
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

    private void showErrorMsg(String message) {
        tvNoComments.setVisibility(View.INVISIBLE);
        tvErrorMsgReview.setText(message);
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
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(REVIEW_ARRAY_LIST_LABEL, reviewArrayList);
        super.onSaveInstanceState(outState);
    }
}
