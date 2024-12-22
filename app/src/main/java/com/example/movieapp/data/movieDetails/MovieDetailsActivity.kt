package com.example.movieapp.data.movieDetails

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.data.UrspRule
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.data.bookmark.BookMarkActivity
import com.example.movieapp.data.search.SearchMoviesActivity
import com.example.movieapp.server.MovieResponseDetails
import com.example.movieapp.util.Constants
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var movieViewModel : MovieDetailsViewModel
    private lateinit var movieId: String
    private lateinit var movieResponseDetails: MovieResponseDetails

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        movieViewModel = ViewModelProvider(this)[MovieDetailsViewModel::class.java]
        movieId = intent.getStringExtra("id").toString()

        var isFirst = true
        var isBookmarked = false

        val backdropPath = findViewById<ImageView>(R.id.backdrop_path)
        val posterPath = findViewById<ImageView>(R.id.poster_path)
        val movieName = findViewById<TextView>(R.id.movie_name)
        val overviewDetail = findViewById<TextView>(R.id.overview_detail)
        val statusDetails = findViewById<TextView>(R.id.statusDetails)
        val releaseDateDetails = findViewById<TextView>(R.id.releaseDateDetails)
        val voteAverageDetail = findViewById<TextView>(R.id.vote_average_detail)
        val bookmark = findViewById<Button>(R.id.bookmark)
        val shareButton = findViewById<Button>(R.id.share)

        movieViewModel.getMovieBookmarkDetails(movieId.toInt())
        bookmark.setOnClickListener {
            isBookmarked = !isBookmarked
            movieViewModel.getMovieBookmarkDetails(movieId.toInt())
        }

        movieViewModel.movieBookmarkDetails.observe(this) {
            it?.let { movie ->
                if (isFirst) {
                    if (movie.bookmark) {
                        bookmark.setBackgroundResource(R.drawable.baseline_bookmark_24)
                    } else {
                        bookmark.setBackgroundResource(R.drawable.baseline_bookmark_border_24)
                    }
                    isFirst = false
                    movieViewModel.updateMovieBookmark(movie)
                } else {
                    if (movie.bookmark && !isBookmarked) {
                        bookmark.setBackgroundResource(R.drawable.baseline_bookmark_border_24)
                        movie.bookmark = false
                        movieViewModel.updateMovieBookmark(movie)
                    }
                    else if (!movie.bookmark && isBookmarked) {
                        bookmark.setBackgroundResource(R.drawable.baseline_bookmark_24)
                        movie.bookmark = true
                        movieViewModel.updateMovieBookmark(movie)
                    }
                }
            } ?: movieViewModel.getNewMovieBookmark(movieId)
        }

        movieViewModel.getMovieDetails(movieId)
        movieViewModel.movieDetails.observe(this) {
            it?.let {
                Log.d("MovieDataDetails", "Backdrop path: ${it.backdrop_path}")
                Log.d("MovieDataDetails", "Poster path: ${it.poster_path}")
                movieResponseDetails = it
                Glide.with(application)
                    .load(Constants.BASE_IMAGE_URL + it.backdrop_path)
                    .into(backdropPath)

                Glide.with(application)
                    .load(Constants.BASE_IMAGE_URL + it.poster_path)
                    .into(posterPath)

                movieName.text = it.original_title
                overviewDetail.text = it.overview
                statusDetails.text = it.status
                releaseDateDetails.text = it.release_date
                voteAverageDetail.text = it.vote_average.toString()
            }
        }

        shareButton.setOnClickListener {
            if (::movieResponseDetails.isInitialized) {
                sendMovieData(movieResponseDetails)
            }
        }
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

            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun sendMovieData(movieResponseDetails: MovieResponseDetails) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        val data = Gson().toJson(movieResponseDetails)
        shareIntent.putExtra(Intent.EXTRA_TEXT, data)
        startActivity(Intent.createChooser(shareIntent, "Share Movie"))
    }
}
