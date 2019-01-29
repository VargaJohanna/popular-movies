package com.movies.popularmoviesjava.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.movies.popularmoviesjava.R;
import com.movies.popularmoviesjava.model.TrailerVideo;

import java.util.ArrayList;
import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {
    private List<TrailerVideo> trailerList;
    final private ItemClickListener itemClickListener;


    public TrailerAdapter(List<TrailerVideo> trailerList, ItemClickListener itemClickListener) {
        this.trailerList = trailerList;
        this.itemClickListener = itemClickListener;
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

    class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final TextView trailerTitle;

        TrailerAdapterViewHolder(View itemView) {
            super(itemView);
            trailerTitle = itemView.findViewById(R.id.row_trailer_title);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick(trailerList.get(getAdapterPosition()));
        }
    }

    public interface ItemClickListener {
        void onItemClick(TrailerVideo video);
    }
}
