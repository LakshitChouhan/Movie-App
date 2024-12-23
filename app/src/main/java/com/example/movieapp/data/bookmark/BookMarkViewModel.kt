package com.example.movieapp.data.bookmark

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.database.Movies
import com.example.movieapp.repositories.RepositoryData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookMarkViewModel @Inject constructor(private val repositoryData: RepositoryData) : ViewModel() {
    val bookmarkMovies : MutableLiveData<List<Movies>> by lazy { MutableLiveData<List<Movies>>() }

    fun getAllMovies() {
        viewModelScope.launch {
            bookmarkMovies.postValue(repositoryData.getAllBookMarkMovies())
        }
    }

}