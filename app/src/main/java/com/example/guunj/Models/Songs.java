package com.example.guunj.Models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Songs implements Parcelable {
    private String id;
    private String title;
    private String artist;
    private String duration;
    private Uri albumUri;
    private Uri songUri;

    public Songs(String id, String title, String artist, String duration, Uri albumUri, Uri songUri) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.albumUri = Uri.parse(albumUri.toString());
        this.songUri = Uri.parse(songUri.toString());
    }

    protected Songs(Parcel in) {
        id = in.readString();
        title = in.readString();
        artist = in.readString();
        duration = in.readString();
        albumUri = in.readParcelable(Uri.class.getClassLoader());
        songUri = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Creator<Songs> CREATOR = new Creator<Songs>() {
        @Override
        public Songs createFromParcel(Parcel in) {
            return new Songs(in);
        }

        @Override
        public Songs[] newArray(int size) {
            return new Songs[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeString(duration);
        dest.writeParcelable(albumUri,flags);
        dest.writeParcelable(songUri,flags);
    }

    // Getters and setters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getDuration() { return duration; }
    public String getAlbumUri() { return String.valueOf(albumUri); }
    public Uri getSongUri() { return songUri; }

    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setArtist(String artist) { this.artist = artist; }
    public void setDuration(String duration) { this.duration = duration; }
    public void setAlbumUri(Uri albumUri) { this.albumUri = albumUri; }
    public void setSongUri(Uri songUri) { this.songUri = songUri; }
}
