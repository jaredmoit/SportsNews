package com.jaredmuralt.sportsnews

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jaredmuralt.sportsnews.model.Article


object FirebaseUtil {

    var savedArticles : ArrayList<Article> = ArrayList()
    val database = FirebaseDatabase.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val savedArticlesRef = database.getReference("$userId/savedArticles")
    private var listener: ValueEventListener? = null

    init {
        startListener()
    }

    fun startListener(){
        listener = savedArticlesRef.addValueEventListener(object :  ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                println("CANCELLLLLLEEEEEDDDDDDD")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val children = snapshot.children
                savedArticles = ArrayList()
                children.forEach {
                    savedArticles.add(it.getValue(Article::class.java)!!)
                    savedArticles.last().id = it.key!!
                }
            }
        })
    }

    fun stopListener(){
        savedArticlesRef.removeEventListener(listener!!)
    }

    fun addArticleToSavedForLater(article: Article){
        savedArticlesRef.push().setValue(article)
    }

    fun removeArticleFromSavedForLater(article: Article){
        val removedArticle = savedArticles.find { savedArticle -> savedArticle.url == article.url }
        savedArticlesRef.child(removedArticle!!.id).removeValue()
    }
}