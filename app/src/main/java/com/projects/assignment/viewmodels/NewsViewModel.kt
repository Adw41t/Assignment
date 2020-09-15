package com.projects.assignment.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.projects.assignment.data.ArticleDao
import com.projects.assignment.models.Article
import com.projects.assignment.models.topHeadlines
import com.projects.assignment.repository.newsRepository


class NewsViewModel(
         articleDao: ArticleDao,
         NEWS_API_BASE_URL:String
) : ViewModel() {
    var newsRepository: newsRepository
    var topHeadlinesLiveData: LiveData<topHeadlines>

    init {
        newsRepository=newsRepository(articleDao,NEWS_API_BASE_URL)
        topHeadlinesLiveData=newsRepository.getTopHeadlinesLiveData()
    }

    fun getArticlesByCategory(country: String,category: String):LiveData<List<Article>>{
        return newsRepository.getArticlesByCategory(country,category)
    }
}