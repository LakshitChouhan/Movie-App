package com.example.movieapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface MoviesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMovie(movie: Movies)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllMovies(movies : List<Movies>)

    @Query("SELECT * FROM Movies ORDER BY id ASC")
    suspend fun getAllPopularMovies() : List<Movies>

    @Query("SELECT * FROM Movies WHERE id = :movieId LIMIT 1")
    suspend fun getBookMarkMovie(movieId: Int): Movies

    @Query("SELECT * FROM Movies WHERE bookmark = 1")
    suspend fun getAllBookMarkMovies(): List<Movies>

    @Update
    suspend fun updateMovie(movie: Movies)
}