package com.projects.assignment.models


import com.google.gson.annotations.SerializedName

data class topHeadlines(
        @SerializedName("articles")
    val articles: List<Article>,
        @SerializedName("status")
    val status: String,
        @SerializedName("totalResults")
    val totalResults: Int,

        var category:String?
)