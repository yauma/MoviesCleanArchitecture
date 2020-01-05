package com.example.jaimequeraltgarrigos.moviesapp.api

import androidx.lifecycle.LiveData
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesService {
    @GET("movie/popular")
    fun getPopularMovies(@Query("api_key") apiKey: String?): LiveData<ApiResponse<MoviesServiceResponse>>
}