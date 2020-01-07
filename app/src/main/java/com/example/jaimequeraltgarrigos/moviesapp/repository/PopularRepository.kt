package com.example.jaimequeraltgarrigos.moviesapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.jaimequeraltgarrigos.moviesapp.AppExecutors
import com.example.jaimequeraltgarrigos.moviesapp.api.ApiResponse
import com.example.jaimequeraltgarrigos.moviesapp.api.MoviesService
import com.example.jaimequeraltgarrigos.moviesapp.api.MoviesServiceResponse
import com.example.jaimequeraltgarrigos.moviesapp.db.MovieDao
import com.example.jaimequeraltgarrigos.moviesapp.model.Movie
import com.example.jaimequeraltgarrigos.moviesapp.model.PopularMoviesResult
import com.example.jaimequeraltgarrigos.moviesapp.model.Resource
import com.example.jaimequeraltgarrigos.moviesapp.util.AbsentLiveData
import com.example.jaimequeraltgarrigos.moviesapp.util.Constants
import com.example.jaimequeraltgarrigos.moviesapp.util.RateLimiter
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PopularRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val movieDao: MovieDao,
    private val moviesService: MoviesService
) {
    private val repoListRateLimit = RateLimiter<String>(10, TimeUnit.MINUTES)

    fun loadPopularMovies(): LiveData<Resource<List<Movie>>> {
        return object : NetworkBoundResource<List<Movie>, MoviesServiceResponse>(appExecutors) {
            override fun saveCallResult(item: MoviesServiceResponse) {
                val moviesId = item.movies.map { it.id }
                val popularMoviesResult: PopularMoviesResult = PopularMoviesResult(
                    moviesId = moviesId,
                    totalCount = item.total_results,
                    next = item.page + 1
                )
                movieDao.insert(item.movies)
                movieDao.insert(popularMoviesResult)
            }

            override fun shouldFetch(data: List<Movie>?): Boolean {
                return data == null || data.isEmpty() || repoListRateLimit.shouldFetch("Movies")
            }

            override fun loadFromDb(): LiveData<List<Movie>> {
                return Transformations.switchMap(movieDao.getPopularMoviesResult()) {
                    if (it == null){
                        AbsentLiveData.create()
                    }else{
                        movieDao.loadOrdered(it.moviesId)

                    }
                }
            }

            override fun createCall(): LiveData<ApiResponse<MoviesServiceResponse>> {
                return moviesService.getPopularMovies(Constants.API_KEY)
            }

        }.asLiveData()
    }

}