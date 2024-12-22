package com.example.movieapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.database.Movies
import com.example.movieapp.repositories.RepositoryData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor( private val repository: RepositoryData) : ViewModel() {
    val movies : MutableLiveData<List<Movies>> by lazy { MutableLiveData<List<Movies>>() }

    fun getMovies(type : String, page: Int) {
        viewModelScope.launch {
            repository.getMovies(type, page.toString()).collect {
                movies.postValue(it)
            }
        }
    }
}