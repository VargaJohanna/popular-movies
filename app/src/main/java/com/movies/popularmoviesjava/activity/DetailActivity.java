package com.movies.popularmoviesjava.activity;

import android.content.Intent;
import android.net.Uri;
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
import com.movies.popularmoviesjava.database.AppDatabase;
import com.movies.popularmoviesjava.database.MovieEntry;
import com.movies.popularmoviesjava.model.Movie;
import com.movies.popularmoviesjava.model.TrailerVideo;
import com.movies.popularmoviesjava.model.TrailersList;
import com.movies.popularmoviesjava.network.GetMovieDataService;
import com.movies.popularmoviesjava.network.RetrofitInstance;
import com.movies.popularmoviesjava.utilities.ApiKey;
import com.movies.popularmoviesjava.utilities.ImageSize;
import com.movies.popularmoviesjava.utilities.TrailerUrl;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity implements TrailerAdapter.ItemClickListener {
    public static final String MOVIE_OBJECT = "movie";
    Intent intent;
    TextView movieTitle;
    ImageView image;
    TextView releaseDate;
    TextView userRating;
    TextView synopsis;
    ImageView favouriteButton;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private TrailerAdapter trailerAdapter;
    private Movie movie;
    public boolean addedToFavouriteBoolean = false;
    private List<String> trailerTitles = new ArrayList<>();
    private List<String> trailerKeys = new ArrayList<>();
    private AppDatabase mDb;

    public List<String> getTrailerTitles() {
        return trailerTitles;
    }

    public void setTrailerTitles(List<TrailerVideo> videoObjects) {
        for(TrailerVideo video : videoObjects){
            this.trailerTitles.add(video.getVideoTitle());
        }
    }

    public List<String> getTrailerKeys() {
        return trailerKeys;
    }

    public void setTrailerKeys(List<TrailerVideo> videoObjects) {
        for(TrailerVideo video : videoObjects){
            this.trailerKeys.add(video.getVideoKey());
        }
    }

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
        favouriteButton = findViewById(R.id.favourite_icon);
        updateFavouriteButton();

        if(intent.hasExtra(MOVIE_OBJECT)) {
            movie = intent.getParcelableExtra(MOVIE_OBJECT);
            setupUI();
            createTrailerList(service);
        }
        mDb = AppDatabase.getInstance(getApplicationContext());
    }

    private void createTrailerList(GetMovieDataService service) {
        Call<TrailersList> call = service.getTrailerList(getVideoId(), ApiKey.KEY);
        progressBar.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<TrailersList>() {
            @Override
            public void onResponse(@NonNull Call<TrailersList> call, @NonNull Response<TrailersList> response) {
                assert response.body() != null;
                if(response.body().getTrailersList().size() != 0) {
                    List<TrailerVideo> returnedTrailerVideoList = response.body().getTrailersList();
                    generateTrailerList(returnedTrailerVideoList);
                    progressBar.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    setTrailerTitles(returnedTrailerVideoList);
                    setTrailerKeys(returnedTrailerVideoList);
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

    private void generateTrailerList(List<TrailerVideo> trailers) {
        recyclerView = findViewById(R.id.recycler_view_trailer);
        trailerAdapter = new TrailerAdapter(trailers, this);
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

    private String getTrailerId(TrailerVideo video) {
        return video.getVideoKey();
    }

    @Override
    public void onItemClick(TrailerVideo video) {
        Uri trailerUri = Uri.parse(TrailerUrl.URL + getTrailerId(video));
        Intent intent = new Intent(Intent.ACTION_VIEW, trailerUri);
        if(intent.resolveActivity(getPackageManager()) != null) {
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            startActivity(intent);
        }
    }

    public void updateFavouriteButton() {
        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!addedToFavouriteBoolean) {
                    favouriteButton.setImageResource(R.drawable.star_small_yellow);
                    addedToFavouriteBoolean = true;
                    addToFavourites();
                } else  {
                    favouriteButton.setImageResource(R.drawable.star_small);
                    addedToFavouriteBoolean = false;
                }
            }
        });
    }

    private void addToFavourites() {
        MovieEntry movieEntry = new MovieEntry(movie.getPosterPath(),
                movie.getUserRating(),
                movie.getTitle(),
                movie.getSynopsis(),
                movie.getReleaseDate(),
                movie.getFilmId(),
                getTrailerTitles(),
                getTrailerKeys());

        mDb.movieDao().insertMovie(movieEntry);
    }

}
