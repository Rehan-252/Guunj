package com.example.guunj.Models;

public class Songs {
    String id,title, artist, duration;
    int thumbnailResId;

    public Songs(String id, String title, String artist, long duration, int thumbnailResId) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.duration = String.valueOf(duration);
        this.thumbnailResId = thumbnailResId;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
