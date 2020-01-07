package com.example.jaimequeraltgarrigos.moviesapp.db

import android.util.SparseIntArray
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.jaimequeraltgarrigos.moviesapp.api.MoviesServiceResponse
import com.example.jaimequeraltgarrigos.moviesapp.model.Movie
import com.example.jaimequeraltgarrigos.moviesapp.model.PopularMoviesResult
import java.util.*

@Dao
abstract class MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(movies: List<Movie>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(popularMoviesResult: PopularMoviesResult)

    @Query("SELECT * FROM movie")
    abstract fun loadMovies(): LiveData<List<Movie>>

    @Query("SELECT * FROM popularmoviesresult")
    abstract fun getPopularMoviesResult(): LiveData<PopularMoviesResult>

    fun loadOrdered(moviesId: List<Int>): LiveData<List<Movie>> {
        val order = SparseIntArray()
        moviesId.withIndex().forEach {
            order.put(it.value, it.index)
        }
        return Transformations.map(loadById(moviesId)) { repositories ->
            Collections.sort(repositories) { r1, r2 ->
                val pos1 = order.get(r1.id)
                val pos2 = order.get(r2.id)
                pos1 - pos2
            }
            repositories
        }
    }

    @Query("SELECT * FROM Movie WHERE id in (:repoIds)")
    protected abstract fun loadById(repoIds: List<Int>): LiveData<List<Movie>>

    @Query("Select next FROM popularmoviesresult")
    abstract fun getNextPage(): Int?
}