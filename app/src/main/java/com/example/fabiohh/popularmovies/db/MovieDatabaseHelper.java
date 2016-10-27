package com.example.fabiohh.popularmovies.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by fabiohh on 10/26/16.
 */

public class MovieDatabaseHelper extends SQLiteOpenHelper {
    final static String DATABASE_NAME = "popularmovies.db";
    final static int DATABASE_VERSION = 1;


    final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME +
            " (" + MovieContract.MovieEntry.COLUMN_ID + " INTEGER AUTO INCREMENT, " +
            MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
            MovieContract.MovieEntry.COLUMN_NAME + " TEXT NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT," +
            MovieContract.MovieEntry.COLUMN_POSTER_BITMAP + " BLOB," +
            MovieContract.MovieEntry.COLUMN_POSTER_URL + " TEXT," +
            MovieContract.MovieEntry.COLUMN_VOTE_COUNT + " TEXT," +
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " TEXT," +
            MovieContract.MovieEntry.COLUMN_SYNOPSIS + " TEXT," +
            MovieContract.MovieEntry.COLUMN_BACKDROP_URL + " TEXT," +
            MovieContract.MovieEntry.COLUMN_TYPE + " TEXT NOT NULL," +
            " PRIMARY KEY (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + ", " + MovieContract.MovieEntry.COLUMN_TYPE + ")" +
            ");";

    final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + MovieContract.FavoriteEntry.TABLE_NAME +
            "( " + MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " INTEGER PRIMARY KEY);";

    public MovieDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS" + MovieContract.FavoriteEntry.TABLE_NAME);
    }
}
