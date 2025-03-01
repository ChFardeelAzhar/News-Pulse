package com.example.newspulse.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.newspulse.data.database.Converter

@Entity(tableName = "news")
data class News(
    val authors: List<String>?,
    @PrimaryKey(autoGenerate = true) val id: Int,
    val image: String,
    val language: String,
    val publish_date: String,
    val sentiment: Double,
    val source_country: String,
    val summary: String?,
    val text: String,
    val title: String,
    val url: String,
)