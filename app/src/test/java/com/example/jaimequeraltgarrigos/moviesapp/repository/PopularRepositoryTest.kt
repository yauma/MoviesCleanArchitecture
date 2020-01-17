package com.example.jaimequeraltgarrigos.moviesapp.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.jaimequeraltgarrigos.moviesapp.api.MoviesService
import com.example.jaimequeraltgarrigos.moviesapp.api.MoviesServiceResponse
import com.example.jaimequeraltgarrigos.moviesapp.db.MovieDao
import com.example.jaimequeraltgarrigos.moviesapp.db.MoviesDB
import com.example.jaimequeraltgarrigos.moviesapp.model.Movie
import com.example.jaimequeraltgarrigos.moviesapp.model.PopularMoviesResult
import com.example.jaimequeraltgarrigos.moviesapp.model.Resource
import com.example.jaimequeraltgarrigos.moviesapp.util.ApiUtil.successCall
import com.example.jaimequeraltgarrigos.moviesapp.util.Constants
import com.example.jaimequeraltgarrigos.moviesapp.util.InstantAppExecutors
import com.example.jaimequeraltgarrigos.moviesapp.util.TestUtil
import com.example.jaimequeraltgarrigos.moviesapp.util.mock
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.*
import org.mockito.Matchers
import org.mockito.Mockito
import org.mockito.internal.matchers.Any

class PopularRepositoryTest {
    private lateinit var repository: PopularRepository
    private val dao = Mockito.mock(MovieDao::class.java)
    private val service = Mockito.mock(MoviesService::class.java)
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        val db = Mockito.mock(MoviesDB::class.java)
        Mockito.`when`(db.movieDao()).thenReturn(dao)
        Mockito.`when`(db.runInTransaction(any())).thenCallRealMethod()
        repository = PopularRepository(InstantAppExecutors(), dao, service)
    }

    @Test
    fun loadRepoFromNetwork() {
        val dbData = MutableLiveData<List<Movie>>()
        val expectedResponse = MutableLiveData<PopularMoviesResult>()
        val movies = MoviesServiceResponse(
            1, 2, 1,
            TestUtil.createMoviesList(Array(2) { "A";"B" })
        )
        val response = TestUtil.createPopularMoviesResult(movies = movies.movies)
        expectedResponse.value = response

        Mockito.`when`(dao.loadOrdered(anyList())).thenReturn(dbData)
        Mockito.`when`(dao.getLiveDataPopularMoviesResult()).thenReturn(expectedResponse)

        val call = successCall(movies)
        Mockito.`when`(service.getPopularMovies(Constants.API_KEY,1)).thenReturn(call)

        val data = repository.loadPopularMovies(1, true)
        Mockito.verify(dao).getLiveDataPopularMoviesResult()
        Mockito.verifyNoMoreInteractions(service)

        val observer = mock<Observer<Resource<List<Movie>>>>()
        data.observeForever(observer)
        Mockito.verifyNoMoreInteractions(service)
        Mockito.verify(observer).onChanged(Resource.loading(null))
        val updatedDbData = MutableLiveData<List<Movie>>()
        Mockito.`when`(dao.loadOrdered(anyList())).thenReturn(updatedDbData)

        dbData.postValue(null)
        Mockito.verify(service).getPopularMovies(Constants.API_KEY,1)
        val moviesArg = movies.movies
        Mockito.verify(dao).insert(moviesArg[0],moviesArg[1])

        updatedDbData.postValue(movies.movies)
        Mockito.verify(observer).onChanged(Resource.success(movies.movies))
    }

}