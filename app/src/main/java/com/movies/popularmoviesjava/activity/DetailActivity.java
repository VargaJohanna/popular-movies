package com.movies.popularmoviesjava.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.movies.popularmoviesjava.R;
import com.movies.popularmoviesjava.adapter.ReviewAdapter;
import com.movies.popularmoviesjava.adapter.TrailerAdapter;
import com.movies.popularmoviesjava.model.Review;
import com.movies.popularmoviesjava.viewmodels.DetailsViewModel;
import com.movies.popularmoviesjava.database.MovieEntry;
import com.movies.popularmoviesjava.model.Movie;
import com.movies.popularmoviesjava.model.TrailerVideo;
import com.movies.popularmoviesjava.network.GetMovieDataService;
import com.movies.popularmoviesjava.network.RetrofitInstance;
import com.movies.popularmoviesjava.utilities.ImageSize;
import com.movies.popularmoviesjava.utilities.TrailerUrl;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements TrailerAdapter.ItemClickListener {
    public static final String MOVIE_OBJECT = "movie";
    Intent intent;
    TextView movieTitle;
    ImageView image;
    TextView releaseDate;
    TextView userRating;
    TextView synopsis;
    TextView reviewTitle;
    ImageView favouriteButton;
    private ProgressBar progressBar;
    private RecyclerView trailersRecyclerView;
    private RecyclerView reviewsRecyclerView;
    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;
    private Movie movie;
    private MovieEntry movieEntry;
    private List<String> trailerTitles = new ArrayList<>();
    private List<String> trailerKeys = new ArrayList<>();
    private List<String> reviewList = new ArrayList<>();
    private DetailsViewModel viewModel;
    private GetMovieDataService service;
    private Boolean mIsFavourite;

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
        trailersRecyclerView = findViewById(R.id.recycler_view_trailer);
        reviewsRecyclerView = findViewById(R.id.recycler_view_reviews);
        reviewTitle = findViewById(R.id.review_list_title);
        viewModel = ViewModelProviders.of(this).get(DetailsViewModel.class);
        service = RetrofitInstance.getInstance().create(GetMovieDataService.class);
        observeFavouriteState();

        if (intent.hasExtra(MOVIE_OBJECT)) {
            movie = intent.getParcelableExtra(MOVIE_OBJECT);
            setupUI();
            viewModel.fetchMovieInFavourites(movie.getFilmId());
            observeTrailerList();
        }
        movieEntry = getMovieEntry();
        addListenerToFavouriteButton();
        observeReviewList();
        setTitle("");
    }

    public List<String> getTrailerTitles() {
        return trailerTitles;
    }

    public void setTrailerTitles(List<TrailerVideo> videoObjects) {
        for (TrailerVideo video : videoObjects) {
            this.trailerTitles.add(video.getVideoTitle());
        }
    }

    public List<String> getTrailerKeys() {
        return trailerKeys;
    }

    public void setTrailerKeys(List<TrailerVideo> videoObjects) {
        for (TrailerVideo video : videoObjects) {
            this.trailerKeys.add(video.getVideoKey());
        }
    }

    public List<String> getReviewList() {
        return reviewList;
    }

    public void setReviewList(List<Review> reviewObjects) {
        for(Review review : reviewObjects) {
            this.reviewList.add(review.getReviewContent());
        }
    }

    private void observeTrailerList() {
        viewModel.getTrailerVideoListFromApi().observe(DetailActivity.this, new Observer<List<TrailerVideo>>() {
            @Override
            public void onChanged(@Nullable List<TrailerVideo> trailerVideos) {
                if(trailerVideos != null) {
                    generateTrailerList(trailerVideos);
                    trailersRecyclerView.setVisibility(View.VISIBLE);
                    setTrailerTitles(trailerVideos);
                    setTrailerKeys(trailerVideos);
                } else if(mIsFavourite) {
                    viewModel.getVideoListFromDb().observe(DetailActivity.this, new Observer<List<TrailerVideo>>() {
                        @Override
                        public void onChanged(@Nullable List<TrailerVideo> trailerVideos) {
                            generateTrailerList(trailerVideos);
                            trailersRecyclerView.setVisibility(View.VISIBLE);
                        }
                    });
                    viewModel.fetchVideoListFromDb(movie.getFilmId());
                } else {
                    Toast.makeText(DetailActivity.this, "Something went wrong. We couldn't fetch trailers...", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
        viewModel.fetchTrailerVideoListFromAPi(service, movie.getFilmId());
    }

    private void generateTrailerList(List<TrailerVideo> trailers) {
        trailerAdapter = new TrailerAdapter(trailers, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        trailersRecyclerView.setLayoutManager(layoutManager);
        trailersRecyclerView.setAdapter(trailerAdapter);
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

    private String getTrailerId(TrailerVideo video) {
        return video.getVideoKey();
    }

    @Override
    public void onItemClick(TrailerVideo video) {
        Uri trailerUri = Uri.parse(TrailerUrl.URL + getTrailerId(video));
        Intent intent = new Intent(Intent.ACTION_VIEW, trailerUri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            startActivity(intent);
        }
    }

    public void addListenerToFavouriteButton() {
        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.updateFavouriteMoviesDb(movieEntry, movie, mIsFavourite);
                viewModel.fetchMovieInFavourites(movie.getFilmId());
            }
        });
    }

    private void observeFavouriteState() {
        viewModel.getIsFavourite().observe(DetailActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean) favouriteButton.setImageResource(R.drawable.ic_blue_star);
                else favouriteButton.setImageResource(R.drawable.ic_five_pointed_star);
                mIsFavourite = aBoolean;
            }
        });
    }

    private MovieEntry getMovieEntry() {
        return new MovieEntry(movie.getPosterPath(),
                movie.getUserRating(),
                movie.getTitle(),
                movie.getSynopsis(),
                movie.getReleaseDate(),
                movie.getFilmId(),
                getTrailerTitles(),
                getTrailerKeys(),
                getReviewList());
    }

    private void observeReviewList() {
        viewModel.getReviewsListFromApi().observe(this, new Observer<List<Review>>() {
            @Override
            public void onChanged(@Nullable List<Review> reviews) {
                if(reviews != null) {
                    generateReviewList(reviews);
                    reviewsRecyclerView.setVisibility(View.VISIBLE);
                    setReviewList(reviews);
                } else if(mIsFavourite) {
                    viewModel.getReviewsListFromDb().observe(DetailActivity.this, new Observer<List<Review>>() {
                        @Override
                        public void onChanged(@Nullable List<Review> reviews) {
                            if(reviews.size() != 0) {
                                generateReviewList(reviews);
                                reviewsRecyclerView.setVisibility(View.VISIBLE);
                            } else {
                                reviewTitle.setText("");
                                reviewsRecyclerView.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                    viewModel.fetchReviewListFromDb(movie.getFilmId());
                }
                else {
                    reviewTitle.setText("");
                    reviewsRecyclerView.setVisibility(View.INVISIBLE);
                }
            }
        });
        viewModel.fetchReviewsFromApi(service, movie.getFilmId());
    }

    private void generateReviewList(List<Review> reviews) {
        reviewAdapter = new ReviewAdapter(reviews);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        reviewsRecyclerView.setLayoutManager(layoutManager);
        reviewsRecyclerView.setAdapter(reviewAdapter);
    }
}
