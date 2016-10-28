package com.example.fabiohh.popularmovies;

import android.view.View;
import android.widget.ImageView;

/**
 * Created by fabiohh on 10/27/16.
 */

public class MovieViewHolder {
    public ImageView imageViewItem;

    public MovieViewHolder(View view) {
        imageViewItem = (ImageView) view.findViewById(R.id.image_movie);
    }
}
