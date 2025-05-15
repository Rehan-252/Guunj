package com.example.guunj.Models;

import android.net.Uri;

public class Songs {
    String id, title, artist, duration;
    Uri albumUri;

    public Songs(String id, String title, String artist, String duration, Uri albumUri) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.albumUri = albumUri;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getDuration() {
        return duration;
    }

    public Uri getAlbumUri() {
        return albumUri;
    }
}
