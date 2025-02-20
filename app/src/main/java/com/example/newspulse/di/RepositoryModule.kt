package com.example.newspulse.di

import com.example.newspulse.data.repository.NewsRepositoryImpl
import com.example.newspulse.data.web.NewsApi
import com.example.newspulse.domain.repository.NewsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideNewsRepository(
        api: NewsApi
    ): NewsRepository {
        return NewsRepositoryImpl(api)

    }

}