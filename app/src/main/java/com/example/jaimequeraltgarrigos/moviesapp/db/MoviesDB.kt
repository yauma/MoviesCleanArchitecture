package com.example.jaimequeraltgarrigos.moviesapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.jaimequeraltgarrigos.moviesapp.model.Movie
import com.example.jaimequeraltgarrigos.moviesapp.model.PopularMoviesResult

/**
 * Main database description.
 */
@Database(
    entities = [
        Movie::class,
        PopularMoviesResult::class],
    version = 1,
    exportSchema = false
)
abstract class MoviesDB : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}