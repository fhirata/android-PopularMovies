package com.example.fabiohh.popularmovies.db;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.fabiohh.popularmovies.models.MovieItem;

/**
 * Created by fabiohh on 10/27/16.
 */

public class MovieContentManager {
    public static long saveToFavorites(Context context, long movieId) {
        ContentValues value = MovieContract.FavoriteEntry.createFavoriteRecord(movieId);
        Uri uri = MovieContract.FavoriteEntry.buildFavoritesUri(movieId);

        Uri returnUri = context.getContentResolver().insert(uri,
                value);

        long rowId = ContentUris.parseId(returnUri);
        if (rowId > 0) {
            return rowId;
        }
        return -1;
    }

    public static boolean removeFromFavorites(Context context, long movieId) {
        Uri uri = MovieContract.FavoriteEntry.buildFavoritesUri(movieId);

        int rows = context.getContentResolver().delete(uri,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{String.valueOf(movieId)});

        return (rows > 0);
    }

    public static boolean containsMovieId(Context context, long movieId) {
        Uri uri = MovieContract.FavoriteEntry.buildFavoritesUri(movieId);

        Cursor cursor = context.getContentResolver().query(uri,
                null,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{String.valueOf(movieId)},
                null,
                null);

        return (cursor.getCount() > 0);
    }

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
