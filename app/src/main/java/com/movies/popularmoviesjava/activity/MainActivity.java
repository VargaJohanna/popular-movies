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
}
