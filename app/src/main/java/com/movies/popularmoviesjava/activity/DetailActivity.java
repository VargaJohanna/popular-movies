package com.movies.popularmoviesjava.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.movies.popularmoviesjava.R;
import com.movies.popularmoviesjava.adapter.ReviewAdapter;
import com.movies.popularmoviesjava.adapter.TrailerAdapter;
import com.movies.popularmoviesjava.database.MovieEntry;
import com.movies.popularmoviesjava.databinding.ActivityDetailBinding;
import com.movies.popularmoviesjava.model.Movie;
import com.movies.popularmoviesjava.model.Review;
import com.movies.popularmoviesjava.model.TrailerVideo;
import com.movies.popularmoviesjava.network.GetMovieDataService;
import com.movies.popularmoviesjava.network.RetrofitInstance;
import com.movies.popularmoviesjava.utilities.ImageSize;
import com.movies.popularmoviesjava.utilities.TrailerUrl;
import com.movies.popularmoviesjava.viewmodels.DetailsViewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements TrailerAdapter.ItemClickListener {
    ActivityDetailBinding mBinding;
    public static final String MOVIE_OBJECT = "movie";
    private Intent intent;
    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;
    private Movie movie;
    private MovieEntry movieEntry;
    private List<String> trailerTitles = new ArrayList<>();
    private List<String> trailerKeys = new ArrayList<>();
    private List<String> reviewList = new ArrayList<>();
    private List<String> reviewAuthorsList = new ArrayList<>();
    private DetailsViewModel viewModel;
    private GetMovieDataService service;
    private Boolean mIsFavourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        intent = getIntent();
        viewModel = ViewModelProviders.of(this).get(DetailsViewModel.class);
        service = RetrofitInstance.getInstance().create(GetMovieDataService.class);
        observeFavouriteState();
        if (intent.hasExtra(MOVIE_OBJECT)) {
            movie = intent.getParcelableExtra(MOVIE_OBJECT);
            setupUI();
            viewModel.fetchMovieInFavourites(movie.getFilmId());
            observeTrailerList();
            observeReviewList();
        }
        movieEntry = getMovieEntry();
        addListenerToFavouriteButton();
        setTitle(movie.getTitle());
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
        for (Review review : reviewObjects) {
            this.reviewList.add(review.getReviewContent().trim());
        }
    }

    public List<String> getReviewAuthorsList() {
        return reviewAuthorsList;
    }

    public void setReviewAuthorsList(List<Review> reviewObjects) {
        for (Review review : reviewObjects) {
            this.reviewAuthorsList.add(review.getReviewAuthor().trim());
        }
    }

    private void observeTrailerList() {
        mBinding.progressBarDetails.setVisibility(View.VISIBLE);
        viewModel.getTrailerVideoListFromApi().observe(DetailActivity.this, new Observer<List<TrailerVideo>>() {
            @Override
            public void onChanged(@Nullable List<TrailerVideo> trailerVideos) {
                if (trailerVideos != null) {
                    generateTrailerList(trailerVideos);
                    mBinding.recyclerViewTrailer.setVisibility(View.VISIBLE);
                    setTrailerTitles(trailerVideos);
                    setTrailerKeys(trailerVideos);
                } else if (mIsFavourite) {
                    viewModel.getVideoListFromDb().observe(DetailActivity.this, new Observer<List<TrailerVideo>>() {
                        @Override
                        public void onChanged(@Nullable List<TrailerVideo> trailerVideos) {
                            generateTrailerList(trailerVideos);
                            mBinding.recyclerViewTrailer.setVisibility(View.VISIBLE);
                        }
                    });
                    viewModel.fetchVideoListFromDb(movie.getFilmId());
                } else {
                    Toast.makeText(DetailActivity.this, R.string.error_message_toast, Toast.LENGTH_SHORT).show();
                }
                mBinding.progressBarDetails.setVisibility(View.INVISIBLE);
            }
        });
        viewModel.fetchTrailerVideoListFromAPi(service, movie.getFilmId());
    }

    private void generateTrailerList(List<TrailerVideo> trailers) {
        trailerAdapter = new TrailerAdapter(trailers, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerViewTrailer.setLayoutManager(layoutManager);
        mBinding.recyclerViewTrailer.setAdapter(trailerAdapter);
    }

    private void setupUI() {
        mBinding.progressBarDetails.setVisibility(View.VISIBLE);
        Picasso.get()
                .load(RetrofitInstance.IMAGE_BASE_URL + ImageSize.getImageSize(4) + movie.getPosterPath())
                .placeholder(R.drawable.ic_launcher_background)
                .into(mBinding.thumbnailImage);

        mBinding.synopsis.setText(movie.getSynopsis());
        mBinding.userRating.setText(String.format("%s %s", getString(R.string.rating_label), movie.getUserRating()));
        mBinding.releaseDate.setText(String.format("%s %s", getString(R.string.released_label), movie.getReleaseDate()));
        mBinding.progressBarDetails.setVisibility(View.INVISIBLE);
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
        mBinding.favouriteIcon.setOnClickListener(new View.OnClickListener() {
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
                if (aBoolean) mBinding.favouriteIcon.setImageResource(R.drawable.ic_blue_star);
                else mBinding.favouriteIcon.setImageResource(R.drawable.ic_five_pointed_star);
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
                getReviewList(),
                getReviewAuthorsList());
    }

    private void observeReviewList() {
        mBinding.progressBarDetails.setVisibility(View.VISIBLE);
        viewModel.getReviewsListFromApi().observe(this, new Observer<List<Review>>() {
            @Override
            public void onChanged(@Nullable List<Review> reviews) {
                if (reviews != null) {
                    generateReviewList(reviews);
                    mBinding.recyclerViewReviews.setVisibility(View.VISIBLE);
                    setReviewList(reviews);
                    setReviewAuthorsList(reviews);
                } else if (mIsFavourite) {
                    viewModel.getReviewsListFromDb().observe(DetailActivity.this, new Observer<List<Review>>() {
                        @Override
                        public void onChanged(@Nullable List<Review> reviews) {
                            if (reviews.size() != 0) {
                                generateReviewList(reviews);
                                mBinding.recyclerViewReviews.setVisibility(View.VISIBLE);
                            } else {
                                mBinding.reviewListTitle.setVisibility(View.INVISIBLE);
                                mBinding.recyclerViewReviews.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                    viewModel.fetchReviewListFromDb(movie.getFilmId());
                } else {
                    mBinding.reviewListTitle.setVisibility(View.INVISIBLE);
                    mBinding.recyclerViewReviews.setVisibility(View.INVISIBLE);
                }
                mBinding.progressBarDetails.setVisibility(View.INVISIBLE);
            }
        });
        viewModel.fetchReviewsFromApi(service, movie.getFilmId());
    }

    private void generateReviewList(List<Review> reviews) {
        reviewAdapter = new ReviewAdapter(reviews);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerViewReviews.setLayoutManager(layoutManager);
        mBinding.recyclerViewReviews.setAdapter(reviewAdapter);
    }
}
