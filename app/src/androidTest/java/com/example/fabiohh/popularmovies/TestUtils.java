package com.example.fabiohh.popularmovies;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.example.fabiohh.popularmovies.db.MovieContract;
import com.example.fabiohh.popularmovies.models.IMovieInfo;

import java.util.Map;
import java.util.Set;

/**
 * Created by fabiohh on 10/26/16.
 */

public class TestUtils extends AndroidTestCase {
    public static long TEST_MOVIE_ID = 123456L;

    static void validateRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            String actualValue = valueCursor.getString(idx);

            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, actualValue);
        }
    }

    public static ContentValues createMovieRecord() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(MovieContract.MovieEntry.COLUMN_NAME, "name");
        contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "09/12/2016");
        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_BITMAP, "blob");
        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_URL, "http://poster_url.com/poster.gif");
        contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, 123);
        contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, "123");
        contentValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, "Long synopsis");
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, TEST_MOVIE_ID);
        contentValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_URL, "http://backdrop_url.com/backdrop.gif");
        contentValues.put(MovieContract.MovieEntry.COLUMN_TYPE, IMovieInfo.POPULAR);

        return contentValues;
    }

    public static ContentValues createFavoriteRecord() {
        ContentValues contentValue = new ContentValues();
        contentValue.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_ID, TEST_MOVIE_ID);

        return contentValue;
    }
}
