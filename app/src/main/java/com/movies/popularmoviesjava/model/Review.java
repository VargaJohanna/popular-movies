package com.movies.popularmoviesjava.model;

import com.google.gson.annotations.SerializedName;

public class Review {
    @SerializedName("content")
    private String reviewContent;

    @SerializedName("author")
    private String reviewAuthor;

    public String getReviewContent() {
        return reviewContent;
    }

    public String getReviewAuthor() {
        return reviewAuthor;
    }

    public Review(String content, String author) {
        this.reviewContent = content;
        this.reviewAuthor = author;
    }
}
