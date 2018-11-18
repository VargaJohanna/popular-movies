package com.movies.popularmoviesjava.model;

import com.google.gson.annotations.SerializedName;

public class Movie {
    @SerializedName("poster_path")
    private String posterPath;

    public Movie (String posterPath) {
        this.posterPath = posterPath;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }
}
