package com.example.movieapp.data.search

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.database.Movies
import com.example.movieapp.repositories.RepositoryData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchMovieViewModel @Inject constructor(private val repositoryData: RepositoryData) : ViewModel() {
    val movieResults : MutableLiveData<List<Movies>> by lazy { MutableLiveData<List<Movies>>() }

    fun getSearchMovies(movieName: String) {
        viewModelScope.launch {
            repositoryData.getSearchMovies(movieName).collect {
                movieResults.postValue(it)
            }
        }
    }

}