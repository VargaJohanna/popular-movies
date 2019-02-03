package com.movies.popularmoviesjava.database;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

public class AddToFavouritesViewModel extends ViewModel {
    private int movieFoundInFavourites;

    public AddToFavouritesViewModel(AppDatabase database, String movieId) {
        movieFoundInFavourites = database.movieDao().isMovieAddedToFavourites(movieId);
    }

    public int getMovieFoundInFavourites() {
        return movieFoundInFavourites;
    }
}
