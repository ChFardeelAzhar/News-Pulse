package com.example.newspulse.domain.usecase

import com.example.newspulse.domain.repository.NewsRepository
import org.intellij.lang.annotations.Language
import javax.inject.Inject

class GetNewsUseCase @Inject constructor(
    private val repository: NewsRepository
) {

    // this is a special kind of function in kotlin its make possible to call a function without name

    suspend operator fun invoke(
        language: String,
        text: String?,
        country: String?,
    ) = repository.newsRepository(language, text, country)

    /*

    // alternate of this operator function
    suspend fun execute(
        language: String,
        text: String,
        country: String,
    ) = repository.newsRepository(language, text, country)


     */
}