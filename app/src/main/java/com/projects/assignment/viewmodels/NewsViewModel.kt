package com.projects.assignment.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.projects.assignment.models.Article
import com.projects.assignment.models.topHeadlines
import com.projects.assignment.repository.newsRepository


class NewsViewModel @ViewModelInject constructor(val newsRepository:newsRepository) : ViewModel() {
    var topHeadlinesLiveData: LiveData<topHeadlines>

    init {
        topHeadlinesLiveData=newsRepository.getTopHeadlinesLiveData()
    }

    fun getArticlesByCategory(country : String, category : String, getNews : Boolean):LiveData<List<Article>>{
        return newsRepository.getArticlesByCategory(country, category, getNews)
    }
}