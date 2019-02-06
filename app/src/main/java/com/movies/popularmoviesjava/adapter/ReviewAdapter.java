package com.movies.popularmoviesjava.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.movies.popularmoviesjava.R;
import com.movies.popularmoviesjava.model.Review;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {
    private List<Review> reviewList;
    public ReviewAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View rowView = inflater.inflate(R.layout.row_review, parent, false);
        return new ReviewAdapterViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapterViewHolder holder, int position) {
        holder.reviewText.setText(reviewList.get(position).getReviewContent());
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public class ReviewAdapterViewHolder extends  RecyclerView.ViewHolder {
        final TextView reviewText;

        public ReviewAdapterViewHolder(View itemView) {
            super(itemView);
            reviewText = itemView.findViewById(R.id.review_content_text);
        }
    }
}
