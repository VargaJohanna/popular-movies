package com.movies.popularmoviesjava.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.List;

@Entity(tableName = "movie")
public class MovieEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "poster_path")
    private String posterPath;
    @ColumnInfo(name = "user_rating")
    private String userRating;
    private String title;
    private String synopsis;
    @ColumnInfo(name = "release_date")
    private String releaseDate;
    @ColumnInfo(name = "film_id")
    private String filmId;
    @ColumnInfo(name = "trailer_titles")
    @TypeConverters(ListTypeConverters.class)
    private List<String> trailerTitles;
    @ColumnInfo(name = "trailer_keys")
    @TypeConverters(ListTypeConverters.class)
    private List<String> trailerKeys;

    @Ignore
    public  MovieEntry(String posterPath, String userRating, String title, String synopsis, String releaseDate, String filmId, List<String> trailerTitles, List<String> trailerKeys) {
        this.posterPath = posterPath;
        this.userRating = userRating;
        this.title = title;
        this.synopsis = synopsis;
        this.releaseDate = releaseDate;
        this.filmId = filmId;
        this.trailerTitles = trailerTitles;
        this.trailerKeys = trailerKeys;
    }

    public  MovieEntry(int id, String posterPath, String userRating, String title, String synopsis, String releaseDate, String filmId, List<String> trailerTitles, List<String> trailerKeys) {
        this.id = id;
        this.posterPath = posterPath;
        this.userRating = userRating;
        this.title = title;
        this.synopsis = synopsis;
        this.releaseDate = releaseDate;
        this.filmId = filmId;
        this.trailerTitles = trailerTitles;
        this.trailerKeys = trailerKeys;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getUserRating() {
        return userRating;
    }

    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getFilmId() {
        return filmId;
    }

    public void setFilmId(String filmId) {
        this.filmId = filmId;
    }
    public List<String> getTrailerTitles() {
        return trailerTitles;
    }

    public void setTrailerTitles(List<String> trailerTitles) {
        this.trailerTitles = trailerTitles;
    }

    public List<String> getTrailerKeys() {
        return trailerKeys;
    }

    public void setTrailerKeys(List<String> trailerKeys) {
        this.trailerKeys = trailerKeys;
    }
}
