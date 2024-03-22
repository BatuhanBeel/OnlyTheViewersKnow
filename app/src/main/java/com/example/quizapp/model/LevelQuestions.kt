package com.example.quizapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LevelQuestions(
    val question: String,
    val choiceA: String,
    val choiceB: String,
    val choiceC: String,
    val choiceD: String,
    val trueChoice: String
): Parcelable
