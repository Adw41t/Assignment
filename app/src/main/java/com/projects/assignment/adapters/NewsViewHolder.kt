package com.projects.assignment.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.projects.assignment.R
import com.projects.assignment.databinding.NewsListItemBinding
import com.projects.assignment.models.Article
import com.projects.assignment.ui.NewsActivity
import java.text.SimpleDateFormat
import java.util.*

class NewsViewHolder(binding: NewsListItemBinding) : RecyclerView.ViewHolder(binding.root) {
    val newsImage = binding.newsListItemImage
    val newsTitle = binding.newsListItemTitle
    val newsDescription = binding.newsListItemDescription
    val newsSource = binding.newsListItemSource
    val newsPublishedTime = binding.newsListItemTime
    val newsShare = binding.newsListItemShare
    fun bindView(context: Context, article: Article){
        if(article.urlToImage!=null) {
            Glide.with(context)
                    .asBitmap()
                    .load(article.urlToImage)
                    .listener(object : RequestListener<Bitmap> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                            newsImage.visibility = GONE
                            return true
                        }

                        override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            return false
                        }
                    })
                    .into(newsImage)
        }
        else{
            newsImage.visibility=GONE
        }
        newsTitle.text=article.title
        if(article.description!=null && !article.description.isEmpty()) {
            newsDescription.text = article.description
        }
        else {
            newsDescription.visibility = GONE
        }
        itemView.setOnClickListener(View.OnClickListener {
            val bundle = Bundle()
            bundle.putSerializable(context.getString(R.string.articleObject), article)
            val intent = Intent(context, NewsActivity::class.java)
            intent.putExtra(context.getString(R.string.articleBundle), bundle)
            context.startActivity(intent);
        })

        if(article.source?.name!=null) {
            newsSource.text = article.source.name
        }
        else {
            newsSource.visibility = INVISIBLE
        }

        if(article.publishedAt!=null) {
            newsPublishedTime.text = convertTimeFromUTC(article.publishedAt)
        }
        else {
            newsPublishedTime.visibility = INVISIBLE
        }

        if(article.url!=null && article.url.isNotEmpty()){
            newsShare.setOnClickListener {
                val intent = Intent(Intent.ACTION_SEND)
                val shareBody = article.title + "\n\n" + article.url
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.share_news))
                intent.putExtra(Intent.EXTRA_TEXT, shareBody)
                context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_news)))
            }
        }
    }

    private fun convertTimeFromUTC(time: String):String{
        var dateTime=time.removeSuffix("Z")
        dateTime=dateTime.replace("T", " ", false)
        dateTime=dateTime.replace("Z", "", false)
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        df.timeZone = TimeZone.getTimeZone("UTC")
        val date = df.parse(dateTime)
        val fmt = SimpleDateFormat("dd-MM-yy HH:mm:aa", Locale.ENGLISH)
        fmt.timeZone = TimeZone.getDefault()
        return fmt.format(date)
    }
}