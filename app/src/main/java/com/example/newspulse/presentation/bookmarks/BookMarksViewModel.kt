package com.example.newspulse.presentation.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newspulse.data.dao.NewsDao
import com.example.newspulse.data.model.News
import com.example.newspulse.utils.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookMarksViewModel @Inject constructor(
    private val dao: NewsDao
) : ViewModel() {

    private val _bookMarksState = MutableStateFlow<ResultState<String>>(ResultState.Loading)
    val bookMarksState: StateFlow<ResultState<String>> = _bookMarksState


    fun getAllBookMarks() = dao.getNews()


    fun deleteNews(news: News) {
        _bookMarksState.value = ResultState.Loading

        viewModelScope.launch {
            try {
                dao.deleteNews(news)
                _bookMarksState.value = ResultState.Success("News Removed Successfully")
            } catch (e: Exception) {
                _bookMarksState.value = ResultState.Error(e.message.toString())
            }
        }

    }


}