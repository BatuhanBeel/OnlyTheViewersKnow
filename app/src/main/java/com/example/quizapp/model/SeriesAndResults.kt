package com.example.quizapp.model

import androidx.room.Embedded
import androidx.room.Entity
import java.io.Serializable

@Entity(tableName = "series_and_results_table", primaryKeys = ["imdbID", "userUid"])
data class SeriesAndResults(
    val userUid: String,
    @Embedded val series: Series,
    @Embedded val userResults: SeriesLevels
): Serializable

