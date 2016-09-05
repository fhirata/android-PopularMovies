package com.example.fabiohh.popularmovies;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fabiohh.popularmovies.models.MovieItem;

/**
 * Created by fabiohh on 8/23/16.
 */
public class DetailFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();

        if (intent == null || !intent.hasExtra(MovieItem.PARCELABLE_KEY)) {
            return rootView;
        }

        Bundle bundle = intent.getExtras();

        MovieItem movieItem = (MovieItem) bundle.getParcelable(MovieItem.PARCELABLE_KEY);
        updateMovieDetail(movieItem, rootView);

        return rootView;
    }

    public void updateMovieDetail(MovieItem movieItem, View view) {
        TextView yearTextView = (TextView) view.findViewById(R.id.text_release_year);
        yearTextView.setText(movieItem.getReleaseYear());

        TextView voteTextView = (TextView) view.findViewById(R.id.text_votes);
        voteTextView.setText(movieItem.getAverageVote() + "/10 (" + movieItem.getVoteCount() + " votes)");

        TextView descTextView = (TextView) view.findViewById(R.id.text_desc);
        descTextView.setText(movieItem.getDescription());

        TextView headerTitleTextView = (TextView) view.findViewById(R.id.text_header_title);
        headerTitleTextView.setText(movieItem.getTitle());

        String url = "";
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            url = movieItem.getImgUrl();
        } else {
            url = movieItem.getBackDropUrl();
        }
        ImageView imageView = (ImageView) view.findViewById(R.id.image_movie);
        Glide.with(this)
                .load(url)
                .into(imageView);

    }
}