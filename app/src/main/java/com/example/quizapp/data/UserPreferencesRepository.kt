package com.example.quizapp.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private const val TAG: String = "UserPreferencesRepo"

enum class SortOrder {
    BY_NAME,
    BY_LEVEL,
    BY_MOVIE,
    BY_SERIES
}

data class UserPreferences(
    val sortOrderSeries: SortOrder,
    val sortOrderQuiz: SortOrder
)

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    private object PreferencesKeys {
        val SORT_ORDER_SERIES = stringPreferencesKey("sort_order_series")
        val SORT_ORDER_QUIZ = stringPreferencesKey("sort_order_quiz")
    }

    var userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            mapUserPreferences(preferences)
        }


    suspend fun updateSortOrder(sortOrder: SortOrder, isQuizUpdated: Boolean) {
        dataStore.edit { preferences ->
            if (isQuizUpdated) {
                preferences[PreferencesKeys.SORT_ORDER_QUIZ] = sortOrder.name
            } else {
                preferences[PreferencesKeys.SORT_ORDER_SERIES] = sortOrder.name
            }
        }
    }

    private fun mapUserPreferences(preferences: Preferences): UserPreferences {
        val sortOrderSeries =
            SortOrder.valueOf(
                preferences[PreferencesKeys.SORT_ORDER_SERIES] ?: SortOrder.BY_NAME.name
            )
        val sortOrderQuiz =
            SortOrder.valueOf(
                preferences[PreferencesKeys.SORT_ORDER_QUIZ] ?: SortOrder.BY_NAME.name
            )
        return UserPreferences(sortOrderSeries, sortOrderQuiz)
    }
}