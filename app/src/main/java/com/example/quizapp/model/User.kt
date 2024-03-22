package com.example.quizapp.model

import android.graphics.Bitmap

data class User(
    val username: String = "",
    val email : String = "",
    val profileImage : Bitmap? = null
)
