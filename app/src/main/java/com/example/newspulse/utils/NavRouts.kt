package com.example.newspulse.utils

import com.example.newspulse.data.model.News
import com.google.gson.Gson
import java.net.URLDecoder
import java.net.URLEncoder

object NavRouts {

    sealed class Destination(val route: String) {
        data object NewsHomeScreen : Destination("home_screen")
        data object NewsDetailScreen : Destination("detail_screen?news={news}")
    }


    fun createDetailScreenRoute(news: News): String {

        val encodedImage = URLEncoder.encode(news.image, "utf-8")
        val encodedUrl = URLEncoder.encode(news.url, "utf-8")

        val tempNews = news.copy(image = encodedImage, url = encodedUrl)
        val gson = Gson().toJson(tempNews)
        return "detail_screen?news=$gson"
    }


    fun getNewsFromRoute(json: String): News {

        val news = Gson().fromJson(json, News::class.java)
        val decodedImage = URLDecoder.decode(news.image, "utf-8")
        val decodedUrl = URLDecoder.decode(news.url, "utf-8")

        return news.copy(image = decodedImage, url = decodedUrl)
    }
}