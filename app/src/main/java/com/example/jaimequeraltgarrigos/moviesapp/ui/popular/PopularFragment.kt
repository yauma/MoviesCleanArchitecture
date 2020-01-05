package com.example.jaimequeraltgarrigos.moviesapp.ui.popular

import android.os.Bundle
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

    private val popularViewModel: PopularViewModel by viewModels {
        viewModelFactory
    }

    private var adapter by autoCleared<MovieListAdapter>()

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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.setLifecycleOwner(viewLifecycleOwner)
        val rvAdapter = MovieListAdapter(
            dataBindingComponent = dataBindingComponent,
            appExecutors = appExecutors
        ) { movie ->
            popularViewModel.retry()
        }
        adapter = rvAdapter
        binding.movieList.adapter = adapter
        binding.callback = object : RetryCallback {
            override fun retry() {
                popularViewModel.movies

            }
        }
        popularViewModel.retry()
        binding.popularResult = popularViewModel.movies
        popularViewModel.movies.observe(viewLifecycleOwner, Observer { result ->
            adapter.submitList(result?.data)
        })

        binding.movieList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastPosition = layoutManager.findLastVisibleItemPosition()
                if (lastPosition == adapter.itemCount - 1) {
                    popularViewModel.loadNextPage()
                }
            }
        })

    }
}