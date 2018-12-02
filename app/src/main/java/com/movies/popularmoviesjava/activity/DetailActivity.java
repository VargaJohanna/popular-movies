package com.movies.popularmoviesjava.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.movies.popularmoviesjava.R;
import com.movies.popularmoviesjava.model.Movie;
import com.movies.popularmoviesjava.network.RetrofitInstance;
import com.movies.popularmoviesjava.utilities.ImageSize;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    public static final String MOVIE_OBJECT = "movie";
    Intent intent;
    TextView movieTitle;
    ImageView image;
    TextView releaseDate;
    TextView userRating;
    TextView synopsis;


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

        if(intent.hasExtra(MOVIE_OBJECT)) {
            setupUI();
        }
    }

    private void setupUI() {
        Movie movie = intent.getParcelableExtra(MOVIE_OBJECT);
        movieTitle.setText(movie.getTitle());
        Picasso.get()
                .load(RetrofitInstance.IMAGE_BASE_URL + ImageSize.getImageSize(4) + movie.getPosterPath())
                .placeholder(R.drawable.ic_launcher_background)
                .into(image);

        synopsis.setText(movie.getSynopsis());
        userRating.setText(String.format("%s %s", getString(R.string.rating_label), movie.getUserRating()));
        releaseDate.setText(String.format("%s %s", getString(R.string.released_label), movie.getReleaseDate()));
    }
}
