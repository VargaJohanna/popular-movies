package com.movies.popularmoviesjava.database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import java.util.List;

public class FavouriteMoviesViewModel extends AndroidViewModel {
    private LiveData<List<MovieEntry>> favouriteMovies;

    public FavouriteMoviesViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(FavouriteMoviesViewModel.class.getSimpleName(), "Retrieving favourites from the database");
        favouriteMovies = database.movieDao().loadAllFavouriteMovies();
    }

    public LiveData<List<MovieEntry>> getFavouriteMovies() {
        return favouriteMovies;
    }
}
