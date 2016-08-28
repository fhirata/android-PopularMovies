package com.example.fabiohh.popularmovies.models;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by fabiohh on 8/25/16.
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

    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
    static SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

    public MovieItem(String title, String description, String voteAverage, String voteCount, String imgUrl, String releaseDate) {
        setImgUrl(imgUrl);
        setTitle(title);
        setDescription(description);
        setAverageVote(voteAverage);
        setVoteCount(voteCount);
        setReleaseDate(releaseDate);
    }

    public MovieItem(ArrayList<String> movieData) {
              setTitle(movieData.get(0));
             setImgUrl(movieData.get(1));
        setDescription(movieData.get(2));
        setAverageVote(movieData.get(3));
          setVoteCount(movieData.get(4));
        setReleaseDate(movieData.get(5));
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getReleaseDate()
    {
        if (this.mReleaseDate != null) {
            return dateFormat.format(this.mReleaseDate);
        }
        return "(Unknown)";
    }

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

    public ArrayList<String> getMovieItemAsList() {
        ArrayList<String> movieAsList = new ArrayList<>();
        movieAsList.add(getTitle());
        movieAsList.add(getImgUrl());
        movieAsList.add(getDescription());
        movieAsList.add(getAverageVote());
        movieAsList.add(getVoteCount());
        movieAsList.add(getReleaseDate());

        return movieAsList;
    }
}
