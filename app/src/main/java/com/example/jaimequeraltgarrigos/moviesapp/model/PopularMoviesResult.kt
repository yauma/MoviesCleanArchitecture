package com.example.jaimequeraltgarrigos.moviesapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.jaimequeraltgarrigos.moviesapp.db.DataTypeConverters

/**
 * Simple object to hold popular movies search responses. This is different from the Entity in the database
 * because we are keeping a search result in 1 row and denormalizing list of results into a single
 * column.
 */
@Entity()
@TypeConverters(DataTypeConverters::class)
data class PopularMoviesResult(
    @PrimaryKey()
    val id: Int = 0,
    val moviesId: List<Int>,
    val totalCount: Int,
    val next: Int?
)