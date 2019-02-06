package com.movies.popularmoviesjava.model;

import com.google.gson.annotations.SerializedName;

public class Review {
    @SerializedName("content")
    private String reviewContent;

    public String getReviewContent() {
        return reviewContent;
    }
}
