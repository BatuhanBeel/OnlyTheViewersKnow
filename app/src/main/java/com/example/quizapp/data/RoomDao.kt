package com.example.quizapp.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.quizapp.model.SeriesAndResults
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomDao {

    fun getSeriesAndResults(searchQuery: String, sortOrder: SortOrder, levelRequired: Boolean) =
        when(sortOrder){
            SortOrder.BY_NAME -> getSeriesOrderByName(searchQuery, levelRequired)
            SortOrder.BY_SERIES -> getSeriesOrderBySeries(searchQuery, levelRequired)
            SortOrder.BY_MOVIE -> getSeriesOrderByMovie(searchQuery, levelRequired)
            SortOrder.BY_LEVEL -> getSeriesOrderByLevel(searchQuery, levelRequired)
        }

    @Query("SELECT * FROM series_and_results_table WHERE ((:levelRequired AND maxLevel != 0) OR NOT :levelRequired) AND title LIKE '%' || :searchQuery || '%' ORDER BY title ASC")
    fun getSeriesOrderByName(searchQuery: String, levelRequired: Boolean): Flow<List<SeriesAndResults>>

    @Query("SELECT * FROM series_and_results_table  WHERE ((:levelRequired AND maxLevel != 0) OR NOT :levelRequired) AND title LIKE '%' || :searchQuery || '%' ORDER BY type DESC, title ASC")
    fun getSeriesOrderBySeries(searchQuery: String, levelRequired: Boolean): Flow<List<SeriesAndResults>>

    @Query("SELECT * FROM series_and_results_table WHERE ((:levelRequired AND maxLevel != 0) OR NOT :levelRequired) AND title LIKE '%' || :searchQuery || '%' ORDER BY type ASC, title ASC")
    fun getSeriesOrderByMovie(searchQuery: String, levelRequired: Boolean): Flow<List<SeriesAndResults>>

    @Query("SELECT * FROM series_and_results_table WHERE ((:levelRequired AND maxLevel != 0) OR NOT :levelRequired) AND title LIKE '%' || :searchQuery || '%' ORDER BY maxLevel DESC, title ASC")
    fun getSeriesOrderByLevel(searchQuery: String, levelRequired: Boolean): Flow<List<SeriesAndResults>>

    @Upsert
    suspend fun upsertSeriesAndResult(seriesAndResults: SeriesAndResults)
}