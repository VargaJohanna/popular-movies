package com.movies.popularmoviesjava.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class TrailerVideo implements Parcelable {
    public String getVideoKey() {
        return videoKey;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    @SerializedName("key")
    private String videoKey;

    @SerializedName("name")
    private String videoTitle;

    @Override
    public int describeContents() {
        return 0;
    }

    private TrailerVideo(Parcel in) {
        videoKey = in.readString();
        videoTitle = in.readString();
    }

    public static final Creator<TrailerVideo> CREATOR = new Creator<TrailerVideo>() {
        @Override
        public TrailerVideo createFromParcel(Parcel in) {
            return new TrailerVideo(in);
        }

        @Override
        public TrailerVideo[] newArray(int size) {
            return new TrailerVideo[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(videoKey);
        dest.writeString(videoTitle);
    }
}
