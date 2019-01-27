package com.movies.popularmoviesjava.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.movies.popularmoviesjava.R;
import com.movies.popularmoviesjava.adapter.TrailerAdapter;
import com.movies.popularmoviesjava.model.Movie;
import com.movies.popularmoviesjava.model.TrailerVideo;
import com.movies.popularmoviesjava.model.TrailersList;
import com.movies.popularmoviesjava.network.GetMovieDataService;
import com.movies.popularmoviesjava.network.RetrofitInstance;
import com.movies.popularmoviesjava.utilities.ApiKey;
import com.movies.popularmoviesjava.utilities.ImageSize;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {
    public static final String MOVIE_OBJECT = "movie";
    Intent intent;
    TextView movieTitle;
    ImageView image;
    TextView releaseDate;
    TextView userRating;
    TextView synopsis;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private TrailerAdapter trailerAdapter;
    private Movie movie;

    private GetMovieDataService service = RetrofitInstance.getInstance().create(GetMovieDataService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        intent = getIntent();
        movieTitle = findViewById(R.id.movie_title);
        image = findViewById(R.id.thumbnail_image);
        releaseDate = findViewById(R.id.release_date);
        userRating = findViewById(R.id.user_rating);
        synopsis = findViewById(R.id.synopsis);
        progressBar = findViewById(R.id.progress_bar_details);


        if(intent.hasExtra(MOVIE_OBJECT)) {
            movie = intent.getParcelableExtra(MOVIE_OBJECT);
            setupUI();
            createTrailerList(service);
        }
    }

    private void createTrailerList(GetMovieDataService service) {
        Call<TrailersList> call = service.getTrailerList(getVideoId(), ApiKey.KEY);
        progressBar.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<TrailersList>() {
            @Override
            public void onResponse(@NonNull Call<TrailersList> call, @NonNull Response<TrailersList> response) {
                assert response.body() != null;
                if(response.body().getTrailersList().size() != 0) {
                    generateTrailerList(response.body().getTrailersList());
                    progressBar.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<TrailersList> call, @NonNull Throwable t) {
                Toast.makeText(DetailActivity.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void generateTrailerList(ArrayList<TrailerVideo> trailers) {
        recyclerView = findViewById(R.id.recycler_view_trailer);
        trailerAdapter = new TrailerAdapter(trailers);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(trailerAdapter);
    }

    private void setupUI() {
        movieTitle.setText(movie.getTitle());
        Picasso.get()
                .load(RetrofitInstance.IMAGE_BASE_URL + ImageSize.getImageSize(4) + movie.getPosterPath())
                .placeholder(R.drawable.ic_launcher_background)
                .into(image);

        synopsis.setText(movie.getSynopsis());
        userRating.setText(String.format("%s %s", getString(R.string.rating_label), movie.getUserRating()));
        releaseDate.setText(String.format("%s %s", getString(R.string.released_label), movie.getReleaseDate()));
    }

    private String getVideoId() {
        return movie.getFilmId();
    }
}
