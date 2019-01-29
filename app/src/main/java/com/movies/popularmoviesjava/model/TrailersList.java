package com.movies.popularmoviesjava.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TrailersList {
    @SerializedName("results")
    private List<TrailerVideo> trailersList;

    public List<TrailerVideo> getTrailersList() {
        return trailersList;
    }
}
