package com.example.quizapp.util

import android.content.Context
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.quizapp.R

inline fun SearchView.onQueryTextChanged(crossinline listener: (String) -> Unit) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            listener(newText.orEmpty())
            return true
        }
    })
}

fun ImageView.downloadFromUrl(url: String?, progressDrawable: CircularProgressDrawable){

    val options = RequestOptions
        .placeholderOf(progressDrawable)
        .error(R.drawable.ic_error)

    Glide.with(context)
        .setDefaultRequestOptions(options)
        .load(url)
        //.transform(CircleCrop())
        .into(this)
}

fun placeholderProgressBar(context: Context): CircularProgressDrawable {
    return CircularProgressDrawable(context).apply {
        strokeWidth = 8f
        centerRadius = 40f
        start()
    }
}