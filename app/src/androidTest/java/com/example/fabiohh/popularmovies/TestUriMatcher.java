package com.example.fabiohh.popularmovies;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.example.fabiohh.popularmovies.db.MovieContentProvider;
import com.example.fabiohh.popularmovies.db.MovieContract;

import static com.example.fabiohh.popularmovies.db.MovieContentProvider.TOP_RATED;
import static com.example.fabiohh.popularmovies.db.MovieContract.FAVORITES_PATH;
import static com.example.fabiohh.popularmovies.db.MovieContract.TOP_RATED_PATH;

/**
 * Created by fabiohh on 10/26/16.
 */

public class TestUriMatcher extends AndroidTestCase {
    private static final long TEST_MOVIE_ID = 123456L;

    private static final Uri TEST_TOPRATED_DIR = MovieContract.MovieEntry.buildMovieUri(TOP_RATED_PATH);
    private static final Uri TEST_MOVIE_ITEM = MovieContract.MovieEntry.buildMovieItemUri(TEST_MOVIE_ID);
    private static final Uri TEST_FAVORITE_DIR = MovieContract.MovieEntry.buildMovieUri(FAVORITES_PATH);
    private static final Uri TEST_FAVORITE_ITEM = MovieContract.FavoriteEntry.buildFavoritesUri(TEST_MOVIE_ID);

    public void testUriMatcher() {
        UriMatcher testMatcher = MovieContentProvider.buildUriMatcher();

        assertEquals("Error: Movie URI matched incorrectly", testMatcher.match(TEST_TOPRATED_DIR), TOP_RATED);
        assertEquals("Error: Movie With ID matched incorrectly", testMatcher.match(TEST_MOVIE_ITEM), MovieContentProvider.MOVIE_ITEM);
        assertEquals("Error: Favorites URI matched incorrectly", testMatcher.match(TEST_FAVORITE_DIR), MovieContentProvider.FAVORITES);
        assertEquals("Error: Movie With ID matched incorrectly", testMatcher.match(TEST_FAVORITE_ITEM), MovieContentProvider.FAVORITES_ITEM);
    }
}
