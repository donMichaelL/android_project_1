package com.example.android.popularmovies.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.Pojo.Video;

import java.util.ArrayList;

/**
 * Created by Michalis on 2/3/2017.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoAdapterViewHolder>{
    private static final String TAG = VideoAdapter.class.getName();

    private final ListItemClickListener mOnClickListener;

    private ArrayList<Video> videoArrayList;


    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }


    public VideoAdapter(ListItemClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

    @Override
    public VideoAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_list_item,parent,false);
        return new VideoAdapterViewHolder(v);
    }


    @Override
    public void onBindViewHolder(VideoAdapterViewHolder holder, int position) {
        holder.tvTitle.setText(videoArrayList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        if (videoArrayList == null) return 0;
        return videoArrayList.size();
    }

    public class VideoAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvTitle;

        public VideoAdapterViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.video_item_id);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnClickListener.onListItemClick(getAdapterPosition());
        }
    }

    public void setArrayAdapter(ArrayList<Video> videoArrayList){
        this.videoArrayList = videoArrayList;
        notifyDataSetChanged();
    }
}
