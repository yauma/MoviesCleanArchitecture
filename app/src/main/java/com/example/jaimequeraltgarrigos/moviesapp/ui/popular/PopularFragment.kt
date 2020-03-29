package com.example.jaimequeraltgarrigos.moviesapp.ui.popular

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.example.github.binding.FragmentDataBindingComponent
import com.example.jaimequeraltgarrigos.moviesapp.AppExecutors
import com.example.jaimequeraltgarrigos.moviesapp.R
import com.example.jaimequeraltgarrigos.moviesapp.databinding.PopularFragmentBinding
import com.example.jaimequeraltgarrigos.moviesapp.di.Injectable
import com.example.jaimequeraltgarrigos.moviesapp.model.Status
import com.example.jaimequeraltgarrigos.moviesapp.ui.common.MovieListAdapter
import com.example.jaimequeraltgarrigos.moviesapp.ui.common.RetryCallback
import com.example.jaimequeraltgarrigos.moviesapp.util.autoCleared
import javax.inject.Inject

class PopularFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    var binding by autoCleared<PopularFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    lateinit var scrollListener: RecyclerView.OnScrollListener

    private val popularViewModel: PopularViewModel by viewModels {
        viewModelFactory
    }

    private var adapter by autoCleared<MovieListAdapter>()

    private lateinit var linearLayoutManager: LinearLayoutManager

    private val lastVisibleItemPosition: Int
        get() = linearLayoutManager.findLastVisibleItemPosition()

    private var currentPage = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.popular_fragment,
            container,
            false,
            dataBindingComponent
        )
        linearLayoutManager = binding.movieList.layoutManager as LinearLayoutManager
        return binding.root
    }

    private fun setRecyclerViewScrollListener() {
        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val totalItemCount = recyclerView.layoutManager!!.itemCount
                if (totalItemCount == lastVisibleItemPosition + 1) {
                    currentPage++
                    popularViewModel.loadNextPage(currentPage)
                }
            }
        }
        binding.movieList.addOnScrollListener(scrollListener)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.lifecycleOwner = viewLifecycleOwner
        setRecyclerViewScrollListener()
        val rvAdapter = MovieListAdapter(
            dataBindingComponent = dataBindingComponent,
            appExecutors = appExecutors
        ) { movie ->

        }
        adapter = rvAdapter
        binding.movieList.adapter = adapter
        binding.callback = object : RetryCallback {
            override fun retry() {

            }
        }
        popularViewModel.loadMovies()
        binding.popularResult = popularViewModel.movies
        binding.loadingMore = popularViewModel.loadingMore
        popularViewModel.movies.observe(viewLifecycleOwner, Observer { result ->
            if (result.status == Status.SUCCESS) {
                popularViewModel.loadingMore.value = false
                adapter.submitList(result?.data)
            }
        })
    }
}
