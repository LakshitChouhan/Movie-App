package com.example.movieapp.server

import com.example.movieapp.database.Movies

data class MovieResponse(
    val results: List<Movies>,
    val total_pages: Int,
    val page: Int
)