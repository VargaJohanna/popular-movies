package com.movies.popularmoviesjava.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TrailersList {
    @SerializedName("results")
    private ArrayList<TrailerVideo> trailersList;

    public ArrayList<TrailerVideo> getTrailersList() {
        return trailersList;
    }
}
