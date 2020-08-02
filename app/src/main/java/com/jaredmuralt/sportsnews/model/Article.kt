package com.jaredmuralt.sportsnews.model

data class Article(
    val author: String,
    val content: String,
    val description: String,
    val publishedAt: String,
    val source: Source?,
    val title: String,
    val url: String,
    val urlToImage: String,
    var id: String
){
    constructor() : this("", "", "", "", null, "", "", "", "")
}