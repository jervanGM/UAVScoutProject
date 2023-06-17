package com.example.uavscoutproject.mainscreen.home.newsapi

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton object for initializing and accessing the Retrofit client.
 */
object RetrofitClient {
    private const val BASE_URL = "https://newsapi.org/v2/"

    /**
     * Lazily initializes and returns the Retrofit instance.
     */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Lazily creates and returns the NewsApiService instance.
     */
    val newsApiService: NewsApiService by lazy {
        retrofit.create(NewsApiService::class.java)
    }
}
