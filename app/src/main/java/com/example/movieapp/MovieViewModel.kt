package com.example.movieapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.database.Movies
import com.example.movieapp.repositories.RepositoryData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor( private val repository: RepositoryData) : ViewModel() {
    val movies : MutableLiveData<Pair<List<Movies>, Int>> by lazy { MutableLiveData<Pair<List<Movies>, Int>>() }

    fun getMovies(type : String, page: Int) {
        viewModelScope.launch {
            repository.getMovies(type, page.toString()).collect {
                movies.postValue(it)
            }
        }
    }
}