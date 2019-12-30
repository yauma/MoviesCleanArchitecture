package com.example.jaimequeraltgarrigos.moviesapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.jaimequeraltgarrigos.moviesapp.model.Movie

/**
 * Main database description.
 */
@Database(
    entities = [
        Movie::class],
    version = 1,
    exportSchema = false
)
abstract class MoviesDB : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}