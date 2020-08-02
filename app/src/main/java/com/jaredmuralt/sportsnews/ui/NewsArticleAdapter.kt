package com.jaredmuralt.sportsnews.ui

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.jaredmuralt.sportsnews.FirebaseUtil
import com.jaredmuralt.sportsnews.R
import com.jaredmuralt.sportsnews.Utils
import com.jaredmuralt.sportsnews.model.Article


class NewsArticleAdapter(private val articles: List<Article>, private val context: Context) :
    RecyclerView.Adapter<NewsArticleViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsArticleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.article_item, parent, false)
        return NewsArticleViewHolder(view)
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    override fun onBindViewHolder(holder: NewsArticleViewHolder, position: Int) {
        return holder.bind(articles[position], context)
    }
}

class NewsArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val photo: ImageView = itemView.findViewById(R.id.article_photo)
    private val title: TextView = itemView.findViewById(R.id.article_title)
    private val source: TextView = itemView.findViewById(R.id.article_source)
    private val saveForLater : ImageButton = itemView.findViewById(R.id.save_for_later)

    fun bind(article: Article, context: Context) {
        if (!article.urlToImage.isNullOrBlank()) {
            val options = RequestOptions().centerCrop()
            Glide.with(itemView.context).load(article.urlToImage).apply(options).into(photo)
        } else photo.visibility = View.GONE

        if(article.url.contains("news.google.com")){
            val options = RequestOptions().centerCrop()
            Glide.with(itemView.context).load("https://cdn.mos.cms.futurecdn.net/SytNGv3ZxAVCkvcspmbbvh.jpg").apply(options).into(photo)
            photo.visibility = View.VISIBLE
        }
        val formattedDate = Utils.DateToTimeFormat(article.publishedAt)

        title.text = article.title
        source.text = "${article.source?.name} - ${formattedDate}"

        itemView.setOnClickListener {
            val intent = Intent(context, ViewArticleActivity::class.java).apply {
                putExtra(context.getString(R.string.url), article.url)
            }
            startActivity(context, intent, null)
        }

        var savedForLater = article.url in FirebaseUtil.savedArticles.map { it.url }
        if(savedForLater) saveForLater.setImageResource(R.drawable.ic_action_saved_for_later)
        else saveForLater.setImageResource(R.drawable.ic_action_save_for_later)

        saveForLater.setOnClickListener {
            if(article.url in FirebaseUtil.savedArticles.map { it.url }){
                //remove to saved for later
                savedForLater = false
                saveForLater.setImageResource(R.drawable.ic_action_save_for_later)
                FirebaseUtil.removeArticleFromSavedForLater(article)
            }else{
                //add to saved for later
                savedForLater = true
                saveForLater.setImageResource(R.drawable.ic_action_saved_for_later)
                FirebaseUtil.addArticleToSavedForLater(article)
            }

        }
    }
}