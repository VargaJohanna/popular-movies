package com.movies.popularmoviesjava.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.movies.popularmoviesjava.database.AppDatabase;
import com.movies.popularmoviesjava.database.MovieEntry;
import com.movies.popularmoviesjava.model.Movie;
import com.movies.popularmoviesjava.model.Review;
import com.movies.popularmoviesjava.model.ReviewsList;
import com.movies.popularmoviesjava.model.TrailerVideo;
import com.movies.popularmoviesjava.model.TrailersList;
import com.movies.popularmoviesjava.network.GetMovieDataService;
import com.movies.popularmoviesjava.utilities.ApiKey;
import com.movies.popularmoviesjava.utilities.AppExecutors;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsViewModel extends AndroidViewModel {
    private MutableLiveData<List<TrailerVideo>> videoListFromDb;
    private MutableLiveData<Boolean> isFavourite;
    private MutableLiveData<List<TrailerVideo>> trailerVideoListFromApi;
    private MutableLiveData<List<Review>> reviewsListFromApi;
    private MutableLiveData<List<Review>> reviewsListFromDb;

    final AppDatabase database = AppDatabase.getInstance(this.getApplication());

    public DetailsViewModel(@NonNull Application application) {
        super(application);
        videoListFromDb = new MutableLiveData<>();
        isFavourite = new MutableLiveData<>();
        trailerVideoListFromApi = new MutableLiveData<>();
        reviewsListFromApi = new MutableLiveData<>();
        reviewsListFromDb = new MutableLiveData<>();
    }

    public LiveData<List<TrailerVideo>> getVideoListFromDb() {
        return videoListFromDb;
    }

    public void fetchVideoListFromDb(final String movieId) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                MovieEntry movie = database.movieDao().getMovieById(movieId);
                List<TrailerVideo> listOfVideos = getTrailerVideoList(movie.getTrailerKeys(), movie.getTrailerTitles());
                videoListFromDb.postValue(listOfVideos);
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

    public void updateFavouriteMoviesDb(final MovieEntry movieEntry, final Movie movie, boolean isFav) {
        if (!isFav) AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                database.movieDao().insertMovie(movieEntry);
            }
        });
        else AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                database.movieDao().deleteMovieWithId(movie.getFilmId());
            }
        });
    }

    public LiveData<List<TrailerVideo>> getTrailerVideoListFromApi() {
        return trailerVideoListFromApi;
    }

    public void fetchTrailerVideoListFromAPi(final GetMovieDataService service, final String filmId) {
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                Call<TrailersList> call = service.getTrailerList(filmId, ApiKey.KEY);
                call.enqueue(new Callback<TrailersList>() {
                    @Override
                    public void onResponse(Call<TrailersList> call, Response<TrailersList> response) {
                        assert response.body() != null;
                        trailerVideoListFromApi.postValue(response.body().getTrailersList());
                    }

                    @Override
                    public void onFailure(Call<TrailersList> call, Throwable t) {
                        trailerVideoListFromApi.postValue(null);
                    }
                });
            }
        });
    }

    public LiveData<List<Review>> getReviewsListFromApi() {
        return reviewsListFromApi;
    }

    public void fetchReviewsFromApi(final GetMovieDataService service, final String filmId) {
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                Call<ReviewsList> call = service.getReviewList(filmId, ApiKey.KEY);
                call.enqueue(new Callback<ReviewsList>() {
                    @Override
                    public void onResponse(Call<ReviewsList> call, Response<ReviewsList> response) {
                        assert response.body() != null;
                        if(response.body().getReviewList().size() != 0) {
                            reviewsListFromApi.postValue(response.body().getReviewList());
                        } else {
                            reviewsListFromApi.postValue(null);
                        }
                    }
                    @Override
                    public void onFailure(Call<ReviewsList> call, Throwable t) {
                        reviewsListFromApi.postValue(null);
                    }
                });
            }
        });
    }

    public LiveData<List<Review>> getReviewsListFromDb() {
        return reviewsListFromDb;
    }

    public void fetchReviewListFromDb(final String movieId) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                MovieEntry movie = database.movieDao().getMovieById(movieId);
                List<String> listOfReviews = movie.getReviews();
                reviewsListFromDb.postValue(getListOfReviewObjects(listOfReviews));
            }
        });
    }

    public List<Review> getListOfReviewObjects(List<String> review) {
        List<Review> listOfReviews = new ArrayList<>();
        for (String reviewText : review) {
            listOfReviews.add(new Review(reviewText));
        }
        return listOfReviews;
    }

}
