package com.example.newspulse.presentation.news_detail

import android.util.Log
import androidx.compose.ui.util.trace
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
    private val dao: NewsDao
) : ViewModel() {

    private val _newsState = MutableStateFlow<ResultState<String>>(ResultState.Idle)
    val newsState: StateFlow<ResultState<String>> = _newsState

    private val _isBookmarked = MutableStateFlow(false)
    val isBookmarked: StateFlow<Boolean> = _isBookmarked

    fun checkIfBookmarked(newsId: Int) {
        viewModelScope.launch {
            _isBookmarked.value = dao.isNewsBookmarked(newsId) != null
        }
    }

    fun toggleBookmark(news: News) {
        _newsState.value = ResultState.Loading
        viewModelScope.launch {
            try {
                if (_isBookmarked.value) {
                    dao.deleteNews(news)
                    _isBookmarked.value = false
                    _newsState.value = ResultState.Success("News removed from Bookmark")
                } else {
                    dao.addNews(news)
                    _isBookmarked.value = true
                    _newsState.value = ResultState.Success("News added as Bookmark")
                }
            } catch (e: Exception) {
                _newsState.value = ResultState.Error(e.localizedMessage ?: "An error occurred")
            }
        }
    }


}