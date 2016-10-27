package com.example.fabiohh.popularmovies;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.example.fabiohh.popularmovies.db.MovieContract;
import com.example.fabiohh.popularmovies.db.MovieDatabaseHelper;

/**
 * Created by fabiohh on 10/26/16.
 */

public class TestMovieProvider extends AndroidTestCase {
    public void testDeleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                MovieContract.FavoriteEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        assertEquals("Delete All Movie Records failed.", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(MovieContract.FavoriteEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        assertEquals("Delete all Favorites Records failed.", 0, cursor.getCount());
        cursor.close();
    }

    private void deleteAllTables() {
        MovieDatabaseHelper dbHelper = new MovieDatabaseHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(MovieContract.MovieEntry.TABLE_NAME, null, null);
        db.delete(MovieContract.FavoriteEntry.TABLE_NAME, null, null);
        db.close();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllTables();
    }

    public void testInsertMovieItem() {
        MovieDatabaseHelper movieHelper = new MovieDatabaseHelper(mContext);
        SQLiteDatabase db = movieHelper.getWritableDatabase();

        ContentValues movieValue = TestUtils.createMovieRecord();
        long rowId = db.insert(MovieContract.MovieEntry.TABLE_NAME,
                null,
                movieValue);
        assertTrue("Error inserting into db.", rowId != -1);

        Cursor movieCursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        assertTrue(movieCursor.moveToFirst());

        TestUtils.validateRecord("Error inserting Movie item.", movieCursor, movieValue);
    }

    public void testInsertFavoriteItem() {
        MovieDatabaseHelper movieHelper = new MovieDatabaseHelper(mContext);
        SQLiteDatabase db = movieHelper.getWritableDatabase();

        ContentValues favoriteRecord = TestUtils.createFavoriteRecord();
        long rowId = db.insert(MovieContract.FavoriteEntry.TABLE_NAME,
                null,
                favoriteRecord);
        assertTrue("Error inserting into db.", rowId != -1);

        Cursor favoriteCursor = db.query(MovieContract.FavoriteEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        assertTrue(favoriteCursor.moveToFirst());

        TestUtils.validateRecord("Error inserting Movie item.", favoriteCursor, favoriteRecord);
    }
}
