package com.movies.popularmoviesjava.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.movies.popularmoviesjava.R;
import com.movies.popularmoviesjava.adapter.MovieAdapter;
import com.movies.popularmoviesjava.model.Movie;
import com.movies.popularmoviesjava.model.MovieList;
import com.movies.popularmoviesjava.network.GetMovieDataService;
import com.movies.popularmoviesjava.network.RetrofitInstance;
import com.movies.popularmoviesjava.utilities.ApiKey;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ItemClickListener {
    private MovieAdapter adapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private String sortBy = "popular";
    private static final String SORT_BY_KEY = "SORT_BY";

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
    }

    private void createMovieList(GetMovieDataService service) {
        Call<MovieList> call = service.getMovieData(getSortBy(), ApiKey.KEY);
        progressBar.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<MovieList>() {
            @Override
            public void onResponse(Call<MovieList> call, Response<MovieList> response) {
                generateMovieList(response.body().getMovieList());
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<MovieList> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Something went wrong...", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void generateMovieList(ArrayList<Movie> movieData) {
        recyclerView = findViewById(R.id.recycler_view);
        adapter = new MovieAdapter(movieData, this);
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
        } else {
            setSortBy(getString(R.string.top_rated_sort_by));
        }
        createMovieList(service);
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
