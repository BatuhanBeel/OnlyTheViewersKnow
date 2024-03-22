package com.example.quizapp.data

import android.util.Log
import com.example.quizapp.model.SeriesAndResults
import com.example.quizapp.repository.provideSeriesNameList
import kotlinx.coroutines.flow.first

private const val TAG = "SeriesRepository"

class SeriesRepository(
    private val firebaseService: FirebaseService,
    private val roomDao: RoomDao,
    private val omdbAPI: OmdbAPI,
){
    private val seriesNameList = provideSeriesNameList()
    private val auth = firebaseService.provideFirebaseAuth()

    suspend fun updateDatabaseFromServer(): Boolean {
        try {
            seriesNameList.onEach {seriesName ->
                val omdbApiResult = omdbAPI.getSeries(seriesName)
                if (omdbApiResult.isSuccessful) {
                    if (auth.currentUser != null){
                        val firebaseResult = firebaseService.getUserQuizResultsFromFirestore(auth.uid!!, seriesName)
                        if (firebaseResult.isSuccessful){
                            roomDao.upsertSeriesAndResult(SeriesAndResults(auth.uid!!, omdbApiResult.body()!!, firebaseResult.seriesLevels))
                        }
                        else{
                            roomDao.upsertSeriesAndResult(
                                SeriesAndResults(
                                    auth.uid!!,
                                    omdbApiResult.body()!!,
                                    firebaseResult.seriesLevels
                                )
                            )
                        }
                    }
                }
            }
            return true
        } catch (e: Exception) {
            Log.d(TAG, "${e.message}")
            return false
        }
    }

    suspend fun getListFromDatabase(searchQuery: String, sortOrder: SortOrder, levelRequired: Boolean): List<SeriesAndResults>{
        return roomDao.getSeriesAndResults(searchQuery, sortOrder, levelRequired).first()
    }

    suspend fun updateUserResultDatabase(userData: SeriesAndResults){
        roomDao.upsertSeriesAndResult(userData)
    }
}