package com.example.movieapp.repositories

import android.util.Log
import com.example.movieapp.database.MovieDatabase
import com.example.movieapp.server.MovieDataApi
import com.example.movieapp.server.MovieResponseDetails
import com.example.movieapp.database.Movies
import com.example.movieapp.util.Constants
import com.example.movieapp.util.ErrorUtils.handleError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.time.Year
import java.util.concurrent.TimeoutException
import javax.inject.Inject

class RepositoryMovieDetailsData @Inject constructor(
    private val movieDataApi: MovieDataApi, private val movieDatabase: MovieDatabase
) {
    // Get movie detail for the movieId
    suspend fun getMovieDetail(movieId: String): Flow<MovieResponseDetails> = flow {
        try {
            val response = movieDataApi.getMovieDetails(movieId, Constants.API_KEY)
            if (response.isSuccessful && response.body() != null) {
                emit(response.body()!!)
            } else {
                // Handle non-successful response (e.g., 404, 500, etc.)
                Log.e("Server Error", "Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            handleError(e)
        }
    }

    // Get bookmarked movie with the movie id
    suspend fun getMovieBookmarkInfo(movieId: Int): Movies {
        return movieDatabase.getMoviesDao().getBookMarkMovie(movieId)
    }

    // Update bookmarked movie with the movie id
    suspend fun updateMovieBookmarkInfo(movie: Movies) {
        movieDatabase.getMoviesDao().updateMovie(movie)
    }

    suspend fun insertMovie(movie: Movies) {
        movieDatabase.getMoviesDao().insertMovie(movie)
    }

    companion object {
        private const val TAG = "RepositoryMovieDetailsData"
    }
}