package com.example.newspulse.data.response

import com.example.newspulse.data.model.News

data class NewsResponse(
    val available: Int,
    val news: List<News>,
    val number: Int,
    val offset: Int
)