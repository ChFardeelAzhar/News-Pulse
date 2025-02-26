package com.example.newspulse.di

import android.content.Context
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
    fun provideDatabase(context: Context): RoomDatabase {
        return NewsDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideDao(database: NewsDatabase): NewsDao {
        return database.newsDao()
    }


}