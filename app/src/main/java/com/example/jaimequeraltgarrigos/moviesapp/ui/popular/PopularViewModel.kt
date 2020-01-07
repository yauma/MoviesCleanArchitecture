package com.example.jaimequeraltgarrigos.moviesapp.ui.popular

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.jaimequeraltgarrigos.moviesapp.model.Movie
import com.example.jaimequeraltgarrigos.moviesapp.model.Resource
import com.example.jaimequeraltgarrigos.moviesapp.model.Status
import com.example.jaimequeraltgarrigos.moviesapp.repository.PopularRepository
import com.example.jaimequeraltgarrigos.moviesapp.util.AbsentLiveData
import java.util.*
import javax.inject.Inject

class PopularViewModel @Inject constructor(val popularRepository: PopularRepository) : ViewModel() {

    private val reloadTrigger = MutableLiveData<Boolean>()
    private var nextPage: Int? = 0
    var movies: LiveData<Resource<List<Movie>>> = Transformations.switchMap(reloadTrigger) {
        popularRepository.loadPopularMovies()
    }

    fun loadMovies() {
        nextPage = popularRepository.getNextPage()
        reloadTrigger.value = true
    }
}