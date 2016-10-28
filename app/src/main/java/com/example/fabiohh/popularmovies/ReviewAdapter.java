package com.example.fabiohh.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fabiohh.popularmovies.models.MovieReview;

import java.util.List;

/**
 * Created by fabiohh on 10/27/16.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    List<MovieReview> reviewList;
    Context mContext;

    public ReviewAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_card, parent, false);
        ReviewViewHolder viewHolder = new ReviewViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        if (reviewList == null || reviewList.isEmpty()) {
            return;
        }
        final MovieReview movieReview = reviewList.get(position);

        holder.author.setText(movieReview.getAuthor());
        holder.content.setText(movieReview.getContent());

        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(movieReview.getUrl()));
                mContext.startActivity(intent);
            }
        });
    }

    public void setData(List<MovieReview> reviewList) {
        this.reviewList = reviewList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (reviewList != null && reviewList.size() > 0) {
            return reviewList.size();
        }
        return 2;
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView author;
        TextView content;

        public ReviewViewHolder(View itemView) {
            super(itemView);

            author = (TextView) itemView.findViewById(R.id.textview_reviewer_label);
            content = (TextView) itemView.findViewById(R.id.textview_description_label);
        }
    }
}
