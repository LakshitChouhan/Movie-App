package com.example.movieapp.data.search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R
import com.example.movieapp.adapters.MoviesAdapter
import com.example.movieapp.data.bookmark.BookMarkActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchMoviesActivity: AppCompatActivity() {
    private lateinit var searchMoviesViewModel : SearchMovieViewModel

    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_movies_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        searchMoviesViewModel = ViewModelProvider(this)[SearchMovieViewModel::class.java]

        val searchMoviesRecyclerView: RecyclerView = findViewById(R.id.searchMoviesRecyclerView)
        val searchMovieTextView: EditText = findViewById(R.id.search_movies)

        searchMoviesRecyclerView.layoutManager = GridLayoutManager(this,2, GridLayoutManager.VERTICAL,false)

        searchMovieTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(1500)
                    searchMoviesViewModel.getSearchMovies(s.toString())
                }
            }
        })

        searchMoviesViewModel.movieResults.observe(this) {
            it?.let { searchMoviesRecyclerView.adapter = MoviesAdapter(it, applicationContext) }
        }
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
}