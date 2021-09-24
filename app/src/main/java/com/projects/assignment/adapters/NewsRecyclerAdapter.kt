package com.projects.assignment.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.projects.assignment.databinding.NewsListItemBinding
import com.projects.assignment.models.Article

class NewsRecyclerAdapter(val context: Context, val list: List<Article>) : RecyclerView.Adapter<NewsViewHolder>() {
    private lateinit var binding : NewsListItemBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        binding = NewsListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        return holder.bindView(context, list.get(position))
    }

    override fun getItemCount(): Int {
        return list.size
    }
}