package com.jaredmuralt.sportsnews.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jaredmuralt.sportsnews.R
import kotlinx.android.synthetic.main.activity_view_article.*

class ViewArticleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_article)

        val url = intent.getStringExtra(getString(R.string.url))

        webView.loadUrl(url)
    }
}