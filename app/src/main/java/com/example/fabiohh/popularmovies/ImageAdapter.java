package com.example.fabiohh.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.fabiohh.popularmovies.models.MovieItem;

import java.util.ArrayList;

/**
 * Created by fabiohh on 8/25/16.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<MovieItem> mMovieItems;

    public ImageAdapter(Context c, ArrayList<MovieItem> movieItems) {
        mContext = c;
        mMovieItems = movieItems;
    }

    public int getCount() {
        if (mMovieItems != null) {
            return mMovieItems.size();
        }
        return 0;
    }

    public Object getItem(int position) {
        if (position < mMovieItems.size()) {
            return mMovieItems.get(position);
        }
        return null;
    }

    public void setData(ArrayList<MovieItem> movieItems) {
        mMovieItems = movieItems;
        notifyDataSetChanged();
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            View view = inflater.inflate(R.layout.grid_item, parent, false);
            imageView = (ImageView) view.findViewById(R.id.grid_item_image);
        } else {
            imageView = (ImageView) convertView;
        }

        Glide
                .with(mContext)
                .load(mMovieItems.get(position).getImgUrl())
                .into(imageView);

        return imageView;
    }
}
