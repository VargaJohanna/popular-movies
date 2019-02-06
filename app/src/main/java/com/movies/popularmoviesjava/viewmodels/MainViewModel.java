package com.movies.popularmoviesjava.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.movies.popularmoviesjava.database.AppDatabase;
import com.movies.popularmoviesjava.database.MovieEntry;
import com.movies.popularmoviesjava.model.Movie;
import com.movies.popularmoviesjava.model.MovieList;
import com.movies.popularmoviesjava.network.GetMovieDataService;
import com.movies.popularmoviesjava.utilities.ApiKey;
import com.movies.popularmoviesjava.utilities.AppExecutors;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends AndroidViewModel {
    private MutableLiveData<List<Movie>> movieListFromApi;
    private LiveData<List<MovieEntry>> favouriteMovies;


    public MainViewModel(@NonNull Application application) {
        super(application);
        this.movieListFromApi = new MutableLiveData<>();
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        favouriteMovies = database.movieDao().loadAllFavouriteMovies();
    }

    public LiveData<List<Movie>> getMovieListFromApi() {
        return movieListFromApi;
    }

    public void fetchMovieListFromApi(final GetMovieDataService service, final String sortBy) {
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                Call<MovieList> call = service.getMovieData(sortBy, ApiKey.KEY);
                call.enqueue(new Callback<MovieList>() {
                    @Override
                    public void onResponse(Call<MovieList> call, Response<MovieList> response) {
                        assert response.body() != null;
                        movieListFromApi.postValue(response.body().getMovieList());
                    }

                    @Override
                    public void onFailure(Call<MovieList> call, Throwable t) {
                        movieListFromApi.postValue(null);
                    }
                });
            }
        });
    }

    public LiveData<List<MovieEntry>> getFavouriteMovies() {
        return favouriteMovies;
    }

}
