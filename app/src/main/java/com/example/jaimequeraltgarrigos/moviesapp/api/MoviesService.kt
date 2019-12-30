package com.example.jaimequeraltgarrigos.moviesapp.api

import androidx.lifecycle.LiveData
import com.example.jaimequeraltgarrigos.moviesapp.model.Movie
import retrofit2.http.GET
import retrofit2.http.Path

interface MoviesService {
    @GET("movie/popular")
    fun getPopularMovies(): LiveData<ApiResponse<List<Movie>>>
}