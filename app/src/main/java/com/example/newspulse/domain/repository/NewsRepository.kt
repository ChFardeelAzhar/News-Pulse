package com.example.newspulse.domain.repository

import com.example.newspulse.data.response.NewsResponse
import retrofit2.Response

interface NewsRepository {

    suspend fun newsRepository(
        language: String,
        text: String?,
        country: String?
    ): Response<NewsResponse>


}