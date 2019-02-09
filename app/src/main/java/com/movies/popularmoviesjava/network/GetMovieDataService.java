package com.movies.popularmoviesjava.network;

import com.movies.popularmoviesjava.model.MovieList;
import com.movies.popularmoviesjava.model.ReviewsList;
import com.movies.popularmoviesjava.model.TrailersList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetMovieDataService {
    @GET("/3/movie/{sort_by}")
    Call<MovieList> getMovieData(@Path("sort_by") String sortBy,
                                 @Query("api_key") String apiKey);

    @GET("/3/movie/{video_id}/videos")
    Call<TrailersList> getTrailerList(@Path("video_id") String videoId,
                                      @Query("api_key") String apiKey);

    @GET("/3/movie/{video_id}/reviews")
    Call<ReviewsList> getReviewList(@Path("video_id") String videoId,
                                    @Query("api_key") String apiKey);
}
