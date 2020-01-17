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

    fun loadPopularMovies(page: Int, firstPage: Boolean): LiveData<Resource<List<Movie>>> {
        return object : NetworkBoundResource<List<Movie>, MoviesServiceResponse>(appExecutors) {
            var current: PopularMoviesResult = PopularMoviesResult(0, arrayListOf(), 0, 0)


            override fun saveCallResult(item: MoviesServiceResponse) {
                val popularMoviesResult = mergeData(item, current, firstPage)
                movieDao.insert(*item.movies.toTypedArray())
                movieDao.insert(popularMoviesResult)
            }

            override fun shouldFetch(data: List<Movie>?): Boolean {
                return !firstPage || data == null || data.isEmpty()
                        || repoListRateLimit.shouldFetch("Movies")
            }

            override fun loadFromDb(): LiveData<List<Movie>> {

                return Transformations.switchMap(movieDao.getLiveDataPopularMoviesResult()) {
                    if (it == null) {
                        AbsentLiveData.create()
                    } else {
                        current = it
                        movieDao.loadById(it.moviesId)

                    }
                }
            }

            override fun createCall(): LiveData<ApiResponse<MoviesServiceResponse>> {
                return moviesService.getPopularMovies(Constants.API_KEY, page)
            }

        }.asLiveData()
    }

    private fun mergeData(
        item: MoviesServiceResponse,
        current: PopularMoviesResult,
        firstPage: Boolean
    ): PopularMoviesResult {
        val ids = arrayListOf<Int>()
        ids.addAll(item.movies.map { it.id })
        if (current.moviesId.isNotEmpty() && !firstPage) {
            ids.addAll(current.moviesId)
        }
        return PopularMoviesResult(
            moviesId = ids,
            totalCount = item.total_results,
            next = item.page + 1
        )
    }

    fun getNextPage(): LiveData<Int> {
        return movieDao.getNextPage()
    }

}