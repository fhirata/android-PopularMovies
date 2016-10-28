package com.example.fabiohh.popularmovies.models;

/**
 * Created by fabiohh on 10/27/16.
 */

public class MovieTrailer {
    String id;
    String thumbnailUrl;
    String videoUrl;
    String name;
    String site;
    String size;
    String type;

    public MovieTrailer(String id, String thumbnailUrl, String videoUrl, String name, String site, String size, String type) {
        this.id = id;
        this.thumbnailUrl = thumbnailUrl;
        this.videoUrl = videoUrl;
        this.name = name;
        this.site = site;
        this.size = size;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
