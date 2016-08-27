package com.example.fabiohh.popularmovies.models;

/**
 * Created by fabiohh on 8/25/16.
 */
public class MovieItem {

    private String title;
    private String imgUrl;
    private String description;
    private String voteCount;
    private String voteAverage;

    public MovieItem(String title, String description, String voteAverage, String voteCount, String imgUrl) {
        this.imgUrl      = imgUrl;
        this.title       = title;
        this.description = description;
        this.voteAverage = voteAverage;
        this.voteCount   = voteCount;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(String voteCount) {
        this.voteCount = voteCount;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }
}
