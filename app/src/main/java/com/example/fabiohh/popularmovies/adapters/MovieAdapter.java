package com.example.fabiohh.popularmovies.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.fabiohh.popularmovies.DetailActivity;
import com.example.fabiohh.popularmovies.MainActivity;
import com.example.fabiohh.popularmovies.MovieViewHolder;
import com.example.fabiohh.popularmovies.MoviesFragment;
import com.example.fabiohh.popularmovies.R;
import com.example.fabiohh.popularmovies.db.MovieContract;

/**
 * Created by fabiohh on 10/26/16.
 */
public class MovieAdapter extends CursorAdapter {
    Activity mActivity;

    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.mActivity = (Activity) context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        MovieViewHolder viewHolder = new MovieViewHolder(view);

        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        MovieViewHolder viewHolder = (MovieViewHolder) view.getTag();

        String posterUrl = cursor.getString(MoviesFragment.COL_POSTER_URL);

        long movieId = cursor.getInt(MoviesFragment.COL_MOVIE_ID);
        final Uri uri = MovieContract.MovieEntry.buildMovieItemUri(movieId);
        final ImageView imageView1 = viewHolder.imageViewItem;
        final int position = cursor.getPosition();

        viewHolder.imageViewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mActivity.findViewById(R.id.container) != null) {

                    Intent intent = new Intent(view.getContext(), DetailActivity.class);
                    intent.putExtra("movie_item_uri", uri);
                    intent.putExtra("scroll_position", cursor.getPosition());

                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(mActivity, imageView1, "posterTransition");

                    mActivity.startActivityForResult(intent,
                            MoviesFragment.MOVIE_ITEM_POSITION_CODE,
                            options.toBundle());
                } else {
                    cursor.moveToPosition(position);
                    ((MainActivity) mContext).onMovieSelected(position, cursor);
                }
            }
        });

        Glide
                .with(mContext)
                .load(posterUrl)
                .dontAnimate()
                .into(viewHolder.imageViewItem);

    }
}
