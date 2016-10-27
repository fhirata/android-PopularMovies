package com.example.fabiohh.popularmovies.models;

import android.database.Cursor;
import android.util.Log;

import com.example.fabiohh.popularmovies.db.MovieContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by fabiohh on 8/25/16.
 * Implements Parcelable - the Parcelable interface allows your classes to be
 * flattened inside a message container called a Parcel to facilitate high
 * performance inter process communication.
 */
public class MovieItem {

    private static String TAG = MovieItem.class.getSimpleName();

    private String   mTitle;
    private String   mImgUrl;
    private String   mDescription;
    private int      mVoteCount;
    private double   mVoteAverage;
    private Date     mReleaseDate;
    private int      mDuration;
    private String mBackDropUrl;

    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
    static SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

    public MovieItem(String title, String description, String voteAverage, String voteCount, String imgUrl, String releaseDate, String backDrop) {
        setImgUrl(imgUrl);
        setTitle(title);
        setDescription(description);
        setAverageVote(voteAverage);
        setVoteCount(voteCount);
        setReleaseDate(releaseDate);
        setBackDropUrl(backDrop);
    }

    public MovieItem() {

    }

    public MovieItem(ArrayList<String> movieData) {

    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

//    public String getReleaseDate()
//    {
//        if (this.mReleaseDate != null) {
//            return dateFormat.format(this.mReleaseDate);
//        }
//        return "(Unknown)";
//    }

    public String getReleaseYear()
    {
        if (this.mReleaseDate != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(this.mReleaseDate);
            return String.valueOf(calendar.get(Calendar.YEAR));
        }
        return "(Unknown)";
    }


    public void setReleaseDate(String releaseDate) {
        try {

            this.mReleaseDate = dateFormat.parse(releaseDate);
        } catch (ParseException dpe) {
            Log.e(TAG, "Failed to parse full date: " + dpe.getMessage());
        }
    }

    public String getBackDropUrl() {
        return mBackDropUrl;
    }

    public void setBackDropUrl(String mBackDrop) {
        this.mBackDropUrl = mBackDrop;
    }


    public String getImgUrl() {
        return mImgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.mImgUrl = imgUrl;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public String getVoteCount() {
        return String.valueOf(mVoteCount);
    }

    public void setVoteCount(String voteCount) {
        this.mVoteCount = Integer.valueOf(voteCount);
    }

    public String getAverageVote() {
        return String.valueOf(mVoteAverage);
    }

    public void setAverageVote(String averageVote) {
        this.mVoteAverage = Double.valueOf(averageVote);
    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int mDuration) {
        this.mDuration = mDuration;
    }

    public static MovieItem fromCursor(Cursor cursor) {
        MovieItem item = new MovieItem();

        item.setTitle(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME)));
        item.setImgUrl(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_URL)));
        item.setDescription(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_SYNOPSIS)));
        item.setVoteCount(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_COUNT)));
        item.setAverageVote(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)));
        item.setReleaseDate(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)));
        item.setBackDropUrl(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP_URL)));

        return item;
    }
}
