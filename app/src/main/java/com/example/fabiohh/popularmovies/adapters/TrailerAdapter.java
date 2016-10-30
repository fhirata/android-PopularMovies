package com.example.fabiohh.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.fabiohh.popularmovies.R;
import com.example.fabiohh.popularmovies.models.MovieTrailer;

import java.util.List;

/**
 * Created by fabiohh on 10/27/16.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {
    private List<MovieTrailer> trailerList;
    private Context mContext;

    public TrailerAdapter(Context context) {
        this.mContext = context;
    }

    /**
     *
     * @param parent
     * @param viewType
     * @return
     *
     * Reference Recycler view never called:
     * http://stackoverflow.com/questions/28115553/recyclerview-not-call-any-adapter-method-oncreateviewholder-onbindviewholder
     */
    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_card, parent, false);
        TrailerViewHolder viewHolder = new TrailerViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        if (trailerList == null || trailerList.isEmpty()) {
            return;
        }
        final MovieTrailer movieTrailer = trailerList.get(position);

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(movieTrailer.getVideoUrl()));
                mContext.startActivity(intent);
            }
        });

        Glide.with(mContext)
                .load(movieTrailer.getThumbnailUrl())
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        if (trailerList != null && trailerList.size() > 0) {
            return trailerList.size();
        }
        return 0;
    }

    public void setData(List<MovieTrailer> trailerList) {
        this.trailerList = trailerList;
        notifyDataSetChanged();
    }

    public static class TrailerViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        ImageView thumbnail;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.play_image);
            thumbnail = (ImageView) itemView.findViewById(R.id.background_image);
        }
    }
}
