package com.example.quizapp.ui.quiz

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.FirebaseService
import com.example.quizapp.data.SeriesRepository
import com.example.quizapp.model.LevelQuestions
import com.example.quizapp.model.SeriesAndResults
import com.example.quizapp.util.Connectivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    val firebaseService: FirebaseService,
    val connectivity: Connectivity,
    val seriesRepository: SeriesRepository
): ViewModel(){

    private var _currentQuestion = 1
    val currentQuestion
        get() = _currentQuestion

    private var _trueCount = 0
    val trueCount
        get() = _trueCount

    lateinit var questionList: List<LevelQuestions>

    fun checkAnswer(answer: String): Boolean{
        if (questionList[_currentQuestion-1].trueChoice == answer){
            _trueCount++
            return true
        }
        return false
    }

    fun increaseCurrentQuestion(){
        _currentQuestion++
    }

    fun finishButtonClicked(level: Int, userData: SeriesAndResults){
        val data = userData.copy()
        data.userResults.let {
            when(level){
                1 -> if(it.level1 < trueCount) it.level1 = trueCount
                2 -> if(it.level2 < trueCount) it.level2 = trueCount
                3 -> if(it.level3 < trueCount) it.level3 = trueCount
                4 -> if(it.level4 < trueCount) it.level4 = trueCount
                5 -> if(it.level5 < trueCount) it.level5 = trueCount
                6 -> if(it.level6 < trueCount) it.level6 = trueCount
                7 -> if(it.level7 < trueCount) it.level7 = trueCount
                8 -> if(it.level8 < trueCount) it.level8 = trueCount
                9 -> if(it.level9 < trueCount) it.level9 = trueCount
            }
            if (it.maxLevel < level && 0 < trueCount){
                it.maxLevel++
            }
        }
        updateQuizResultDatabase(data)
        updateQuizResultFirebase(data)
    }
    private fun updateQuizResultDatabase(userData: SeriesAndResults){
        viewModelScope.launch(Dispatchers.IO) {
            seriesRepository.updateUserResultDatabase(userData)
        }
    }

    private fun updateQuizResultFirebase(userData: SeriesAndResults){
        viewModelScope.launch(Dispatchers.IO) {
            firebaseService.updateUserQuizResultsFirebase(userData)
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("deneme","cleared.")
    }
}