package com.movies.popularmoviesjava.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.movies.popularmoviesjava.R;
import com.movies.popularmoviesjava.adapter.MovieAdapter;
import com.movies.popularmoviesjava.model.Movie;
import com.movies.popularmoviesjava.model.MovieList;
import com.movies.popularmoviesjava.network.GetMovieDataService;
import com.movies.popularmoviesjava.network.RetrofitInstance;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private MovieAdapter adapter;
    private RecyclerView recyclerView;
    private String sortBy = "popular";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GetMovieDataService service = RetrofitInstance.getInstance().create(GetMovieDataService.class);

        Call<MovieList> call = service.getMovieData(sortBy, ApiKey.KEY);
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
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new MovieAdapter(movieData);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}
