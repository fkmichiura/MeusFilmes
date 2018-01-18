package com.example.fabio.meusfilmes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.fabio.meusfilmes.models.Movie;
import com.example.fabio.meusfilmes.utils.MainAdapter;
import com.example.fabio.meusfilmes.utils.MovieAPI;
import com.example.fabio.meusfilmes.utils.MovieDBService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MainActivity";
    private SharedPreferences preferences;

    private MovieDBService service;

    private FloatingActionButton fab;
    private EditText editText;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MainAdapter adapter;

    private List<Movie> registeredMovies = new ArrayList<>();
    private int id, position;

    private String listPrefs = "List_prefs";
    private String positionPref = "Position_pref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Recupera a ID do filme da Activity de busca
        id = getIntent().getIntExtra("Movie ID", 0);
        position = getIntent().getIntExtra("Position", -1);
        getSharedPreferences();

        recyclerView = findViewById(R.id.main_recyclerview);
        fab = findViewById(R.id.main_fab);

        fab.setOnClickListener(this);

        //Faz a chamada da API para recuperar os dados e atualizar o item do filme
        service = MovieAPI.getApiClient().create(MovieDBService.class);
        loadMovieInfo();

        //Cria uma adapter e atribui na RecyclerView
        setAdapter();
    }

    @Override
    protected void onPause() {
        super.onPause();
        setSharedPreferences(position);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSharedPreferences();
    }

    @Override
    public void onClick(View view) {
        showInputDialog();
    }

    //Responsável pela caixa de diálogo
    protected void showInputDialog() {

        // Inflar a Layout de prompt title_layout.xml
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View promptView = layoutInflater.inflate(R.layout.title_layout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(promptView);

        //Associa a variável a sua id na .xml
        editText = promptView.findViewById(R.id.edittext);

        //Configuração da janela de diálogo
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        //Cria um novo item Filme e adiciona na lista
                        Movie movie = new Movie(editText.getText().toString());
                        adapter.addMovie(movie, adapter.getItemCount());
                    }
                })
                .setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        //Criação de um AlertDialog e mostrar na View
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private Call<Movie> callMoviesInfoAPI(){
        return service.getMovieInfo(id, getString(R.string.api_key),"pt-BR");
    }

    private void loadMovieInfo(){

        callMoviesInfoAPI().enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                //Log de erros de comunicação com o servidor e código de resposta
                if(!response.isSuccessful()){
                    Log.i(TAG, "Código de erro: " + response.code());
                }
                else {
                    //Sucesso
                    getSharedPreferences();

                    //Atribui o conteúdo consumido pela API ao item selecionado
                    Movie movieInfo = response.body();
                    setMovieInfo(movieInfo);

                    setAdapter();
                    return;
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                //Log de erros de conexão com a API
                Log.i(TAG, "Erro: " + t.getMessage());
            }
        });
    }

    private void setAdapter() {

        //Cria uma adapter e atribui na RecyclerView
        adapter = new MainAdapter(registeredMovies, MainActivity.this);
        setRecyclerView(MainActivity.this, recyclerView, adapter);
    }

    private void setMovieInfo(Movie movieInfo) {

        Movie info = registeredMovies.get(position);
        info.setTitle(movieInfo.getTitle());
        info.setOverview(movieInfo.getOverview());
        info.setRelease_date(movieInfo.getRelease_date());
        info.setVote_average(movieInfo.getVote_average());
        info.setVote_count(movieInfo.getVote_count());
    }

    //Atribui a lista de dados e tipo de Layout na RecyclerView
    public void setRecyclerView(Activity activity, RecyclerView rv, MainAdapter adapter){
        //Utilização da Layout vertical
        layoutManager = new LinearLayoutManager(
                activity,
                LinearLayoutManager.VERTICAL,
                false);

        rv.setAdapter(adapter);
        rv.setLayoutManager(layoutManager);
    }

    //Salva os dados referentes aos filmes salvos na lista
    public void setSharedPreferences(int pos){

        preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();

        String json = gson.toJson(registeredMovies);

        editor.putString(listPrefs, json);
        editor.commit();
    }

    //Recupera os dados referentes aos filmes salvos na lista
    public void getSharedPreferences(){

        preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        //Verifica se há conteúdo na SharedPreferences
        if(preferences.contains(listPrefs)){
            Gson gson = new Gson();
            String json = preferences.getString(listPrefs, null);
            Type type = new TypeToken<ArrayList<Movie>>() {}.getType();
            registeredMovies = gson.fromJson(json, type);
        }
    }
}
