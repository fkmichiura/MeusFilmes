package com.example.fabio.meusfilmes.utils;

import com.example.fabio.meusfilmes.models.Movie;
import com.example.fabio.meusfilmes.models.Page;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Fabio on 15/01/2018.
 */

public interface MovieDBService {

    String BASE_URL = "https://api.themoviedb.org/3/";

    @GET("search/movie")
    Call<Page> listMovies(
            @Query("api_key") String key,
            @Query("query") String query,
            @Query("language") String language);

    @GET("movie/{id}")
    Call<Movie> getMovieInfo(
            @Path("id") int id,
            @Query("api_key") String key,
            @Query("language") String language);
}
