package com.example.uavscoutproject.mainscreen.home.newsapi

import com.example.uavscoutproject.mainscreen.home.data.Article

/**
 * Represents the response received from the news API.
 *
 * @property status The status of the API response.
 * @property articles The list of articles returned by the API.
 */
data class NewsApiResponse(
    val status: String,
    val articles: List<Article>
)


