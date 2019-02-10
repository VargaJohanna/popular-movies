package com.movies.popularmoviesjava.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.movies.popularmoviesjava.R;
import com.movies.popularmoviesjava.adapter.MovieAdapter;
import com.movies.popularmoviesjava.database.MovieEntry;
import com.movies.popularmoviesjava.databinding.ActivityMainBinding;
import com.movies.popularmoviesjava.model.Movie;
import com.movies.popularmoviesjava.network.GetMovieDataService;
import com.movies.popularmoviesjava.network.RetrofitInstance;
import com.movies.popularmoviesjava.viewmodels.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ItemClickListener {
    ActivityMainBinding mBinding;
    private MovieAdapter mainAdapter;
    private MainViewModel viewModel;
    private GetMovieDataService service = RetrofitInstance.getInstance().create(GetMovieDataService.class);
    String screenSizeCategory;
    String screenOrientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainAdapter = new MovieAdapter(new ArrayList<Movie>(), this);
        observeSortBy();
        String sortByValue = viewModel.getSortBy().getValue();
        if (sortByValue == null) {
            viewModel.setSortBy(getString(R.string.popular_sort_by));
        }
        observeMovieList();
        screenSizeCategory = getScreenSizeCategory();
        screenOrientation = getScreenOrientation();
        generateMovieList(mainAdapter);
        setTitle(R.string.most_popular_title);
    }

    private void observeSortBy() {
        viewModel.getSortBy().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                assert s != null;
                if (!s.equals(getString(R.string.favourite_sort_by))) {
                    viewModel.fetchMovieListFromApi(service, s);
                } else {
                    getFavouriteMovieList();
                }
                setAppTitle(s);
            }
        });
    }

    private void observeMovieList() {
        mBinding.progressBar.setVisibility(View.VISIBLE);

        viewModel.getMovieListFromApi().observe(MainActivity.this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                if (!movies.isEmpty() && !viewModel.getSortBy().getValue().equals(getString(R.string.favourite_sort_by))) {
                    mainAdapter.updateList(movies);
                } else if (!viewModel.getSortBy().getValue().equals(getString(R.string.favourite_sort_by))) {
                    Toast.makeText(MainActivity.this, R.string.no_internet_message, Toast.LENGTH_LONG).show();
                }
                mBinding.progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void getFavouriteMovieList() {
        viewModel.getFavouriteMovies().observe(this, new Observer<List<MovieEntry>>() {
                    @Override
                    public void onChanged(@Nullable List<MovieEntry> movieEntries) {
                        Log.d(MainActivity.class.getSimpleName(), "Updating list from LiveData in ViewModal");
                        List<Movie> movieList = generateMovieObjectList(movieEntries);
                        mainAdapter.updateList(movieList);
                    }
                }
        );
    }

    private List<Movie> generateMovieObjectList(List<MovieEntry> movieEntries) {
        List<Movie> listOfMovies = new ArrayList<>();
        for (MovieEntry movie : movieEntries) {
            listOfMovies.add(new Movie(movie.getPosterPath(),
                    movie.getUserRating(),
                    movie.getTitle(),
                    movie.getSynopsis(),
                    movie.getReleaseDate(),
                    movie.getFilmId()));
        }
        return listOfMovies;
    }

    private void generateMovieList(RecyclerView.Adapter adapter) {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(MainActivity.this, getSpanCount());
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.most_popular) {
            viewModel.setSortBy(getString(R.string.popular_sort_by));
        } else if (itemId == R.id.top_rated) {
            viewModel.setSortBy(getString(R.string.top_rated_sort_by));
        } else if (itemId == R.id.favourite_menu) {
            viewModel.setSortBy(getString(R.string.favourite_sort_by));
        }
        if (viewModel.getSortBy().getValue() != null) {
            setAppTitle(viewModel.getSortBy().getValue());
        }
        return true;
    }

    private void setAppTitle(String actualSortBy) {
        if (actualSortBy.equals(getString(R.string.popular_sort_by))) {
            setTitle(R.string.most_popular_title);
        } else if (actualSortBy.equals(getString(R.string.top_rated_sort_by))) {
            setTitle(R.string.top_rated_movies_title);
        } else if (actualSortBy.equals(getString(R.string.favourite_sort_by))) {
            setTitle(R.string.my_favourites_title);
        }
    }

    @Override
    public void onItemClick(Movie movie) {
        Intent detailActivity = new Intent(this, DetailActivity.class);
        detailActivity.putExtra(DetailActivity.MOVIE_OBJECT, movie);
        startActivity(detailActivity);
    }

    private String getScreenSizeCategory() {
        int screenLayout = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

        switch (screenLayout) {
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                // small screens are at least 426dp x 320dp
                return getString(R.string.screen_small);
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                // normal screens are at least 470dp x 320dp
                return getString(R.string.screen_normal);
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                // large screens are at least 640dp x 480dp
                return getString(R.string.screen_large);
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                // xlarge screens are at least 960dp x 720dp
                return getString(R.string.screen_xlarge);
            default:
                return getString(R.string.undefined);
        }
    }

    private String getScreenOrientation() {
        String orientation = getString(R.string.undefined);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            orientation = getString(R.string.screen_landscape);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            orientation = getString(R.string.screen_portrait);
        }

        return orientation;
    }

    private int getSpanCount() {
        if (screenSizeCategory.equals(getString(R.string.screen_small))) {
            return 2;
        } else if (screenOrientation.equals(getString(R.string.screen_portrait))
                && screenSizeCategory.equals(getString(R.string.screen_normal))) {
            return 2;
        } else {
            return 3;
        }
    }
}
