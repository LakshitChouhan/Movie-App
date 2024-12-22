package com.example.movieapp.di

import android.content.Context
import androidx.room.Room
import com.example.movieapp.database.MovieDatabase
import com.example.movieapp.repositories.RepositoryData
import com.example.movieapp.repositories.RepositoryMovieDetailsData
import com.example.movieapp.server.MovieDataApi
import com.example.movieapp.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class DataMovieModule {
    @Singleton
    @Provides
    fun provideRetrofitInterface(): MovieDataApi {
        return Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(MovieDataApi::class.java)
    }

    @Singleton
    @Provides
    fun bindUserDataBase(@ApplicationContext context: Context): MovieDatabase {
        return Room.databaseBuilder(
            context.applicationContext, MovieDatabase::class.java, "popular_movies_db"
        ).allowMainThreadQueries().build()
    }

    @Provides
    fun provideRepositoryData(movieDataApi: MovieDataApi, movieDataBase: MovieDatabase): RepositoryData {
        return RepositoryData(movieDataApi, movieDataBase)
    }

    @Provides
    fun provideRepositoryDetailsData(movieDataApi: MovieDataApi, movieDataBase: MovieDatabase): RepositoryMovieDetailsData {
        return RepositoryMovieDetailsData(movieDataApi, movieDataBase)
    }

}