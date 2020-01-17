package com.example.jaimequeraltgarrigos.moviesapp.ui.popular

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.jaimequeraltgarrigos.moviesapp.model.Movie
import com.example.jaimequeraltgarrigos.moviesapp.model.Resource
import com.example.jaimequeraltgarrigos.moviesapp.repository.PopularRepository
import javax.inject.Inject

class PopularViewModel @Inject constructor(val popularRepository: PopularRepository) : ViewModel() {

    var loadingMore = MutableLiveData<Boolean>()
    private var currentPage = MutableLiveData<Int>()
    private var firsPage = true
    var movies: LiveData<Resource<List<Movie>>> = Transformations.switchMap(currentPage) {
        popularRepository.loadPopularMovies(it, firsPage)
    }

    fun loadMovies() {
        loadingMore.value = false
        currentPage.value = 1

    }

    fun loadNextPage(nextPage: Int) {
        loadingMore.value = true
        firsPage = false
        currentPage.value = nextPage
    }
}