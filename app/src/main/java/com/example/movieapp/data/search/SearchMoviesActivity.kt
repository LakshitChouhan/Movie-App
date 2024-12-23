package com.example.movieapp.data.search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R
import com.example.movieapp.adapters.MoviesAdapter
import com.example.movieapp.data.bookmark.BookMarkActivity
import com.example.movieapp.database.Movies
import com.example.movieapp.databinding.SearchMoviesActivityBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

@AndroidEntryPoint
class SearchMoviesActivity: AppCompatActivity() {
    private lateinit var searchMoviesViewModel : SearchMovieViewModel
    private lateinit var moviesAdapter: MoviesAdapter
    private lateinit var binding: SearchMoviesActivityBinding

    private var searchJob: Job? = null
    private val isLoading = AtomicBoolean(false)
    private var currentPage = 1
    private var totalPage = 1
    private var currentSearchMovie: String = ""
    private var prevSearchMovie: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SearchMoviesActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        searchMoviesViewModel = ViewModelProvider(this)[SearchMovieViewModel::class.java]

        binding.searchMoviesRecyclerView.layoutManager = GridLayoutManager(this,2, GridLayoutManager.VERTICAL,false)

        binding.searchMovies.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(500)
                    currentSearchMovie = s.toString()
                    loadSearchMovies(currentSearchMovie)
                }
            }
        })

        searchMoviesViewModel.movieResults.observe(this) {
            it?.let { loadMoviesToAdapter(it.first, it.second) }
            prevSearchMovie = currentSearchMovie
        }

        binding.searchMoviesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1) && !isLoading.get() && currentPage < totalPage) {
                    currentPage++
                    lifecycleScope.launch { loadSearchMovies(currentSearchMovie) }
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_bookmark -> {
                val intent = Intent(this, BookMarkActivity::class.java)
                startActivity(intent)
                true
            }

            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private suspend fun loadSearchMovies(movieName: String) {
        repeat(5) {
            searchMoviesViewModel.getSearchMovies(movieName, currentPage)
            delay(500)
            currentPage++
        }
    }

    private fun loadMoviesToAdapter(moviesList: List<Movies>, total: Int) {
        totalPage = total
        isLoading.set(false)
        if(currentPage == 1 || prevSearchMovie != currentSearchMovie) {
            moviesAdapter = MoviesAdapter(moviesList.toMutableList(), applicationContext)
            binding.searchMoviesRecyclerView.adapter = moviesAdapter
        } else {
            if(::moviesAdapter.isInitialized) moviesAdapter.addMovies(moviesList)
        }
    }
}