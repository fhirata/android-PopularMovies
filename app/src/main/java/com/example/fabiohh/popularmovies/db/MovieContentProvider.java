package com.example.fabiohh.popularmovies.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import static android.content.ContentUris.withAppendedId;
import static com.example.fabiohh.popularmovies.db.MovieContract.CONTENT_AUTHORITY;
import static com.example.fabiohh.popularmovies.db.MovieContract.FAVORITES_PATH;
import static com.example.fabiohh.popularmovies.db.MovieContract.MOVIES_PATH;
import static com.example.fabiohh.popularmovies.db.MovieContract.POPULAR_PATH;
import static com.example.fabiohh.popularmovies.db.MovieContract.TOP_RATED_PATH;

/**
 * Created by fabiohh on 10/26/16.
 *
 * reference for closing db:
 * http://stackoverflow.com/questions/23387405/android-database-cannot-perform-this-operation-because-the-connection-pool-has
 *
 *
 */

public class MovieContentProvider extends ContentProvider {
    MovieDatabaseHelper movieDatabaseHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    public static final int TOP_RATED = 100;
    public static final int POPULAR = 110;
    public static final int MOVIE_ITEM = 120;
    public static final int FAVORITES = 300;
    public static final int FAVORITES_ITEM = 310;

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(CONTENT_AUTHORITY, MOVIES_PATH + "/" + TOP_RATED_PATH , TOP_RATED);
        uriMatcher.addURI(CONTENT_AUTHORITY, MOVIES_PATH + "/" + POPULAR_PATH , POPULAR);
        uriMatcher.addURI(CONTENT_AUTHORITY, MOVIES_PATH + "/#", MOVIE_ITEM);
        uriMatcher.addURI(CONTENT_AUTHORITY, MOVIES_PATH + "/" + FAVORITES_PATH, FAVORITES);
        uriMatcher.addURI(CONTENT_AUTHORITY, FAVORITES_PATH + "/#", FAVORITES_ITEM);

        return uriMatcher;
    }

    private static final SQLiteQueryBuilder sFavoriteMoviesQueryBuilder;
    static {
        sFavoriteMoviesQueryBuilder = new SQLiteQueryBuilder();
        sFavoriteMoviesQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.FavoriteEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID +
                        " = " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.FavoriteEntry.COLUMN_MOVIE_ID);
    }

    private static final SQLiteQueryBuilder sMoviesWithFavoritesQueryBuilder;
    static {
        sMoviesWithFavoritesQueryBuilder = new SQLiteQueryBuilder();
        sMoviesWithFavoritesQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " LEFT JOIN " +
                        MovieContract.FavoriteEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID +
                        " = " + MovieContract.FavoriteEntry.TABLE_NAME +
                        "." + MovieContract.FavoriteEntry.COLUMN_MOVIE_ID);
    }
    @Override
    public boolean onCreate() {
        movieDatabaseHelper = new MovieDatabaseHelper(getContext());
        return false;
    }

    private Cursor getFavoriteMovies(Uri uri, String[] projection, String sortOrder) {

        return sFavoriteMoviesQueryBuilder.query(
                movieDatabaseHelper.getReadableDatabase(),
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getMoviesWithFavorite(Uri uri, String[] projection, String sortOrder) {
        String movieType = MovieContract.MovieEntry.getMovieTypeFromUri(uri);
        String selection = MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_TYPE + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(movieType)};

        return sMoviesWithFavoritesQueryBuilder.query(
                movieDatabaseHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = movieDatabaseHelper.getReadableDatabase();

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case TOP_RATED:
            case POPULAR: {
                retCursor = getMoviesWithFavorite(uri, projection, sortOrder);
            }
            break;
            case FAVORITES: {
                retCursor = getFavoriteMovies(uri, projection, sortOrder);
            }
            break;
            case MOVIE_ITEM: {
                retCursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null);
            }
            break;
            case FAVORITES_ITEM: {
                retCursor = db.query(MovieContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null);
            }
            break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (retCursor.getCount() > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case TOP_RATED:
            case POPULAR:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_ITEM:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case FAVORITES:
                return MovieContract.FavoriteEntry.CONTENT_TYPE;
            case FAVORITES_ITEM:
                return MovieContract.FavoriteEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int count = 0;
        final SQLiteDatabase db = movieDatabaseHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case TOP_RATED:
            case POPULAR: {
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(
                                MovieContract.MovieEntry.TABLE_NAME,
                                null,
                                value);
                        if (_id != 0) {
                            count++;
                        }
                    }
                } finally {
                    db.endTransaction();
                }
            }
            break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        }

        Cursor cursor = db.query(MovieContract.MovieEntry.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();

        Log.d("PROVIDER", "bulkInsert() called with: cursor = [" + cursor.getCount() + "], values = [" + values + "]");
        return super.bulkInsert(uri, values);
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        Uri returnUri;
        final SQLiteDatabase db = movieDatabaseHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case POPULAR:
            case TOP_RATED:
            case MOVIE_ITEM: {
                long _id = db.insert(
                        MovieContract.MovieEntry.TABLE_NAME,
                        null,
                        contentValues);
                returnUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, _id);
                break;
            }
            case FAVORITES_ITEM: {
                long _id = db.insert(
                        MovieContract.FavoriteEntry.TABLE_NAME,
                        null,
                        contentValues);
                returnUri = withAppendedId(MovieContract.FavoriteEntry.CONTENT_URI, _id);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db =movieDatabaseHelper.getWritableDatabase();
        int rowsDeleted = 0;
        switch(sUriMatcher.match(uri)) {
            case MOVIE_ITEM: {
                rowsDeleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
            }
            break;
            case FAVORITES_ITEM: {
                rowsDeleted = db.delete(
                        MovieContract.FavoriteEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
            }
            break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String where, String[] whereArgs) {
        int rowsUpdated = 0;
        final SQLiteDatabase db = movieDatabaseHelper.getWritableDatabase();
        switch(sUriMatcher.match(uri)) {
            case TOP_RATED:
            case POPULAR: {
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME,
                        contentValues,
                        where,
                        whereArgs);
            }
            break;
            case FAVORITES: {
                rowsUpdated = db.update(MovieContract.FavoriteEntry.TABLE_NAME,
                        contentValues,
                        where,
                        whereArgs);
            }
            break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
