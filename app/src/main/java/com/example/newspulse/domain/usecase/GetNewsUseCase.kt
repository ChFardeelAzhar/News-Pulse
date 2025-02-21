package com.example.newspulse.domain.usecase

import com.example.newspulse.data.response.NewsResponse
import com.example.newspulse.domain.repository.NewsRepository
import org.intellij.lang.annotations.Language
import javax.inject.Inject

class GetNewsUseCase @Inject constructor(
    private val repository: NewsRepository
) {

    /*
    // this is a special kind of function in kotlin its make possible to call a function without name

    suspend operator fun invoke(
        language: String,
        text: String?,
        country: String?,
    ) = repository.newsRepository(language, text, country)

     */

    // alternate of this operator function
    suspend fun execute(
        language: String,
        text: String?,
        country: String?,
    ): NewsResponse {

        val response = repository.newsRepository(language, text, country)

        if (response.body() == null) {
            if (response.code() == 404) {
                throw Exception("No news found")
            } else if (response.code() == 500) {
                throw Exception("Server Error")
            } else if (response.code() == 401) {
                throw Exception("Unauthorized")
            } else if (response.code() == 400) {
                throw Exception("Bad Response")
            } else
                throw Exception("No News Found")
        }

        return repository.newsRepository(language, text, country).body()!!

    }


}