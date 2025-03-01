package com.example.newspulse.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.newspulse.data.dao.NewsDao
import com.example.newspulse.data.database.NewsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(context: Context): NewsDatabase {
        return Room.databaseBuilder(context, NewsDatabase::class.java, "news_db")
            .fallbackToDestructiveMigrationFrom()
            .build()
    }

    @Provides
    @Singleton
    fun provideDao(database: NewsDatabase): NewsDao {
        return database.newsDao()
    }


}