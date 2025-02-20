package com.example.newspulse.domain.repository

import com.example.newspulse.data.response.NewsResponse

interface NewsRepository {

    suspend fun newsRepository(
        language : String,
        text : String?,
        country : String?
    ) : NewsResponse


}