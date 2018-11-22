package com.movies.popularmoviesjava.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.movies.popularmoviesjava.R;
import com.movies.popularmoviesjava.network.RetrofitInstance;
import com.movies.popularmoviesjava.utilities.ImageSize;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    public static final String TITLE = "title";
    public static final String SYNOPSIS = "synopsis";
    public static final String RELEASE_DATE = "release date";
    public static final String USER_RATING = "user rating";
    public static final String IMAGE_PATH = "image path";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        TextView movieTitle = findViewById(R.id.movie_title);
        ImageView image = findViewById(R.id.thumbnail_image);
        TextView releaseDate = findViewById(R.id.release_date);
        TextView userRating = findViewById(R.id.user_rating);
        TextView synopsis = findViewById(R.id.synopsis);

        if(intent.hasExtra(TITLE)) {
            movieTitle.setText(intent.getStringExtra(TITLE));
        }

        if(intent.hasExtra(IMAGE_PATH)) {
            Picasso.get()
                    .load(RetrofitInstance.IMAGE_BASE_URL + ImageSize.getImageSize(4) + intent.getStringExtra(IMAGE_PATH))
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(image);
        }

        if(intent.hasExtra(SYNOPSIS)) {
            synopsis.setText(intent.getStringExtra(SYNOPSIS));
        }

        if(intent.hasExtra(USER_RATING)) {
            userRating.setText(String.format("%s %s", getString(R.string.rating_label), intent.getStringExtra(USER_RATING)));
        }

        if(intent.hasExtra(RELEASE_DATE)) {
            releaseDate.setText(String.format("%s %s", getString(R.string.released_label), intent.getStringExtra(RELEASE_DATE)));
        }
    }
}
