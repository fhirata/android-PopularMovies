package com.example.fabiohh.popularmovies;

import android.net.Uri;

import com.example.fabiohh.popularmovies.db.MovieContract;

import junit.framework.TestCase;

/**
 * Created by fabiohh on 10/26/16.
 */

public class TestMovieContract extends TestCase {
    long TEST_MOVIE_ID = 12345L;

    public void testBuildMovieItem() {
        Uri movieUri = MovieContract.MovieEntry.buildMovieItemUri(TEST_MOVIE_ID);
        assertEquals(Long.toString(TEST_MOVIE_ID), movieUri.getLastPathSegment());
    }

    public void testGetMovieIdFromUri() {
        Uri movieUri = MovieContract.MovieEntry.buildMovieItemUri(TEST_MOVIE_ID);

        long movieId = MovieContract.MovieEntry.getMovieIdFromUri(movieUri);
        assertEquals(movieId, TEST_MOVIE_ID);
    }
}
