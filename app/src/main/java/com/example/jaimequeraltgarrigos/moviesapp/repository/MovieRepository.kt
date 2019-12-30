package com.example.jaimequeraltgarrigos.moviesapp.repository

import androidx.lifecycle.LiveData
import com.example.jaimequeraltgarrigos.moviesapp.AppExecutors
import com.example.jaimequeraltgarrigos.moviesapp.api.ApiResponse
import com.example.jaimequeraltgarrigos.moviesapp.api.MoviesService
import com.example.jaimequeraltgarrigos.moviesapp.db.MovieDao
import com.example.jaimequeraltgarrigos.moviesapp.db.MoviesDB
import com.example.jaimequeraltgarrigos.moviesapp.model.Movie
import com.example.jaimequeraltgarrigos.moviesapp.model.Resource
import com.example.jaimequeraltgarrigos.moviesapp.util.RateLimiter
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val db: MoviesDB,
    private val movieDao: MovieDao,
    private val moviesService: MoviesService
) {
    private val repoListRateLimit = RateLimiter<String>(10, TimeUnit.MINUTES)

    fun loadPopularMovies(): LiveData<Resource<List<Movie>>> {
        return object : NetworkBoundResource<List<Movie>, List<Movie>>(appExecutors) {
            override fun saveCallResult(item: List<Movie>) {
                movieDao.insert(item)
            }

            override fun shouldFetch(data: List<Movie>?): Boolean {
                return data == null || data.isEmpty() || repoListRateLimit.shouldFetch("Movies")
            }

            override fun loadFromDb(): LiveData<List<Movie>> {
                return movieDao.loadMovies()
            }

            override fun createCall(): LiveData<ApiResponse<List<Movie>>> {
                return moviesService.getPopularMovies()
            }

        }.asLiveData()
    }

}