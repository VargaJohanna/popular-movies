package com.movies.popularmoviesjava.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MovieList {
    @SerializedName("results")
    private ArrayList<Movie> movieList;

    public ArrayList<Movie> getMovieList() {
        return movieList;
    }
}
