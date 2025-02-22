package com.example.newspulse.domain.usecase

import android.util.Log
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
        Log.d("GetNewsUseCase", "Fetching news for language: $language, text: $text, country: $country")

        val response = repository.newsRepository(language, text, country)

        Log.d("GetNewsUseCase", "Response Code: ${response.code()}")

        if (response.body() == null) {

            Log.e("GetNewsUseCase", "Error - Response body is null")

            if (response.code() == 404) {
                throw Exception("No news found")
            } else if (response.code() == 500) {
                throw Exception("Server Error")
            } else if (response.code() == 401) {
                throw Exception("Unauthorized")
            } else if (response.code() == 400) {
                throw Exception("Bad Response")
            } else if (response.code() == 402) {
                throw Exception("Your free plan is over")
            } else
                throw Exception("No News Found")

        } else {
            Log.d("GetNewsUseCase", "News fetched successfully")
            return repository.newsRepository(language, text, country).body()!!
        }




    }


}