package com.projects.assignment.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.projects.assignment.models.Article

@Dao
interface ArticleDao {
    @Insert
    fun insert(article:Article)

    @Query("select * from newsArticle")
    fun getAllArticles():LiveData<List<Article>>

    @Query("select * from newsArticle where category like :articleCategory")
    fun getArticlesByCategory(articleCategory: String):LiveData<List<Article>>

    @Query("delete from newsArticle")
    fun deleteAllArticles()

    @Query("delete from newsArticle where category like :articleCategory")
    fun deleteArticlesByCategory(articleCategory: String)


    @Query("select * from newsArticle LIMIT 1")
    fun getTopArticle(): Article
}