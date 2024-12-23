package com.example.movieapp.data.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.database.Movies
import com.example.movieapp.repositories.RepositoryData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchMovieViewModel @Inject constructor(private val repositoryData: RepositoryData) : ViewModel() {
    val movieResults : MutableLiveData<Pair<List<Movies>, Int>> by lazy { MutableLiveData<Pair<List<Movies>, Int>>() }

    fun getSearchMovies(movieName: String, page: Int) {
        viewModelScope.launch {
            repositoryData.getSearchMovies(movieName, page.toString()).collect {
                movieResults.postValue(it)
            }
        }
    }

}