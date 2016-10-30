package com.example.fabiohh.popularmovies.db;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.example.fabiohh.popularmovies.MoviesFragment.MOVIE_FETCH_MODE_POPULAR;
import static com.example.fabiohh.popularmovies.MoviesFragment.MOVIE_FETCH_MODE_TOPRATED;

/**
 * Created by fabiohh on 10/26/16.
 */

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.fabiohh.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String FAVORITES_PATH = "favorites";
    public static final String MOVIES_PATH = "movies";
    public static final String TOP_RATED_PATH = "toprated";
    public static final String POPULAR_PATH = "popular";

    public static final class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER_BITMAP = "poster";
        public static final String COLUMN_POSTER_URL = "poster_url";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_BACKDROP_URL = "backdrop_url";
        public static final String COLUMN_TYPE = "type"; // Top Rated or Most Popular

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(MOVIES_PATH).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + MOVIES_PATH;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + MOVIES_PATH;

        public static Uri buildMovieItemUri(long movieId) {
            return ContentUris.withAppendedId(CONTENT_URI, movieId);
        }

        public static long getMovieIdFromUri(Uri uri) {
            return Long.valueOf(uri.getPathSegments().get(1));
        }

        // content://com.example.fabiohh.popularmovies/movies/1234  1234 = movie Id
        // content://com.example.fabiohh.popularmovies/movies/popular
        // content://com.example.fabiohh.popularmovies/movies/toprated
        // content://com.example.fabiohh.popularmovies/movies/favorite
        public static Uri buildMovieUri(String movieType) {
            if (movieType.equals(MOVIE_FETCH_MODE_POPULAR)) {
                return CONTENT_URI.buildUpon().appendPath(POPULAR_PATH).build();
            } else if (movieType.equals(MOVIE_FETCH_MODE_TOPRATED)) {
                return CONTENT_URI.buildUpon().appendPath(TOP_RATED_PATH).build();
            }
            return CONTENT_URI.buildUpon().appendPath(FAVORITES_PATH).build();
        }

        public static String getMovieTypeFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class FavoriteEntry implements BaseColumns {

        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(FAVORITES_PATH).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + FAVORITES_PATH;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + FAVORITES_PATH;

        public static Uri buildFavoritesUri(long movieId) {
            return ContentUris.withAppendedId(CONTENT_URI, movieId);
        }

        public static ContentValues createFavoriteRecord(long movieId) {
            ContentValues contentValue = new ContentValues();
            contentValue.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_ID, movieId);

            return contentValue;
        }

    }
}
