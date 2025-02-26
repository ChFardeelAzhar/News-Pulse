package com.example.newspulse.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.newspulse.data.dao.NewsDao
import com.example.newspulse.data.model.News

@Database(entities = [News::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class NewsDatabase : RoomDatabase() {

    abstract fun newsDao(): NewsDao

    companion object {

        private const val DATABASE_NAME = "news_db"

        @JvmStatic
        fun getDatabase(context: Context): NewsDatabase {

            return Room.databaseBuilder(
                context,
                NewsDatabase::class.java,
                DATABASE_NAME
            ).fallbackToDestructiveMigrationFrom().build()

        }


    }


}