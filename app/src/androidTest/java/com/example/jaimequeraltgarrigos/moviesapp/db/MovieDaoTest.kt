package com.example.jaimequeraltgarrigos.moviesapp.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.runner.AndroidJUnit4
import com.example.jaimequeraltgarrigos.moviesapp.util.LiveDataTestUtil
import com.example.jaimequeraltgarrigos.moviesapp.util.TestUtil
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MovieDaoTest : MoviesDBTest() {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun writeMoviesAndReadFromDB() {
        val movies = TestUtil.createMoviesList(Array<String>(3) { "B";"C";"D" })
        db.movieDao().insert(movies[0],movies[1],movies[2])
        val moviesFromDB = LiveDataTestUtil.getValue(db.movieDao().loadMovies())
        Assert.assertEquals(moviesFromDB.get(0).id, movies.get(0).id)
    }
}