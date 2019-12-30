package com.example.jaimequeraltgarrigos.moviesapp.db

import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.jaimequeraltgarrigos.moviesapp.model.Movie

interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(movie: List<Movie>)

    @Query("SELECT * FROM movie")
    fun loadMovies(): LiveData<List<Movie>>
}