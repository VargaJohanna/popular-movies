package com.movies.popularmoviesjava.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MovieDao {
    @Query("SELECT * FROM movie ORDER BY title")
    LiveData<List<MovieEntry>> loadAllFavouriteMovies();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(MovieEntry movieEntry);

    @Query("SELECT count(*) FROM movie WHERE film_id = :movieId")
    int isMovieAddedToFavourites(String movieId);

    @Query("DELETE FROM movie WHERE film_id = :movieId")
    void deleteMovieWithId(String movieId);

    @Query("SELECT * FROM movie WHERE film_id = :movieId")
    MovieEntry getMovieById(String movieId);
}
