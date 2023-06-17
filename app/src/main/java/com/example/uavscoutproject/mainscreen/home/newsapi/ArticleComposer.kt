package com.example.uavscoutproject.mainscreen.home.newsapi

import androidx.compose.runtime.mutableStateListOf
import com.example.uavscoutproject.mainscreen.home.data.Article

/**
 * Object for managing a list of articles.
 */
object ArticleComposer {

    private val articleList = mutableStateListOf<Article>()

    /**
     * Inserts a new list of articles, replacing the existing list.
     *
     * @param newList The new list of articles to insert.
     */
    fun insertList(newList: List<Article>) {
        articleList.clear()
        articleList.addAll(newList)
    }

    /**
     * Retrieves the list of articles.
     *
     * @return The list of articles.
     */
    fun getList(): List<Article> {
        return articleList
    }
}
