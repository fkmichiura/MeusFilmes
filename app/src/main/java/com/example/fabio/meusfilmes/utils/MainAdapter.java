package com.example.fabio.meusfilmes.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fabio.meusfilmes.MainActivity;
import com.example.fabio.meusfilmes.R;
import com.example.fabio.meusfilmes.SearchActivity;
import com.example.fabio.meusfilmes.models.Movie;

import java.util.List;

/**
 * Created by Fabio on 16/01/2018.
 */

public class MainAdapter extends RecyclerView.Adapter {

    private onRecyclerViewItemClickListener listener;

    private List<Movie> movies;
    private Activity context;

    public MainAdapter(List<Movie> movies, Activity context) {
        this.movies = movies;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.movie_item, parent, false);

        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ListViewHolder viewHolder = (ListViewHolder)holder;
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

    private class ListViewHolder extends RecyclerView.ViewHolder{

        private TextView title;
        private TextView overview;
        private TextView year;
        private TextView voteAverage;
        private TextView voteCount;

        public ListViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.tv_title);
            overview = itemView.findViewById(R.id.tv_overview);
            year = itemView.findViewById(R.id.tv_year);
            voteAverage = itemView.findViewById(R.id.tv_vote_average);
            voteCount = itemView.findViewById(R.id.tv_vote_count);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, SearchActivity.class);
                    intent.putExtra("Title", movies.get(getAdapterPosition()).getTitle());
                    intent.putExtra("Position", getAdapterPosition());
                    context.startActivity(intent);
                }
            });
        }
    }

    public void addMovie(Movie movie, int index) {
        movies.add(movie);
        notifyItemInserted(index);
    }

    //Controle de eventos de click nos itens da RecyclerView
    public void setOnItemClickListener(onRecyclerViewItemClickListener listener) {
        this.listener = listener;
    }

    public interface onRecyclerViewItemClickListener {
        void onItemClickListener(View view, int position);
    }
}