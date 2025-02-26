package com.example.newspulse.data.database

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.newspulse.data.model.News
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converter {

    @TypeConverter
    fun fromListToJson(value: List<String>): String {
        val gson = Gson()
        return gson.toJson(value)
    }

    fun fromJsonToList(value: String): List<String> {

        val gson = Gson()
        val type = object : TypeToken<List<String>>() {}.type

        return gson.fromJson(value, type)

    }

}