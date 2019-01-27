package com.movies.popularmoviesjava.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.movies.popularmoviesjava.R;
import com.movies.popularmoviesjava.activity.DetailActivity;
import com.movies.popularmoviesjava.model.TrailerVideo;
import com.movies.popularmoviesjava.model.TrailersList;

import java.util.ArrayList;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {
    private ArrayList<TrailerVideo> trailerList;

    public TrailerAdapter(ArrayList<TrailerVideo> trailerList) {
        this.trailerList = trailerList;

    }
    @NonNull
    @Override
    public TrailerAdapter.TrailerAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View rowView = inflater.inflate(R.layout.row_trailer, parent, false);
        return new TrailerAdapterViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerAdapter.TrailerAdapterViewHolder holder, int position) {
        holder.trailerTitle.setText(trailerList.get(position).getVideoTitle());
    }

    @Override
    public int getItemCount() {
        return trailerList.size();
    }

    class TrailerAdapterViewHolder extends RecyclerView.ViewHolder {
        final TextView trailerTitle;

        TrailerAdapterViewHolder(View itemView) {
            super(itemView);
            trailerTitle = itemView.findViewById(R.id.row_trailer_title);
        }
    }
}
