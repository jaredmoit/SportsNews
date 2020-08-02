package com.jaredmuralt.sportsnews.model

data class NewsArticles(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)