package com.example.fabiohh.popularmovies;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fabiohh.popularmovies.db.MovieContentManager;
import com.example.fabiohh.popularmovies.db.MovieContract;
import com.example.fabiohh.popularmovies.models.MovieItem;
import com.example.fabiohh.popularmovies.services.MoviesService;

/**
 * Created by fabiohh on 8/23/16.
 * App crash based on the order of loader layoutManager initialization:
 * http://stackoverflow.com/questions/27416834/app-crashing-when-trying-to-use-recyclerview-on-android-5-0
 */
public class DetailFragment extends Fragment {

    TrailerAdapter trailerAdapter;
    ReviewAdapter reviewAdapter;
    long movieId;

    private RecyclerView mTrailerRecyclerView;
    private RecyclerView.Adapter mTrailerAdapter;

    private RecyclerView mReviewRecyclerView;
    private RecyclerView.Adapter mReviewAdapter;
    private RecyclerView.LayoutManager mReviewLayoutManager;
    private RecyclerView.LayoutManager mTrailerLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();

        if (intent == null || !intent.hasExtra("movie_item_uri")) {
            return rootView;
        }

        final Uri uri = intent.getParcelableExtra("movie_item_uri");
        MovieItem movieItem = MovieContentManager.getMovieItem(this.getActivity(), uri);
        movieId = MovieContract.MovieEntry.getMovieIdFromUri(uri);

        mReviewRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_review_carousel);
        mTrailerRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_trailer_carousel);

        // Check if is already added, if so show pressed state
        final Button favoriteButton = (Button) rootView.findViewById(R.id.favorite_btn);
        final TextView favoriteTextView = (TextView) rootView.findViewById(R.id.favorite_label);

        if (MovieContentManager.containsMovieId(rootView.getContext(), movieId)) {
            favoriteButton.setBackgroundResource(R.drawable.ic_favorite_pressed);
            favoriteTextView.setText(getString(R.string.added_to_favorite));
        }

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!MovieContentManager.containsMovieId(view.getContext(), movieId)) {
                    // Add to Favorites table
                    MovieContentManager.saveToFavorites(view.getContext(), movieId);
                    favoriteButton.setBackgroundResource(R.drawable.ic_favorite_pressed);
                    favoriteTextView.setText(getString(R.string.added_to_favorite));
                    Toast.makeText(view.getContext(), "Added to Favorites!", Toast.LENGTH_SHORT).show();
                } else {
                    // Remove from Favorites table
                    MovieContentManager.removeFromFavorites(view.getContext(), movieId);
                    favoriteButton.setBackgroundResource(R.drawable.ic_favorite);
                    favoriteTextView.setText(getString(R.string.add_to_favorite));
                    Toast.makeText(view.getContext(), "Removed from Favorites!", Toast.LENGTH_SHORT).show();
                }
            }
        });

                updateMovieDetail(movieItem, rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Improve performance if we know that changes in content do not change
        // the layout size of the RecyclerView
        mTrailerRecyclerView.setHasFixedSize(true);
        mReviewRecyclerView.setHasFixedSize(true);

        mTrailerLayoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mTrailerAdapter = new TrailerAdapter(this.getActivity());
        mTrailerRecyclerView.setAdapter(mTrailerAdapter);
        mTrailerRecyclerView.setLayoutManager(mTrailerLayoutManager);


        mReviewLayoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mReviewAdapter = new ReviewAdapter(this.getActivity());
        mReviewRecyclerView.setAdapter(mReviewAdapter);
        mReviewRecyclerView.setLayoutManager(mReviewLayoutManager);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MoviesService reviewsService = new MoviesService(this.getActivity(),  MoviesFragment.MOVIE_FETCH_MODE_REVIEWS,  movieId, mReviewAdapter);
        reviewsService.execute();

        MoviesService trailersService = new MoviesService(this.getActivity(),  MoviesFragment.MOVIE_FETCH_MODE_TRAILERS,  movieId, mTrailerAdapter);
        trailersService.execute();
    }

    public void updateMovieDetail(MovieItem movieItem, View view) {
        ImageView backgroundHeaderImage = (ImageView) view.findViewById(R.id.background_header_image);

        Glide
                .with(this)
                .load(movieItem.getBackDropUrl())
                .into(backgroundHeaderImage);

        TextView yearTextView = (TextView) view.findViewById(R.id.text_release_year);
        yearTextView.setText(movieItem.getReleaseYear());

        TextView voteTextView = (TextView) view.findViewById(R.id.text_votes);
        voteTextView.setText(movieItem.getAverageVote() + "/10 (" + movieItem.getVoteCount() + " votes)");

        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        ratingBar.setRating(Float.valueOf(movieItem.getAverageVote())/2);

        TextView descTextView = (TextView) view.findViewById(R.id.text_desc);
        descTextView.setText(movieItem.getDescription());

        TextView headerTitleTextView = (TextView) view.findViewById(R.id.text_header_title);
        headerTitleTextView.setText(movieItem.getTitle());
        if (movieItem.getBackDropUrl() != null) {
            headerTitleTextView.setBackgroundColor(Color.TRANSPARENT);
        }

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
