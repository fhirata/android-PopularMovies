package com.example.fabiohh.popularmovies.models;

import android.database.Cursor;
import android.util.Log;

import com.example.fabiohh.popularmovies.db.MovieContentProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    private String mName;
    private String   mImgUrl;
    private String   mDescription;
    private int      mVoteCount;
    private double   mVoteAverage;
    private Date     mReleaseDate;
    private int      mDuration;
    private String   mBackDropUrl;
    private long   mMovieId;

    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
    static SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");


    public MovieItem() {
        Log.d(TAG, "MovieItem() called");
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getName() {
        return mName;
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

    public long getMovieId() {
        return mMovieId;
    }

    public void setMovieId(long movieId) {
        Log.d(TAG, "setMovieId() called with: movieId = [" + movieId + "]");
        this.mMovieId = movieId;
    }

    public static MovieItem fromCursor(Cursor cursor) {
        MovieItem item = new MovieItem();

        item.setName(cursor.getString(MovieContentProvider.COL_NAME));
        item.setImgUrl(cursor.getString(MovieContentProvider.COL_POSTER_URL));
        item.setDescription(cursor.getString(MovieContentProvider.COL_SYNOPSIS));
        item.setVoteCount(cursor.getString(MovieContentProvider.COL_VOTE_COUNT));
        item.setAverageVote(cursor.getString(MovieContentProvider.COL_VOTE_AVERAGE));
        item.setReleaseDate(cursor.getString(MovieContentProvider.COL_POSTER_RELEASE_DATE));
        item.setBackDropUrl(cursor.getString(MovieContentProvider.COL_BACKDROP_URL));
        item.setMovieId(cursor.getLong(MovieContentProvider.COL_MOVIE_ID));

        Log.d(TAG, "fromCursor() called with: cursor = [" + cursor + "]");
        return item;
    }
}
