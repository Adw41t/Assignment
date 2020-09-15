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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class newsRepository(
        private val articleDao:ArticleDao,
        NEWS_API_BASE_URL:String
) {
    private val  NEWS_API_KEY= BuildConfig.NEWS_API_KEY
    private var getNewsBuilder: getNews
    var topHeadlinesLiveData: MutableLiveData<topHeadlines> = MutableLiveData()

    init {
        getNewsBuilder= Retrofit.Builder()
                .baseUrl(NEWS_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(getNews::class.java)
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