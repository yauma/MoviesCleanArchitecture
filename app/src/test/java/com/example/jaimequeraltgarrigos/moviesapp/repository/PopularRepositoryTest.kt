package com.example.jaimequeraltgarrigos.moviesapp.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.jaimequeraltgarrigos.moviesapp.api.MoviesService
import com.example.jaimequeraltgarrigos.moviesapp.api.MoviesServiceResponse
import com.example.jaimequeraltgarrigos.moviesapp.db.MovieDao
import com.example.jaimequeraltgarrigos.moviesapp.db.MoviesDB
import com.example.jaimequeraltgarrigos.moviesapp.model.Movie
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
import org.mockito.Mockito

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
        Mockito.`when`(db.runInTransaction(ArgumentMatchers.any())).thenCallRealMethod()
        repository = PopularRepository(InstantAppExecutors(), dao, service)
    }

    @Test
    fun loadRepoFromNetwork() {
        val dbData = MutableLiveData<List<Movie>>()
        Mockito.`when`(dao.loadMovies()).thenReturn(dbData)

        val movies = MoviesServiceResponse(
            1, 2, 1,
            TestUtil.createMoviesList(Array(2) { "A";"B" })
        )
        val call = successCall(movies)
        Mockito.`when`(service.getPopularMovies(Constants.API_KEY)).thenReturn(call)

        val data = repository.loadPopularMovies()
        Mockito.verify(dao).loadMovies()
        Mockito.verifyNoMoreInteractions(service)

        val observer = mock<Observer<Resource<List<Movie>>>>()
        data.observeForever(observer)
        Mockito.verifyNoMoreInteractions(service)
        Mockito.verify(observer).onChanged(Resource.loading(null))
        val updatedDbData = MutableLiveData<List<Movie>>()
        Mockito.`when`(dao.loadMovies()).thenReturn(updatedDbData)

        dbData.postValue(null)
        Mockito.verify(service).getPopularMovies(Constants.API_KEY)
        Mockito.verify(dao).insert(movies = movies.movies)

        updatedDbData.postValue(movies.movies)
        Mockito.verify(observer).onChanged(Resource.success(movies.movies))
    }

}