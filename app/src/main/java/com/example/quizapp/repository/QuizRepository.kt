package com.example.quizapp.repository

import com.example.quizapp.repository.series_questions.attackOnTitanQuestionList
import com.example.quizapp.repository.series_questions.breakingBadQuestionsList

val attackOnTitanQuestions = hashMapOf(
    "1" to attackOnTitanQuestionList[0],
    "2" to attackOnTitanQuestionList[1],
    "3" to attackOnTitanQuestionList[2],
    "4" to attackOnTitanQuestionList[3],
    "5" to attackOnTitanQuestionList[4],
    "6" to attackOnTitanQuestionList[5],
    "7" to attackOnTitanQuestionList[6],
    "8" to attackOnTitanQuestionList[7],
    "9" to attackOnTitanQuestionList[8],
)
val breakingBadQuestions = hashMapOf(
    "1" to breakingBadQuestionsList[0],
    "2" to breakingBadQuestionsList[1],
    "3" to breakingBadQuestionsList[2],
    "4" to breakingBadQuestionsList[3],
    "5" to breakingBadQuestionsList[4],
    "6" to breakingBadQuestionsList[5],
    "7" to breakingBadQuestionsList[6],
    "8" to breakingBadQuestionsList[7],
    "9" to breakingBadQuestionsList[8],
)
val allQuestions = hashMapOf(
    "Attack on Titan" to attackOnTitanQuestions,
    "Breaking Bad" to breakingBadQuestions
)

