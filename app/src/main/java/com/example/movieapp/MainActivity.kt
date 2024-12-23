package com.example.movieapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.adapters.MoviesAdapter
import com.example.movieapp.data.bookmark.BookMarkActivity
import com.example.movieapp.data.search.SearchMoviesActivity
import com.example.movieapp.database.Movies
import com.example.movieapp.databinding.ActivityMoviesBinding
import com.example.movieapp.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var moviesViewModel : MovieViewModel
    private lateinit var binding: ActivityMoviesBinding
    private lateinit var moviesAdapter: MoviesAdapter

    private val isLoading = AtomicBoolean(false)
    private var currentPage = 1
    private var totalPage = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMoviesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        moviesViewModel = ViewModelProvider(this)[MovieViewModel::class.java]

        binding.movieRecyclerView.layoutManager = GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false)

        lifecycleScope.launch {
            getRecyclerViewData(R.id.popular_tab)
        }

        var prevView: View = binding.popularTab
        binding.popularTab.setOnClickListener {
            setCurrentButton(it, prevView)
            prevView = it
        }

        binding.topRatedTab.setOnClickListener {
            setCurrentButton(it, prevView)
            prevView = it
        }

        binding.nowPlayingTab.setOnClickListener {
            setCurrentButton(it, prevView)
            prevView = it
        }

        moviesViewModel.movies.observe(this) {
            loadMoviesToAdapter(it.first, it.second)
        }

        binding.movieRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1) && !isLoading.get() && currentPage < totalPage) {
                    currentPage++
                    lifecycleScope.launch { getRecyclerViewData(recyclerView.id) }
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                val intent = Intent(this, SearchMoviesActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_bookmark -> {
                val intent = Intent(this, BookMarkActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private suspend fun getRecyclerViewData(id : Int){
        if (isLoading.get()) return  // Prevent multiple requests at the same time
        isLoading.set(true)
        val type = when(id){
            R.id.popular_tab ->  Constants.POPULAR
            R.id.top_Rated_tab -> Constants.TOP_RATED
            else -> Constants.NOW_PLAYING
        }
        repeat(5) {
            moviesViewModel.getMovies(type, currentPage)
            delay(500)
            currentPage++
        }
    }

    private fun setCurrentButton(view: View, prevView: View) {
        currentPage = 1
        lifecycleScope.launch { getRecyclerViewData(view.id) }
        view.setBackgroundResource(R.drawable.home_button_set)
        prevView.setBackgroundResource(R.drawable.home_button)
    }

    private fun loadMoviesToAdapter(moviesList: List<Movies>, total: Int) {
        totalPage = total
        isLoading.set(false)
        if(currentPage == 1) {
            moviesAdapter = MoviesAdapter(moviesList.toMutableList(), applicationContext)
            binding.movieRecyclerView.adapter = moviesAdapter
        } else {
            if(::moviesAdapter.isInitialized) moviesAdapter.addMovies(moviesList)
        }
    }
}
