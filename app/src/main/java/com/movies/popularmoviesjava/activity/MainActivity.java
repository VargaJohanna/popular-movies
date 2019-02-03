package com.movies.popularmoviesjava.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.movies.popularmoviesjava.database.AppDatabase;
import com.movies.popularmoviesjava.database.FavouriteMoviesViewModel;
import com.movies.popularmoviesjava.database.MovieEntry;
import com.movies.popularmoviesjava.model.Movie;
import com.movies.popularmoviesjava.model.MovieList;
import com.movies.popularmoviesjava.network.GetMovieDataService;
import com.movies.popularmoviesjava.network.RetrofitInstance;
import com.movies.popularmoviesjava.utilities.ApiKey;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ItemClickListener {
    private MovieAdapter mainAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private String sortBy = "popular";
    private static final String SORT_BY_KEY = "SORT_BY";
    private AppDatabase mDb;

    public String getSortBy() {
        return sortBy;
    }
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
    private GetMovieDataService service = RetrofitInstance.getInstance().create(GetMovieDataService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progress_bar);
        if (savedInstanceState!= null){
            setSortBy(savedInstanceState.getString(SORT_BY_KEY));
        }
        createMovieList(service);
        mDb = AppDatabase.getInstance(getApplicationContext());
        mainAdapter = new MovieAdapter(new ArrayList<Movie>(), this);
        generateMovieList(mainAdapter);
    }

    private void createMovieList(GetMovieDataService service) {
        Call<MovieList> call = service.getMovieData(getSortBy(), ApiKey.KEY);
        progressBar.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<MovieList>() {
            @Override
            public void onResponse(@NonNull Call<MovieList> call, @NonNull Response<MovieList> response) {
                assert response.body() != null;
                List<Movie> movieList = response.body().getMovieList();
                mainAdapter.updateList(movieList);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<MovieList> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Something went wrong...", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void createFavouriteMovieList(){
        FavouriteMoviesViewModel viewModal = ViewModelProviders.of(this).get(FavouriteMoviesViewModel.class);
        viewModal.getFavouriteMovies().observe(this, new Observer<List<MovieEntry>>() {
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
        for(MovieEntry movie : movieEntries ) {
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
        recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
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
            createMovieList(service);
        } else if( itemId == R.id.top_rated){
            setSortBy(getString(R.string.top_rated_sort_by));
            createMovieList(service);
        } else if( itemId == R.id.favourite_menu) {
            createFavouriteMovieList();
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
