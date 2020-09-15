package com.projects.assignment.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.projects.assignment.data.ArticleDao

class NewsViewModelFactory(val articleDao: ArticleDao,private val NEWS_API_BASE_URL:String): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewsViewModel(articleDao,NEWS_API_BASE_URL) as T
    }
}