package com.example.newspulse.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newspulse.data.model.News
import com.example.newspulse.data.response.NewsResponse
import com.example.newspulse.domain.usecase.GetNewsUseCase
import com.example.newspulse.utils.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val useCase: GetNewsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ResultState<NewsResponse>>(ResultState.Loading)
    val state: StateFlow<ResultState<NewsResponse>> = _state

    private var job: Job? = null

    init {
        getNews()
    }

    fun getNews(
        text: String? = null,
        country: String? = null
    ) {


        job?.cancel()

        _state.value = ResultState.Loading
        Log.d("NewsViewModel", "Fetching news...")

        job = viewModelScope.launch {
            try {
                val result = useCase.execute("us", text, country)
                _state.value = ResultState.Success(result)

                Log.d("NewsViewModel", "News fetched successfully: ${result.news.size} articles")

            } catch (e: Exception) {
                _state.value = ResultState.Error(e.message.toString())
                Log.e("NewsViewModel", "Error fetching news: ${e.message}")

            }
        }

    }

}