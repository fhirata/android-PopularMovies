package com.example.fabiohh.popularmovies.models;

/**
 * Created by fabiohh on 8/27/16.
 */
public interface IMovieInfo {
    // These are the names of the JSON objects that need to be extracted.
    String MOVIE_RESULTS = "results";
    String MOVIE_TITLE = "title";
    String MOVIE_IMAGE = "poster_path";
    String MOVIE_DESC = "overview";
    String MOVIE_VOTE_COUNT = "vote_count";
    String MOVIE_VOTE_AVG = "vote_average";
    String MOVIE_YEAR = "release_date";
}
