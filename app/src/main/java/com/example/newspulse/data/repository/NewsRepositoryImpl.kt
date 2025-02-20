package com.example.newspulse.data.repository

import com.example.newspulse.data.response.NewsResponse
import com.example.newspulse.data.web.NewsApi
import com.example.newspulse.domain.repository.NewsRepository
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor( private val api: NewsApi) : NewsRepository {
    override suspend fun newsRepository(language: String, text: String?, country: String?): NewsResponse {

        val response = api.getNews(country = country, text = text, language = language)
        return response.body()!!

    }


}