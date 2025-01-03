package com.example.movieapp.repositories

import android.util.Log
import com.example.movieapp.database.MovieDatabase
import com.example.movieapp.database.Movies
import com.example.movieapp.server.MovieDataApi
import com.example.movieapp.util.Constants
import com.example.movieapp.util.ErrorUtils.handleError
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class RepositoryData (private val movieDataApi: MovieDataApi, private val movieDatabase: MovieDatabase) {
    private lateinit var moviesList : List<Movies>

    // Get all movies with the movieType (popular, top rates, now playing, etc.. )
    suspend fun getMovies(movieType : String, page: String) = flow {
        moviesList = getMoviesFromLocal()
        loadMoviesRemote(movieType, page)
            .catch {
                 handleError(it)
                if (::moviesList.isInitialized) emit(moviesList to 0)
            }
            .collect { emit(it) }
    }

    // Get all movies with the movie name
    suspend fun getSearchMovies(movieName : String, page: String) = flow {
        loadSearchMovies(movieName, page).catch {
            handleError(it)
            emit(listOf<Movies>() to 0)
        }.collect {
            emit(it)
        }
    }

    // Get all bookmark movies
    suspend fun getAllBookMarkMovies(): List<Movies> {
        return movieDatabase.getMoviesDao().getAllBookMarkMovies()
    }

    private suspend fun getMoviesFromLocal() : List<Movies>{
        return movieDatabase.getMoviesDao().getAllPopularMovies()
    }

    private suspend fun loadMoviesRemote(movieType: String, page: String) = flow {
        val response = movieDataApi.getMovie(movieType, page, Constants.API_KEY)
        if (response.isSuccessful && response.body() != null) {
            response.body()?.let {
                moviesList = getMappedMovies(it.results)
                movieDatabase.getMoviesDao().insertAllMovies(moviesList)
                emit(moviesList to it.total_pages)
            }
        } else {
            Log.e(TAG, "Error loading movies : ${response.errorBody()}")
            emit(listOf<Movies>() to 0)
        }
    }

    private suspend fun loadSearchMovies(movieName : String, page: String) = flow {
        val response = movieDataApi.getSearchMovie(movieName, page, Constants.API_KEY)
        if (response.isSuccessful && response.body() != null) {
            response.body()?.let {
                emit(it.results to it.total_pages)
            }
        }else{
            Log.e(TAG, "Error searching movies : ${response.errorBody()}" )
        }
    }

    private fun getMappedMovies(movies : List<Movies>) : List<Movies>{
        val localMovies: MutableList<Movies> = mutableListOf()
        movies.forEach {
            localMovies.add(
                Movies(it.id,it.original_title,it.release_date,it.poster_path,it.vote_average)
            )
        }
        return localMovies
    }

    companion object {
        private const val TAG = "RepositoryData"
    }
}