package com.jaredmuralt.sportsnews.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.FirebaseError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jaredmuralt.sportsnews.FirebaseUtil
import com.jaredmuralt.sportsnews.R
import com.jaredmuralt.sportsnews.model.Article
import kotlinx.android.synthetic.main.activity_main.*

class SavedArticlesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        swipeContainer.isEnabled = false

        toolbar.title = getString(R.string.saved_for_later)
        swipeContainer.setOnRefreshListener {
            getSavedForLaterArticles()
        }

        getSavedForLaterArticles()
    }

    private fun getSavedForLaterArticles(){
        newsArticleRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@SavedArticlesActivity)
            adapter =
                NewsArticleAdapter(
                    FirebaseUtil.savedArticles, this@SavedArticlesActivity
                )
        }
    }
}