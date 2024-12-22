package com.example.movieapp.data.bookmark

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R
import com.example.movieapp.adapters.MoviesAdapter
import com.example.movieapp.data.search.SearchMoviesActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookMarkActivity: AppCompatActivity() {
    private lateinit var bookmarkMoviesViewModel : BookMarkViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bookmark_movies_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        bookmarkMoviesViewModel = ViewModelProvider(this)[BookMarkViewModel::class.java]
        val bookmarkMoviesRecyclerView: RecyclerView = findViewById(R.id.bookmarkMoviesRecyclerView)
        bookmarkMoviesRecyclerView.layoutManager = GridLayoutManager(this,2, GridLayoutManager.VERTICAL,false)

        bookmarkMoviesViewModel.getAllMovies()
        bookmarkMoviesViewModel.bookmarkMovies.observe(this) {
            it?.let { bookmarkMoviesRecyclerView.adapter = MoviesAdapter(it, applicationContext) }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bookmark_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search_movies -> {
                val intent = Intent(this, SearchMoviesActivity::class.java)
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
}