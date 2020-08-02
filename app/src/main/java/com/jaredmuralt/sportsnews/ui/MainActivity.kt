package com.jaredmuralt.sportsnews.ui

import android.R.attr
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.jaredmuralt.sportsnews.FirebaseUtil
import com.jaredmuralt.sportsnews.R
import com.jaredmuralt.sportsnews.api.NewsAPIEndpoints
import com.jaredmuralt.sportsnews.api.RetrofitUtils
import com.jaredmuralt.sportsnews.model.NewsArticles
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private val RC_SIGN_IN = 400

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            //signed in, load page
            showNewsActivity()
        } else {
            // not signed in
            showLoginActivity()
        }
    }

    private fun showNewsActivity(){
        setSupportActionBar(toolbar)
        toolbar.title = getString(R.string.app_name)
        swipeContainer.setOnRefreshListener {
            getNews()
        }

        FirebaseUtil.startListener()
        getNews()
    }

    private fun showSavedArticlesActivity(){
        val intent = Intent(this, SavedArticlesActivity::class.java)
        ContextCompat.startActivity(this, intent, null)
    }

    private fun showLoginActivity(){
        startActivityForResult(
            // Get an instance of AuthUI based on the default app
            AuthUI.getInstance().createSignInIntentBuilder().setIsSmartLockEnabled(false).build(), RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            // Successfully signed in
            if (resultCode == Activity.RESULT_OK) {
                showNewsActivity()
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Toast.makeText(this, getString(R.string.sign_in_cancelled), Toast.LENGTH_SHORT).show()
                    return
                }
                if (response.error!!.errorCode == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
                    return
                }
                Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                FirebaseUtil.stopListener()
                showLoginActivity()
                true
            }
            R.id.action_saved_for_later -> {
                showSavedArticlesActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getNews() {
        val request = RetrofitUtils.buildService(NewsAPIEndpoints::class.java)
        val call = request.getNewsArticles(getString(R.string.api_key))
        swipeContainer.isRefreshing = true

        call.enqueue(object : Callback<NewsArticles> {
            override fun onResponse(call: Call<NewsArticles>, response: Response<NewsArticles>) {
                swipeContainer.isRefreshing = false
                if (response.isSuccessful) {
                    newsArticleRecyclerView.apply {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(this@MainActivity)
                        adapter =
                            NewsArticleAdapter(
                                response.body()!!.articles, this@MainActivity
                            )
                    }
                } else {
                    //failure response
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.timeout_error_message),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<NewsArticles>, t: Throwable) {
                swipeContainer.isRefreshing = false
                println(t.message)
                Toast.makeText(this@MainActivity, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
}