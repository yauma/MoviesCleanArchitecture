package com.example.jaimequeraltgarrigos.moviesapp.util

import com.example.jaimequeraltgarrigos.moviesapp.model.Movie

class TestUtil {
    companion object {
        fun createMoviesList(moviesNames: Array<String>): List<Movie> {
            val movies = arrayListOf<Movie>()
            var id = 0
            for (movieName in moviesNames) {
                id++
                val movie = Movie(id.toString(), movieName, "url")
                movies.add(movie)
            }
            return movies
        }
    }
}