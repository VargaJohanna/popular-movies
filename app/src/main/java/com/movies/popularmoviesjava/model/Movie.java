package com.movies.popularmoviesjava.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Movie implements Parcelable {
    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("vote_average")
    private String userRating;

    @SerializedName("original_title")
    private String title;

    @SerializedName("overview")
    private String synopsis;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("id")
    private String filmId;

    public Movie(Parcel in) {
        posterPath = in.readString();
        userRating = in.readString();
        title = in.readString();
        synopsis = in.readString();
        releaseDate = in.readString();
        filmId = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public Movie(String posterPath, String userRating, String title, String synopsis, String releaseDate, String filmId) {
        this.posterPath = posterPath;
        this.userRating = userRating;
        this. title = title;
        this.synopsis = synopsis;
        this. releaseDate = releaseDate;
        this.filmId = filmId;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getUserRating() {
        return userRating;
    }

    public String getTitle() {
        return title;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getFilmId() {
        return filmId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(posterPath);
        dest.writeString(userRating);
        dest.writeString(title);
        dest.writeString(synopsis);
        dest.writeString(releaseDate);
        dest.writeString(filmId);
    }
}
