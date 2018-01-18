package com.example.fabio.meusfilmes;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.fabio.meusfilmes.models.Movie;
import com.example.fabio.meusfilmes.models.Page;
import com.example.fabio.meusfilmes.utils.MainAdapter;
import com.example.fabio.meusfilmes.utils.MovieAPI;
import com.example.fabio.meusfilmes.utils.MovieDBService;
import com.example.fabio.meusfilmes.utils.SearchAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";

    private MovieDBService service;
    private Page page;

    private RecyclerView recyclerView;
    private SearchAdapter adapter;

    private List<Movie> moviesList = new ArrayList<>();
    private String title;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Recupera a ID do filme da Activity de busca e posição do item para posterior utilização
        title = getIntent().getStringExtra("Title");
        position = getIntent().getIntExtra("Position", -1);

        //Ativação do botão home
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.search_recyclerview);

        service = MovieAPI.getApiClient().create(MovieDBService.class);
        loadMoviesPage();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Call<Page> callMoviesAPI(){
        return service.listMovies(getString(R.string.api_key), title, "pt-BR");
    }

    private void loadMoviesPage(){

        callMoviesAPI().enqueue(new Callback<Page>() {
            @Override
            public void onResponse(Call<Page> call, Response<Page> response) {

                //Log de erros de comunicação com o servidor e código de resposta
                if(!response.isSuccessful()){
                    Log.i(TAG, "Código de erro: " + response.code());
                }
                else {
                    //Sucesso
                    page = response.body();
                    moviesList = page.getResults();

                    //Cria uma adapter e atribui na RecyclerView
                    adapter = new SearchAdapter(moviesList, SearchActivity.this, position);
                    setRecyclerView(SearchActivity.this, recyclerView, adapter);
                }
            }

            @Override
            public void onFailure(Call<Page> call, Throwable t) {
                //Log de erros de conexão com a API
                Log.i(TAG, "Erro: " + t.getMessage());
            }
        });
    }


    //Atribui a lista de dados e tipo de Layout na RecyclerView
    public void setRecyclerView(Activity activity, RecyclerView rv, SearchAdapter adapter){
        //Utilização da Layout vertical
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                activity,
                LinearLayoutManager.VERTICAL,
                false);

        rv.setAdapter(adapter);
        rv.setLayoutManager(layoutManager);
    }
}
