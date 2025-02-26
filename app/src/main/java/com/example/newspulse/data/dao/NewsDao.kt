package com.example.newspulse.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.newspulse.data.model.News
import com.example.newspulse.utils.ResultState

@Dao
interface NewsDao {


    @Query("SELECT * FROM news")
    fun getNews(): ResultState<List<News>>

    @Insert
    suspend fun addNews(news: News)

    @Delete
    suspend fun deleteNews(news: News)

}