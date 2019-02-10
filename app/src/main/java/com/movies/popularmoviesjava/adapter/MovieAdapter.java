package com.movies.popularmoviesjava.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.movies.popularmoviesjava.R;
import com.movies.popularmoviesjava.model.Movie;
import com.movies.popularmoviesjava.network.RetrofitInstance;
import com.movies.popularmoviesjava.utilities.ImageSize;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {
    private List<Movie> movieList;
    final private ItemClickListener itemClickListener;

    public MovieAdapter(List<Movie> movieList, ItemClickListener itemClickListener) {
        this.movieList = movieList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_movie, parent, false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapterViewHolder holder, int position) {
        Picasso.get()
                .load(RetrofitInstance.IMAGE_BASE_URL + ImageSize.getImageSize(3) + movieList.get(position).getPosterPath())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.posterView);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView posterView;

        MovieAdapterViewHolder(View itemView) {
            super(itemView);
            posterView = itemView.findViewById(R.id.movieImage);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick(movieList.get(getAdapterPosition()));
        }
    }

    public interface ItemClickListener {
        void onItemClick(Movie movie);
    }

    public void updateList(List<Movie> list) {
        movieList = list;
        notifyDataSetChanged();
    }
}
