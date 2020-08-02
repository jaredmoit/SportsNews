package com.jaredmuralt.sportsnews.api

import com.jaredmuralt.sportsnews.model.NewsArticles
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPIEndpoints{

    @GET("top-headlines?country=us&category=sports")
    fun getNewsArticles(@Query("apiKey") key: String): Call<NewsArticles>
}