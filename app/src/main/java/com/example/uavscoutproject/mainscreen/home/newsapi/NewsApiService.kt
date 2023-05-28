package com.example.uavscoutproject.mainscreen.home.newsapi


import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("everything?q=(FPV AND dron)&sortBy=publishedAt&language=es")
    suspend fun getDroneArticles(
        @Query("apiKey") apiKey: String
    ): retrofit2.Response<NewsApiResponse>
}
