package com.example.fabiohh.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

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
public class MovieItem implements Parcelable {

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

    // Parcelable pattern implementation
    public MovieItem(Parcel in) {
        readFromParcel(in);
    }
    public static final String PARCELABLE_KEY = "movie";

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mTitle);
        out.writeString(mImgUrl);
        out.writeString(mDescription);
        out.writeInt(mVoteCount);
        out.writeDouble(mVoteAverage);
        out.writeSerializable(mReleaseDate);
        out.writeInt(mDuration);
        out.writeString(mBackDropUrl);
    }

    private void readFromParcel(Parcel in) {
        mTitle = in.readString();
        mImgUrl = in.readString();
        mDescription = in.readString();
        mVoteCount = in.readInt();
        mVoteAverage = in.readDouble();
        mReleaseDate = (Date) in.readSerializable();
        mDuration = in.readInt();
        mBackDropUrl = in.readString();
    }

    public static final Parcelable.Creator<MovieItem> CREATOR = new Parcelable.Creator() {

        public MovieItem createFromParcel(Parcel in) {
            return new MovieItem(in);
        }

        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
