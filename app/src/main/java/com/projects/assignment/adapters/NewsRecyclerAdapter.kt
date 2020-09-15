package com.projects.assignment.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.projects.assignment.R
import com.projects.assignment.models.Article

class NewsRecyclerAdapter(val context: Context,val list:List<Article>) : RecyclerView.Adapter<NewsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.news_list_item,parent,false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        return holder.bindView(context,list.get(position))
    }

    override fun getItemCount(): Int {
        return list.size
    }
}