package com.movies.popularmoviesjava.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieList {
    @SerializedName("results")
    private List<Movie> movieList;

    public List<Movie> getMovieList() {
        return movieList;
    }
}
