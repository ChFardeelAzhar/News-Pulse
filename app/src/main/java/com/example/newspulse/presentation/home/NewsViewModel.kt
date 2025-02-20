package com.example.newspulse.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.newspulse.data.model.News
import com.example.newspulse.domain.usecase.GetNewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val useCase: GetNewsUseCase
) : ViewModel() {

    init {
        viewModelScope.launch {
            val result = getNews()
            result.forEach { news ->
                Log.d("NEWS_TITLE", "${news.title}: ")
            }
        }

    }

    suspend fun getNews(): List<News> {
        return useCase.invoke("us", null, null).news
    }

}