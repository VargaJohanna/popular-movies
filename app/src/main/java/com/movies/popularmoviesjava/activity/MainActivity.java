package com.movies.popularmoviesjava.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
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
import android.widget.ProgressBar;
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
    private String sortBy = "popular";
    private static final String SORT_BY_KEY = "SORT_BY";
    private MainViewModel viewModel;
    private String getSortBy() {
        return sortBy;
    }
    private void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
    private GetMovieDataService service = RetrofitInstance.getInstance().create(GetMovieDataService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        if (savedInstanceState != null) {
            setSortBy(savedInstanceState.getString(SORT_BY_KEY));
        }
        observeMovieList(service);
        mainAdapter = new MovieAdapter(new ArrayList<Movie>(), this);
        generateMovieList(mainAdapter);
        setTitle(R.string.most_popular_title);
    }

    private void observeMovieList(final GetMovieDataService service) {
        mBinding.progressBar.setVisibility(View.VISIBLE);

        viewModel.getMovieListFromApi().observe(MainActivity.this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                if (movies != null) {
                    mainAdapter.updateList(movies);
                } else {
                    Toast.makeText(MainActivity.this, R.string.no_internet_message, Toast.LENGTH_LONG).show();
                }
                mBinding.progressBar.setVisibility(View.INVISIBLE);
            }
        });
        viewModel.fetchMovieListFromApi(service, getSortBy());
    }

    private void observeFavouriteMovieList() {
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
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 2);
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
            setSortBy(getString(R.string.popular_sort_by));
            viewModel.fetchMovieListFromApi(service, getSortBy());
            setTitle(R.string.most_popular_title);
        } else if (itemId == R.id.top_rated) {
            setSortBy(getString(R.string.top_rated_sort_by));
            viewModel.fetchMovieListFromApi(service, getSortBy());
            setTitle(R.string.top_rated_movies_title);
        } else if (itemId == R.id.favourite_menu) {
            observeFavouriteMovieList();
            setTitle(R.string.my_favourites_title);
        }
        return true;
    }

    @Override
    public void onItemClick(Movie movie) {
        Intent detailActivity = new Intent(this, DetailActivity.class);
        detailActivity.putExtra(DetailActivity.MOVIE_OBJECT, movie);
        startActivity(detailActivity);
    }

    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(SORT_BY_KEY, getSortBy());
    }
}
