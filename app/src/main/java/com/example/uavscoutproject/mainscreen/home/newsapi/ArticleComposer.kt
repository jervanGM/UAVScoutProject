package com.example.uavscoutproject.mainscreen.home.newsapi

import androidx.compose.runtime.mutableStateListOf
import com.example.uavscoutproject.mainscreen.home.data.Article

object ArticleComposer {

    private val articleList = mutableStateListOf<Article>()

    fun insertList(newList: List<Article>) {
        articleList.clear()
        articleList.addAll(newList)
    }

    fun getItem(index: Int): Article {
        return articleList[index]
    }

    fun getList(): List<Article> {
        return articleList
    }
}