package com.example.newspulse.presentation.news_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newspulse.data.dao.NewsDao
import com.example.newspulse.data.database.NewsDatabase
import com.example.newspulse.data.model.News
import com.example.newspulse.utils.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsDetailsViewModel @Inject constructor(
    private val database: NewsDatabase,
    private val dao: NewsDao
) : ViewModel() {

    val _newsState = MutableStateFlow<ResultState<Boolean>>(ResultState.Loading)
    val newsState: StateFlow<ResultState<Boolean>> = _newsState


    fun addNews(news: News) {
        _newsState.value = ResultState.Loading

        viewModelScope.launch {
            try {
                dao.addNews(news)
                _newsState.value = ResultState.Success(true)
            } catch (e: Exception) {
                _newsState.value = ResultState.Error(e.message.toString())
            }

        }

    }


}