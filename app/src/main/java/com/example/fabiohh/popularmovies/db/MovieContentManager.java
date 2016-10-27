package com.example.fabiohh.popularmovies.db;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.fabiohh.popularmovies.models.MovieItem;

/**
 * Created by fabiohh on 10/27/16.
 */

public class MovieContentManager {
    public static MovieItem getMovieItem(Context context, Uri uri) {
        long movieId = MovieContract.MovieEntry.getMovieIdFromUri(uri);

        Cursor cursor = context.getContentResolver().query(uri,
                null,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{String.valueOf(movieId)},
                null,
                null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return MovieItem.fromCursor(cursor);
        }

        return null;
    }

    public static MovieItem getMovieById(Context context, long movieId) {
        Cursor cursor = context.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                null,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{String.valueOf(movieId)},
                null,
                null);

        if (cursor.getCount() > 0) {
            return MovieItem.fromCursor(cursor);
        }

        return null;
    }
}
