package com.example.jaimequeraltgarrigos.moviesapp.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.android.example.github.ui.common.DataBoundListAdapter
import com.example.jaimequeraltgarrigos.moviesapp.AppExecutors
import com.example.jaimequeraltgarrigos.moviesapp.R
import com.example.jaimequeraltgarrigos.moviesapp.databinding.MovieItemBinding
import com.example.jaimequeraltgarrigos.moviesapp.model.Movie

class MovieListAdapter constructor(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val movieClickCallback: ((Movie) -> Unit)?
) : DataBoundListAdapter<Movie, MovieItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.imageUrl == newItem.imageUrl && oldItem.title == newItem.title
        }
    }
) {
    override fun createBinding(parent: ViewGroup): MovieItemBinding {
        val binding = DataBindingUtil.inflate<MovieItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.movie_item,
            parent,
            false,
            dataBindingComponent
        )
        binding.root.setOnClickListener {
            binding.movie?.let {
                movieClickCallback?.invoke(it)
            }
        }
        return binding
    }

    override fun bind(binding: MovieItemBinding, item: Movie) {
        binding.movie = item
    }
}