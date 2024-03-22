package com.example.quizapp.ui.quizzes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.SeriesRepository
import com.example.quizapp.data.SortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.quizapp.data.UserPreferencesRepository
import com.example.quizapp.model.SeriesAndResults
import com.example.quizapp.util.Connectivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "SeriesQuizzesViewModel"

data class SeriesQuizUiState(
    val series: List<SeriesAndResults> = emptyList(),
    val isLoading: Boolean = false,
    val userMessage: String? = null
)

@HiltViewModel
class SeriesQuizzesViewModel @Inject constructor(
    private val connectivity: Connectivity,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val seriesRepository: SeriesRepository
) : ViewModel() {

    init {
        getDataFromSeriesRepository()
    }

    private var _uiState = MutableStateFlow(SeriesQuizUiState(isLoading = true))
    var uiState: StateFlow<SeriesQuizUiState> = _uiState

    val searchQuery = MutableStateFlow("")
    private val userPreferencesFlow = userPreferencesRepository.userPreferencesFlow

    val seriesFlow = combine(
        searchQuery,
        userPreferencesFlow
    ) { query, filterPreferences ->
        Log.d(TAG,"seriesFlow quiz updated.")
        return@combine SeriesQuizUiState(
            series = seriesRepository.getListFromDatabase(
                query,
                filterPreferences.sortOrderSeries,
                levelRequired = true),
            isLoading = false,
        )
    }

    private fun getDataFromSeriesRepository() {
        viewModelScope.launch(Dispatchers.IO) {
            if (connectivity.isNetworkAvailable()){
                val isDatabaseUpdated = seriesRepository.updateDatabaseFromServer()
                if (!isDatabaseUpdated) {
                    Log.d(TAG,"Data update operation failed.")
                }
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        series = seriesRepository.getListFromDatabase(
                            searchQuery.value,
                            userPreferencesFlow.first().sortOrderSeries,
                            levelRequired = true),
                        isLoading = false,
                    )
                }
            }
            else{
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        series = seriesRepository.getListFromDatabase(
                            searchQuery.value,
                            userPreferencesFlow.first().sortOrderSeries,
                            levelRequired = true),
                        isLoading = false,
                        userMessage = "Data could not be updated due to lack of internet."
                    )
                }
            }
        }
    }

    fun updateSortOrder(sortOrder: SortOrder) {
        viewModelScope.launch {
            userPreferencesRepository.updateSortOrder(sortOrder, isQuizUpdated = false)
        }
    }

    fun userMessageShown() {
        _uiState.update { currentUiState ->
            currentUiState.copy(userMessage = null)
        }
    }

    fun updateLoadingState() {
        _uiState.update { currentUiState ->
            currentUiState.copy(isLoading = true)
        }
    }

    fun updateUiState(seriesQuizUiState: SeriesQuizUiState) {
        _uiState.value = seriesQuizUiState
    }

    fun pageRefreshed() {
        getDataFromSeriesRepository()
    }
}