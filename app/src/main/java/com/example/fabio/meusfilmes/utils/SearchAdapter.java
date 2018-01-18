package com.example.fabio.meusfilmes.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fabio.meusfilmes.MainActivity;
import com.example.fabio.meusfilmes.R;
import com.example.fabio.meusfilmes.models.Movie;

import java.util.List;

/**
 * Created by Fabio on 18/01/2018.
 */

public class SearchAdapter extends RecyclerView.Adapter {

    private List<Movie> movies;
    private Activity context;
    private int position;

    public SearchAdapter(List<Movie> movies, Activity context, int position) {
        this.movies = movies;
        this.context = context;
        this.position = position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.movie_item, parent, false);

        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SearchViewHolder viewHolder = (SearchViewHolder)holder;
        Movie movie = movies.get(position);

        viewHolder.title.setText(movie.getTitle());
        viewHolder.overview.setText(movie.getOverview());
        viewHolder.year.setText(movie.getRelease_date());
        viewHolder.voteAverage.setText(String.valueOf(movie.getVote_average()));
        viewHolder.voteCount.setText(String.valueOf(movie.getVote_count()));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder{

        private TextView title;
        private TextView overview;
        private TextView year;
        private TextView voteAverage;
        private TextView voteCount;

        public SearchViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.tv_title);
            overview = itemView.findViewById(R.id.tv_overview);
            year = itemView.findViewById(R.id.tv_year);
            voteAverage = itemView.findViewById(R.id.tv_vote_average);
            voteCount = itemView.findViewById(R.id.tv_vote_count);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Armazena o ID do Filme, relativo ao item selecionado na lista
                    int movieId = movies.get(getAdapterPosition()).getId();

                    //Envia o ID para a MainActivity
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("Movie ID", movieId);
                    intent.putExtra("Position", position);
                    context.startActivity(intent);
                }
            });
        }
    }
}
