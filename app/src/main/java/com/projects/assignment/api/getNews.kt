package com.projects.assignment.api

import com.projects.assignment.models.topHeadlines
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface getNews {

    @GET("/v2/top-headlines")
    fun getTopHeadlines(
        @Query("country") country:String="in",
        @Query("category") category: String="general",
        @Query("apiKey") apiKey:String
    ):Call<topHeadlines>


}