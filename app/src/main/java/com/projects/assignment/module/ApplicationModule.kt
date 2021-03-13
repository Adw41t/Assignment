package com.projects.assignment.module

import android.content.Context
import com.projects.assignment.R
import com.projects.assignment.api.getNews
import com.projects.assignment.data.ArticleDao
import com.projects.assignment.data.NewsDatabase
import com.projects.assignment.repository.newsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class ApplicationModule {

    @Provides
    fun provideDatabase(@ApplicationContext context: Context):NewsDatabase{
        return NewsDatabase.getInstance(context)
    }

    @Provides
    fun provideArticleDao(database:NewsDatabase):ArticleDao{
        return database.articleDao()
    }

    @Provides
    fun provideNewsApiUrl(@ApplicationContext context: Context):String{
        return context.getString(R.string.NEWS_API_BASE_URL)
    }

    @Provides
    @Singleton
    fun provideRetrofit(news_api_url: String): Retrofit =
            Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(news_api_url)
                    .build()

    @Provides
    @Singleton
    fun provideGetNewsApiService(retrofit: Retrofit) = retrofit.create(getNews::class.java)

    @Provides
    @Singleton
    fun provideNewsRepository(
            articleDao: ArticleDao,
            getNewsBuilder: getNews
    ): newsRepository{
        return newsRepository(articleDao,getNewsBuilder)
    }
}
@EntryPoint
@InstallIn(ApplicationComponent::class)
interface newsRepositoryFieldInjectEntryPoint {
    fun newsRepo(): newsRepository
}