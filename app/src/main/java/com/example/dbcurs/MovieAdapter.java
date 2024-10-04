package com.example.dbcurs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MovieAdapter extends DBItemAdapter<Movie, MovieAdapter.MovieViewHolder> {

    public MovieAdapter(Context context, List<Movie> list, OnItemClickListener<Movie> onClickListener) { super(context, list, R.layout.movie_item, onClickListener); }

    @Override
    protected MovieViewHolder createViewHolder(View view) { return new MovieViewHolder(view); }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        Movie movie = items.get(position);
        holder.title.setText(movie.title);
        holder.director.setText(movie.director);
        holder.genre.setText(movie.genre);
        holder.starring.setText(movie.starring);
        holder.year.setText(movie.releaseYear > 0 ? Integer.toString(movie.releaseYear) : "");
        holder.studio.setText(movie.studio);
        holder.annotation.setText(movie.annotation);
        holder.rentCost.setText(Float.toString(movie.rentCost));

        holder.itemView.setOnClickListener(view -> itemClick(view, movie, position));
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView
        title,
        director,
        genre,
        starring,
        year,
        studio,
        annotation,
        rentCost;

        private View view;

        MovieViewHolder(View view)
        {
            super(view);
            title = view.findViewById(R.id.movieTitle);
            director = view.findViewById(R.id.movieDirector);
            genre = view.findViewById(R.id.movieGenre);
            starring = view.findViewById(R.id.movieStarring);
            year = view.findViewById(R.id.movieYear);
            studio = view.findViewById(R.id.movieStudio);
            annotation = view.findViewById(R.id.movieAnnotation);
            rentCost = view.findViewById(R.id.movieRentCost);
        }
    }
}
