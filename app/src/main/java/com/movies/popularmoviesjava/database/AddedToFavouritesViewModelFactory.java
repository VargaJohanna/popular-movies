package com.movies.popularmoviesjava.database;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

public class AddedToFavouritesViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final AppDatabase database;
    private final String movieId;

    public AddedToFavouritesViewModelFactory(AppDatabase database, String movieId) {
        this.database = database;
        this.movieId = movieId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new AddToFavouritesViewModel(database, movieId);
    }
}
