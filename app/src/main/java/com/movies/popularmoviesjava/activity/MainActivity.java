package com.movies.popularmoviesjava.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    private String sortBy = "popular";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createMovieList();
    }

    private void createMovieList() {
        GetMovieDataService service = RetrofitInstance.getInstance().create(GetMovieDataService.class);

        Call<MovieList> call = service.getMovieData(getSortBy(), ApiKey.KEY);
        Log.d("URL called", call.request().url().toString());


        call.enqueue(new Callback<MovieList>() {
            @Override
            public void onResponse(Call<MovieList> call, Response<MovieList> response) {
                generateMovieList(response.body().getMovieList());
            }

            @Override
            public void onFailure(Call<MovieList> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Something went wrong...", Toast.LENGTH_LONG).show();
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
            setSortBy("popular");
        } else {
            setSortBy("top_rated");
        }
        createMovieList();
        return true;
    }

    @Override
    public void onItemClick(Movie movie) {
        Intent detailActivity = new Intent(this, DetailActivity.class);
        detailActivity.putExtra(DetailActivity.TITLE, movie.getTitle());
        detailActivity.putExtra(DetailActivity.IMAGE_PATH, movie.getPosterPath());
        detailActivity.putExtra(DetailActivity.RELEASE_DATE, movie.getReleaseDate());
        detailActivity.putExtra(DetailActivity.SYNOPSIS, movie.getSynopsis());
        detailActivity.putExtra(DetailActivity.USER_RATING, movie.getUserRating());
        startActivity(detailActivity);
    }
}
