package com.movies.popularmoviesjava.model;

import com.google.gson.annotations.SerializedName;

public class Movie {
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
}
