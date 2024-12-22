package com.example.movieapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.adapters.MoviesAdapter
import com.example.movieapp.data.bookmark.BookMarkActivity
import com.example.movieapp.database.Movies
import com.example.movieapp.data.search.SearchMoviesActivity
import com.example.movieapp.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var moviesViewModel : MovieViewModel
    private var tTemp:String = "popular"
    private var tbool : Int = 1
    private lateinit var movieRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movies)

        moviesViewModel = ViewModelProvider(this)[MovieViewModel::class.java]

        movieRecyclerView = findViewById(R.id.movieRecyclerView)
        val popularTab: Button = findViewById(R.id.popular_tab)
        val topRatedTab: Button = findViewById(R.id.top_Rated_tab)
        val nowPlayingTab: Button = findViewById(R.id.Now_Playing_tab)

        movieRecyclerView.layoutManager = GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false)

        getRecyclerViewData(R.id.popular_tab)

        var prevView: View = popularTab

        popularTab.setOnClickListener {
            getRecyclerViewData(R.id.popular_tab)
            it.setBackgroundResource(R.drawable.home_button_set)
            prevView.setBackgroundResource(R.drawable.home_button)
            prevView = it
        }

        topRatedTab.setOnClickListener {
            getRecyclerViewData(R.id.top_Rated_tab)
            it.setBackgroundResource(R.drawable.home_button_set)
            prevView.setBackgroundResource(R.drawable.home_button)
            prevView = it
        }

        nowPlayingTab.setOnClickListener {
            getRecyclerViewData(R.id.Now_Playing_tab)
            it.setBackgroundResource(R.drawable.home_button_set)
            prevView.setBackgroundResource(R.drawable.home_button)
            prevView = it
        }

        moviesViewModel.movies.observe(this, Observer {
            populateMovieRecycler(it)
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

    private fun getRecyclerViewData(id : Int){
        val type = when(id){
            R.id.popular_tab ->  Constants.POPULAR
            R.id.top_Rated_tab -> Constants.TOP_RATED
            else -> Constants.NOW_PLAYING
        }
        if(type != tTemp) {
            tTemp = type
        }
        moviesViewModel.getMovies(type, tbool)
    }

    private fun populateMovieRecycler(moviesList: List<Movies>) {
        movieRecyclerView.adapter = MoviesAdapter(moviesList, applicationContext)
    }

}
