package com.example.uavscoutproject.mainscreen.home.newsapi

import com.example.uavscoutproject.mainscreen.home.data.Article

data class NewsApiResponse(
    val status: String,
    val articles: List<Article>
)

