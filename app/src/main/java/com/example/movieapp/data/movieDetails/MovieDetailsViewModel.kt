package com.example.movieapp.data.movieDetails

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.database.Movies
import com.example.movieapp.server.MovieResponseDetails
import com.example.movieapp.repositories.RepositoryMovieDetailsData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val repositoryMovieDetailsData: RepositoryMovieDetailsData
) : ViewModel() {
    val movieDetails : MutableLiveData<MovieResponseDetails> by lazy { MutableLiveData<MovieResponseDetails>() }
    val movieBookmarkDetails: MutableLiveData<Movies> by lazy { MutableLiveData<Movies>() }

    fun getMovieDetails(movieId : String) {
        viewModelScope.launch {
            repositoryMovieDetailsData.getMovieDetail(movieId).collect {
                movieDetails.postValue(it)
            }
        }
    }

    fun getMovieBookmarkDetails(movieId: Int) {
        viewModelScope.launch {
            movieBookmarkDetails.postValue(repositoryMovieDetailsData.getMovieBookmarkInfo(movieId))
        }
    }

    fun getNewMovieBookmark(movieId: String) {
        viewModelScope.launch {
            repositoryMovieDetailsData.getMovieDetail(movieId).collect {
                val movie = Movies(
                    movieId.toInt(), it.original_title, it.release_date, it.poster_path,
                    it.vote_average.toFloat(),false
                )
                repositoryMovieDetailsData.insertMovie(movie)
            }
        }
    }

    fun updateMovieBookmark(movie: Movies) {
        viewModelScope.launch {
            repositoryMovieDetailsData.updateMovieBookmarkInfo(movie)
        }
    }
}