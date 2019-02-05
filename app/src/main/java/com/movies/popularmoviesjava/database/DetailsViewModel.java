package com.movies.popularmoviesjava.database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.movies.popularmoviesjava.model.TrailerVideo;
import com.movies.popularmoviesjava.utilities.AppExecutors;

import java.util.ArrayList;
import java.util.List;

public class DetailsViewModel extends AndroidViewModel {
    private MutableLiveData<List<TrailerVideo>> videoList;
    private MutableLiveData<Boolean> isFavourite;

    final AppDatabase database = AppDatabase.getInstance(this.getApplication());

    public DetailsViewModel(@NonNull Application application) {
        super(application);
        videoList = new MutableLiveData<>();
        isFavourite = new MutableLiveData<>();
    }

    public LiveData<List<TrailerVideo>> getVideoList() {
        return videoList;
    }

    public void fetchVideoList(final String movieId) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                MovieEntry movie = database.movieDao().getMovieById(movieId);
                List<TrailerVideo> listOfVideos = getTrailerVideoList(movie.getTrailerKeys(), movie.getTrailerTitles());
                videoList.postValue(listOfVideos);
            }
        });
    }

    private List<TrailerVideo> getTrailerVideoList(List<String> keys, List<String> titles) {
        List<TrailerVideo> listOfVideos = new ArrayList<>();
        for (int i = 0; i < keys.size(); i++) {
            listOfVideos.add(new TrailerVideo(keys.get(i), titles.get(i)));
        }
        return listOfVideos;
    }

    public LiveData<Boolean> getIsFavourite() {
        return isFavourite;
    }

    public void fetchMovieInFavourites(final String movieId) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                boolean isFound = database.movieDao().isMovieAddedToFavourites(movieId) != 0;
                isFavourite.postValue(isFound);
            }
        });
    }
}
