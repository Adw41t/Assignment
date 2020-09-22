package com.projects.assignment.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.projects.assignment.BuildConfig
import com.projects.assignment.api.getNews
import com.projects.assignment.data.ArticleDao
import com.projects.assignment.models.Article
import com.projects.assignment.models.topHeadlines
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class newsRepository @Inject constructor(
        private val articleDao:ArticleDao,
        private val getNewsBuilder: getNews
) {
    private val  NEWS_API_KEY= BuildConfig.NEWS_API_KEY
    var topHeadlinesLiveData: MutableLiveData<topHeadlines> = MutableLiveData()

    init {
        topHeadlinesLiveData.observeForever{
            persistInDb(it)
        }
    }

    private fun getTopHeadlines(country:String, category:String){
        getNewsBuilder.getTopHeadlines(country,category,NEWS_API_KEY)
                .enqueue(object :Callback<topHeadlines>{
                    override fun onResponse(call: Call<topHeadlines>, response: Response<topHeadlines>) {
                        val topHeadlines=response.body()
                        if(topHeadlines!=null) {
                            topHeadlines.category = category
                            topHeadlinesLiveData.postValue(topHeadlines)
                        }
                    }

                    override fun onFailure(call: Call<topHeadlines>, t: Throwable) {
//                        topHeadlinesLiveData.postValue(null)
                    }

                })
    }
    fun getTopHeadlinesLiveData(): LiveData<topHeadlines> {
        return topHeadlinesLiveData
    }

    fun getArticlesByCategory(country: String,category: String):LiveData<List<Article>>{
        getTopHeadlines(country,category)
        return articleDao.getArticlesByCategory(category)
    }
    private fun persistInDb(headlines : topHeadlines){
        GlobalScope.launch(Dispatchers.IO){
            deleteArticlesByCategory(headlines.category)
            for (article in headlines.articles) {
                article.category=headlines.category
                articleDao.insert(article)
            }
        }
    }
    private fun deleteArticlesByCategory(category:String?){
        if(category!=null)
            articleDao.deleteArticlesByCategory(category)
        else
            articleDao.deleteAllArticles()
    }
}