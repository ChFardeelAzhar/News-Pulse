package com.example.newspulse.data.web

import com.example.newspulse.data.response.NewsResponse
import com.example.newspulse.utils.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface NewsApi {

    @GET("search-news")
    suspend fun getNews(
        @Query("text") text: String?,
        @Query("country") country: String?,
        @Query("language") language: String,
        @Query("news-sources") newsSource : String? = "https://www.bbc.co.uk",
        @Query("api-key") apiKey: String = API_KEY,
    ): Response<NewsResponse>

}