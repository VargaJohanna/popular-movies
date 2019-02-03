package com.movies.popularmoviesjava.database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.movies.popularmoviesjava.model.TrailerVideo;
import com.movies.popularmoviesjava.utilities.AppExecutors;

import java.util.ArrayList;
import java.util.List;

public class TrailerVideoListViewModel extends AndroidViewModel {
    private MutableLiveData<List<TrailerVideo>> videoList;

    public TrailerVideoListViewModel(@NonNull Application application) {
        super(application);
        videoList = new MutableLiveData<>();
    }

    public LiveData<List<TrailerVideo>> getVideoList() {
        return videoList;
    }

    public void fetchVideoList(final String movieId) {
        final AppDatabase database = AppDatabase.getInstance(this.getApplication());
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                MovieEntry movie = database.movieDao().getMovieById(movieId);
                List<TrailerVideo> listOfVideos = getTrailerVideoList(movie.getTrailerKeys(), movie.getTrailerTitles());
                videoList.postValue( listOfVideos);
            }
        });
    }

    private List<TrailerVideo> getTrailerVideoList(List<String> keys, List<String> titles) {
        List<TrailerVideo> listOfVideos = new ArrayList<>();
        for(int i = 0; i < keys.size(); i++) {
            listOfVideos.add(new TrailerVideo(keys.get(i), titles.get(i)));
        }
        return listOfVideos;
    }
}
