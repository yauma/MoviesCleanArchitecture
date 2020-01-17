package com.example.jaimequeraltgarrigos.moviesapp.util

import com.example.jaimequeraltgarrigos.moviesapp.model.Movie
import com.example.jaimequeraltgarrigos.moviesapp.model.PopularMoviesResult

class TestUtil {
    companion object {
        fun createMoviesList(moviesNames: Array<String>): List<Movie> {
            val movies = arrayListOf<Movie>()
            var id = 0
            for (movieName in moviesNames) {
                id++
                val movie = Movie(id, movieName, "url")
                movies.add(movie)
            }
            return movies
        }

        fun createPopularMoviesResult(movies: List<Movie>): PopularMoviesResult {
            val ids =  mutableListOf<Int>()
            for (movie in movies) {
                ids.add(movie.id)
            }
            return PopularMoviesResult(1, ids.toList(), ids.size, 2)
        }
    }
}