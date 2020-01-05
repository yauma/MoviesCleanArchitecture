package com.example.jaimequeraltgarrigos.moviesapp.api

import com.example.jaimequeraltgarrigos.moviesapp.model.Movie
import com.google.gson.annotations.SerializedName
/**
 * Simple object to hold repo search responses. This is different from the Entity in the database
 * because we are keeping a search result in 1 row and denormalizing list of results into a single
 * column.
 */
data class MoviesServiceResponse constructor(
    @SerializedName("page")
    val page: Int,
    @SerializedName("total_results")
    val total_results: Int,
    @SerializedName("total_pages")
    val total_pages: Int,
    @SerializedName("results")
    val movies: List<Movie>
) {
}