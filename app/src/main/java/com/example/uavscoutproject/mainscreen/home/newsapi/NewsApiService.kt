package com.example.uavscoutproject.mainscreen.home.newsapi


import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit API service for retrieving drone-related articles from a news API.
 */
interface NewsApiService {
    /**
     * Retrieves drone articles from the news API.
     *
     * @param apiKey The API key for authentication.
     * @return A Retrofit response containing the drone articles.
     */
    @GET("everything?q=(FPV AND dron)&sortBy=publishedAt&language=es")
    suspend fun getDroneArticles(
        @Query("apiKey") apiKey: String
    ): retrofit2.Response<NewsApiResponse>
}

