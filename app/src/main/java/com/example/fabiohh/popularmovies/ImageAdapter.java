package com.example.fabiohh.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
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
    private Activity mActivity;

    public ImageAdapter(Context c, ArrayList<MovieItem> movieItems) {
        mContext = c;
        mActivity = (Activity) c;
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
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            LayoutInflater inflater = mActivity.getLayoutInflater();
            View view = inflater.inflate(R.layout.grid_item, parent, false);
            imageView = (ImageView) view.findViewById(R.id.image_movie);
        } else {
            imageView = (ImageView) convertView;
        }

        final ImageView imageView1 = imageView;

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mActivity.findViewById(R.id.container) != null) {
                    Intent intent = new Intent(view.getContext(), DetailActivity.class);
                    intent.putExtra(MovieItem.PARCELABLE_KEY, mMovieItems.get(position));
                    intent.putExtra("scroll_position", position);

                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(mActivity, imageView1, "posterTransition");

                    mActivity.startActivityForResult(intent,
                            MoviesFragment.MOVIE_ITEM_POSITION_CODE,
                            options.toBundle());
                } else {
                    ((MainActivity) mContext).onMovieSelected(position, mMovieItems.get(position));
                }
            }
        });
        Glide
                .with(mContext)
                .load(mMovieItems.get(position).getImgUrl())
                .dontAnimate()
                .into(imageView);

        return imageView;
    }
}
