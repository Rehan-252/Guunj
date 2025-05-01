package com.example.guunj.Models;

public class Songs {
    String title, artist, duration;
    int thumbnailResId;

    public Songs(String title, String artist, String duration, int thumbnailResId) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.thumbnailResId = thumbnailResId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getThumbnailResId() {
        return thumbnailResId;
    }

    public void setThumbnailResId(int thumbnailResId) {
        this.thumbnailResId = thumbnailResId;
    }

}
